package com.example.mikat.smack.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.mikat.smack.R
import com.example.mikat.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

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

            avatarColor = "[${r.toDouble() / 255}, ${r.toDouble() / 255}, ${r.toDouble() / 255}, 1]"
            println(avatarColor)
        }

        createUserBtn.setOnClickListener{
            val email = createEmailText.text.toString()
            val password = createPasswordText.text.toString()
            AuthService.registerUser(this,
                    email,
                    password){ registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(this, email, password ) {
                        loginSuccess ->
                        println("createUserBtn: ${AuthService.userEmail}")
                        println("createUserBtn: ${AuthService.authToken}")
                        println("createUserBtn: ${AuthService.isLoggedIn}")
                        if (loginSuccess){
                            println("Login succeeded with $email")
                        }
                    }
                }
            }
        }
    }
}
