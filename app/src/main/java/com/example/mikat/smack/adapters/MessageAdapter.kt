package com.example.mikat.smack.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.mikat.smack.Model.Message
import com.example.mikat.smack.R
import com.example.mikat.smack.Services.UserDataService

/**
 * Created by mikat on 22.10.2017.
 */
class MessageAdapter(val context: Context, val messages: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>(){
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bingMessage(context, messages[position])
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder{
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false))
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        val userImage = itemView?.findViewById<ImageView>(R.id.messageUserImage)
        val timestamp = itemView?.findViewById<TextView>(R.id.timestampTxt)
        val userName = itemView?.findViewById<TextView>(R.id.messageUserNameTxt)
        val messageBody = itemView?.findViewById<TextView>(R.id.messageBodyTxt)

        fun bingMessage(context: Context, message: Message) {
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            userName?.text = message.userName
            timestamp?.text = message.timeStamp
            messageBody?.text = message.message
        }
    }
}