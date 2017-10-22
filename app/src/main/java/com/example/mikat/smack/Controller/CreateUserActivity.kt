package com.example.mikat.smack.Controller

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.example.mikat.smack.R
import com.example.mikat.smack.Services.AuthService
import com.example.mikat.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        createSpinner.visibility = View.INVISIBLE

        createAvatarImageView.setOnClickListener {
            val random = Random()
            val color = random.nextInt(2)
            val avatar = random.nextInt(28)

            if (color == 0) {
                userAvatar = "light$avatar"
            } else {
                userAvatar = "dark$avatar"
            }
            createAvatarImageView.setImageResource(
                    resources.getIdentifier(userAvatar, "drawable", packageName))
        }

        createBackgroundColorBtn.setOnClickListener{
            val random = Random()
            val r = random.nextInt(255)
            val g = random.nextInt(255)
            val b = random.nextInt(255)

            createAvatarImageView.setBackgroundColor(Color.rgb(r, g, b))

            avatarColor = "[${r.toDouble() / 255}, ${g.toDouble() / 255}, ${b.toDouble() / 255}, 1]"
            println(avatarColor)
        }

        createUserBtn.setOnClickListener{

            enableSpinner(true)

            val userName = createUserNameText.text.toString()
            val email = createEmailText.text.toString()
            val password = createPasswordText.text.toString()

            if (userName.isEmpty() && email.isEmpty() && password.isEmpty()) {
                Toast.makeText(this,
                        "Make sure user name, email, and password are filled in",
                        Toast.LENGTH_LONG).show()
                enableSpinner(false)
                return@setOnClickListener
            }

            AuthService.registerUser(email, password){ registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(email, password ) {
                        loginSuccess ->
                        if (loginSuccess){
                            AuthService.createUser(userName, email,
                                    userAvatar,
                                    avatarColor) {
                                createSuccess ->
                                if(createSuccess) {
                                    println("createUserBtn SUCCESS")
                                    LocalBroadcastManager.getInstance(this)
                                            .sendBroadcast(Intent(BROADCAST_USER_DATA_CHANGE))
                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast("User creation")
                                }
                            }
                        } else {
                            errorToast("Login failed")
                        }
                    }
                } else {
                    errorToast("User registering failed")
                }
            }
        }
    }

    fun errorToast(reason: String){
        Toast.makeText(this, "$reason, Please try again", Toast.LENGTH_LONG)
                .show()
        enableSpinner(false)
    }
    fun enableSpinner(enable: Boolean) {
        if (enable) {
            createSpinner.visibility = View.VISIBLE
        } else {
            createSpinner.visibility = View.INVISIBLE
        }
        createUserBtn.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
        createBackgroundColorBtn.isEnabled = !enable
    }
}
