package com.example.mikat.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.mikat.smack.Controller.App
import com.example.mikat.smack.Model.Channel
import com.example.mikat.smack.Model.Message
import com.example.mikat.smack.Utilities.URL_GET_CHANNELS
import com.example.mikat.smack.Utilities.URL_GET_MESSAGES
import org.json.JSONException


/**
 * Created by mikat on 21.10.2017.
 */
object MessageService {
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit) {

        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null,
                Response.Listener { response ->
                    channels.clear()
                    try {
                        for (x in 0 until response.length()) {
                            val channel = response.getJSONObject(x)
                            channels.add(
                                    Channel(channel.getString("name"),
                                    channel.getString("description"),
                                    channel.getString("_id")))
                        }
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSON", "EXC:" + e.localizedMessage)
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not retrieve channels")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Authorization" to "Bearer ${App.prefs.authToken}")
            }

        }
        App.prefs.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {
        var url = "$URL_GET_MESSAGES$channelId"

        val messagesRequest = object : JsonArrayRequest(Method.GET, url, null,
                Response.Listener { response ->
                    clearMessages()
                    try {
                        for (x in 0 until response.length()) {
                            val message = response.getJSONObject(x)
                            messages.add(Message(
                                    message.getString("messageBody"),
                                    message.getString("userName"),
                                    message.getString("channelId"),
                                    message.getString("userAvatar"),
                                    message.getString("userAvatarColor"),
                                    message.getString("_id"),
                                    message.getString("timeStamp")))

                        }
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSON", "EXC:" + e.localizedMessage)
                        complete(false)
                    }
                },
                Response.ErrorListener {
                    Log.d("ERROR", "Could not retrieve messages")
                    complete(false)
                }
                ) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Authorization" to "Bearer ${App.prefs.authToken}")
            }
        }
        App.prefs.requestQueue.add(messagesRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }

}