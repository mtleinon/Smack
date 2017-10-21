package com.example.mikat.smack.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.mikat.smack.R
import com.example.mikat.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE

        loginLoginBtn.setOnClickListener {
            val email = loginEmailText.text.toString()
            val password = loginPasswordText.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,"Please fill in both email and password",
                        Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            enableSpinner(true)
            AuthService.loginUser(this, email, password)
            {
                loginSuccess ->
                if(loginSuccess) {
                    AuthService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess) {
                            enableSpinner(false)
                            finish()
                        } else {
                            errorToast("User could not be found")
                        }
                    }
                } else {
                    errorToast("Login failed")
                }
            }
        }

        loginCreateUserBtn.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java ))
            finish()
        }
    }
/*
    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
*/
    fun errorToast(reason: String){
        Toast.makeText(this, "$reason, Please try again", Toast.LENGTH_LONG)
                .show()
        enableSpinner(false)
    }
    fun enableSpinner(enable: Boolean) {
        if (enable) {
            loginSpinner.visibility = View.VISIBLE
        } else {
            loginSpinner.visibility = View.INVISIBLE
        }
        loginLoginBtn.isEnabled = !enable
        loginCreateUserBtn.isEnabled = !enable
    }
}
