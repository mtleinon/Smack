package com.example.mikat.smack

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginLoginBtn.setOnClickListener {

        }

        loginCreateUserBtn.setOnClickListener {
            val createUSerIntent = Intent(this, CreateUserActivity::class.java )
            startActivity(createUSerIntent)
        }
    }
}
