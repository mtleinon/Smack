package com.example.mikat.smack.Model

/**
 * Created by mikat on 21.10.2017.
 */
class Channel(val name: String, val description: String, val id: String) {
    override fun toString(): String {
        return "#$name"
    }
}