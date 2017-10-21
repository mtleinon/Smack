package com.example.mikat.smack.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.mikat.smack.R
import com.example.mikat.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginLoginBtn.setOnClickListener {
            val email = loginEmailText.text.toString()
            val password = loginPasswordText.text.toString()
            AuthService.loginUser(this, email, password){
                loginSuccess ->
                if(loginSuccess) {
                    AuthService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess) {
                            finish()
                        }
                    }
                }
            }
        }

        loginCreateUserBtn.setOnClickListener {
            val createUSerIntent = Intent(this, CreateUserActivity::class.java )
            startActivity(createUSerIntent)
            finish()
        }
    }
}
