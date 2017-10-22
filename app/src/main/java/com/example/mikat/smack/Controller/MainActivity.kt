package com.example.mikat.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.mikat.smack.R
import com.example.mikat.smack.Services.AuthService
import com.example.mikat.smack.Services.MessageService
import com.example.mikat.smack.Services.UserDataService
import com.example.mikat.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import com.example.mikat.smack.Utilities.SOCKET_URL
import com.example.mikat.smack.Utilities.TEST
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import com.example.mikat.smack.Model.Channel
import com.example.mikat.smack.Model.Message
import com.example.mikat.smack.adapters.MessageAdapter

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    lateinit var messageAdapter: MessageAdapter


    var selectedChannel : Channel? = null

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,
                MessageService.channels)
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this, MessageService.messages)
        messageListView.adapter = messageAdapter
        messageListView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }

    private val onNewChannel = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {
            runOnUiThread {
                MessageService.channels.add(
                        Channel(
                                args[0] as String,
                                args[1] as String,
                                args[2] as String)
                )
                channelAdapter.notifyDataSetChanged()
                TEST("${MessageService.channels.last().name}")
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {
            runOnUiThread {
                if (args[2] as String == selectedChannel?.id) {
                    MessageService.messages.add(
                            Message(
                                    args[0] as String,
                                    args[3] as String,
                                    args[2] as String,
                                    args[4] as String,
                                    args[5] as String,
                                    args[6] as String,
                                    args[7] as String)
                    )
                    TEST(MessageService.messages.last().toString())
                    messageAdapter.notifyDataSetChanged()
                    messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage )
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setupAdapters()

        LocalBroadcastManager.getInstance(this).registerReceiver(
                userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))

        if (App.prefs.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }

        loginBtnNav.setOnClickListener {
            if (App.prefs.isLoggedIn) {
                UserDataService.logout()
                channelAdapter.notifyDataSetChanged()
                messageAdapter.notifyDataSetChanged()
                userNameNavHeader.text = ""
                userEMailNavHeader.text = ""
                loginBtnNav.text = "LOGIN"
                userImageNavHeader.setImageResource(R.drawable.profiledefault)
                userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        addChannelBtn.setOnClickListener {
            if (!App.prefs.isLoggedIn) {
                return@setOnClickListener
            }
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                    .setPositiveButton("Add") {
                        _, i ->
                        val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                        val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)

                        socket.emit("newChannel",
                                nameTextField.text.toString(),
                                descTextField.text.toString())
                    }
                    .setNegativeButton("Cancel") {
                        dialogInterface, i ->
                    }
                    .show()
        }

        sendMessageBtn.setOnClickListener {
            if (App.prefs.isLoggedIn && messageTextField.text.isNotEmpty() && selectedChannel != null) {
                socket.emit("newMessage",
                        messageTextField.text.toString(),
                        UserDataService.id,
                        selectedChannel!!.id,
                        UserDataService.name,
                        UserDataService.avatarName,
                        UserDataService.avatarColor)
                messageTextField.text.clear()
            }

        }

        channel_list.setOnItemClickListener { _, _, i, _ ->
            selectedChannel = MessageService.channels[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateMainChannelName()
        }
    }

    private val userDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.prefs.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEMailNavHeader.text = UserDataService.email
                userImageNavHeader.setImageResource(resources.getIdentifier(
                        UserDataService.avatarName, "drawable", packageName))
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(
                        UserDataService.avatarColor))
                loginBtnNav.text = "Logout"

                MessageService.getChannels {complete ->
                    if (complete) {
                        if (MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels.first()
                            channelAdapter.notifyDataSetChanged()
                            updateMainChannelName()
                        }
                    }
                }
            }
        }
    }

    fun updateMainChannelName(){
        mainChannelName.text = "#${selectedChannel?.name}"

        MessageService.getMessages(selectedChannel!!.id) { complete ->
            if(complete) {
                messageAdapter.notifyDataSetChanged()
                if (messageAdapter.itemCount > 0) {
                    messageListView.smoothScrollToPosition(messageAdapter.itemCount -1)
                }
            }
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}
