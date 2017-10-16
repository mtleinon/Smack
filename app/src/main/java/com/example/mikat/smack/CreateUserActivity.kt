package com.example.mikat.smack

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_create_user.*

class CreateUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        createAvatarImageView.setOnClickListener {
        }
        createBackgroundColorBtn.setOnClickListener{
        }
        createUserBtn.setOnClickListener{
        }



    }
}
