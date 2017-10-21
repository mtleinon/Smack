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

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            val  channelName = args[0] as String
            val  channelDescription = args[1] as String
            val  channelId = args[2] as String

            val newChannel = Channel (channelName, channelDescription, channelId)
            MessageService.channels.add(newChannel)
            //println("${newChannel.name} ${newChannel.description} ${newChannel.id}")
            TEST("${newChannel.name} ${newChannel.description} ${newChannel.id}")
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

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        loginBtnNav.setOnClickListener {
            if (AuthService.isLoggedIn) {
                UserDataService.logout()
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
            if (!AuthService.isLoggedIn) {
                return@setOnClickListener
            }
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                    .setPositiveButton("Add") {
                        dialogInterface, i ->
                        val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                        val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)
                        val channelName = nameTextField.text.toString()
                        val channelDesc = descTextField.text.toString()

                        socket.emit("newChannel", channelName, channelDesc)
                    }
                    .setNegativeButton("Cancel") {
                        dialogInterface, i ->
                    }
                    .show()
        }

        sendMessageBtn.setOnClickListener {
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))

    }

    private val userDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEMailNavHeader.text = UserDataService.email
                userImageNavHeader.setImageResource(resources.getIdentifier(
                        UserDataService.avatarName, "drawable", packageName))
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(
                        UserDataService.avatarColor))
                loginBtnNav.text = "Logout"
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
