package com.example.mikat.smack.Controller

import android.app.Application
import com.example.mikat.smack.Utilities.SharedPrefs

/**
 * Created by mikat on 22.10.2017.
 */
class App : Application() {

    companion object {
        lateinit var prefs: SharedPrefs
    }
    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}