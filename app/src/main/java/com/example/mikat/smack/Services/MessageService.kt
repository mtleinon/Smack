package com.example.mikat.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.mikat.smack.Model.Channel
import com.example.mikat.smack.Utilities.URL_GET_CHANNELS
import org.json.JSONException


/**
 * Created by mikat on 21.10.2017.
 */
object MessageService {
    val channels = ArrayList<Channel>()

    fun getChannels(context:Context, complete: (Boolean) -> Unit) {

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
                return hashMapOf("Authorization" to "Bearer ${AuthService.authToken}")
            }

        }
        Volley.newRequestQueue(context).add(channelsRequest)
    }

}