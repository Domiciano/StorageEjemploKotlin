package edu.co.icesi.firestoreejemplokotlin.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import edu.co.icesi.firestoreejemplokotlin.models.Message
import edu.co.icesi.firestoreejemplokotlin.util.NotificationUtil
import org.json.JSONObject

class FCMService : FirebaseMessagingService() {


    override fun onMessageReceived(message: RemoteMessage) {
        val obj = JSONObject(message.data as Map<*, *>)
        val json = obj.toString()
        val message = Gson().fromJson(json, Message::class.java)
        NotificationUtil.showNotification(this, "Mensaje nuevo", message.message)
    }
}