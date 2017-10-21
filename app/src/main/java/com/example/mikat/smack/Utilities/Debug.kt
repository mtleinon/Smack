package com.example.mikat.smack.Utilities

import android.util.Log

/**
 * Created by mikat on 20.10.2017.
 */
fun TEST (msg: String = " ") {
    val st = Throwable().getStackTrace()
    var i = 0
    while (st[i].fileName == "Debug.kt") {
            i++
    }
    Log.d ( "TEST ${st[i].className.substringAfterLast('.')
            .replace("$1", "")
            .replace("$"," ")}" +
            " ${st[i].methodName} ${st[i].lineNumber}/",
            msg)
}







