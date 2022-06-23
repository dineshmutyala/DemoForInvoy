package com.dinesh.demoforinvoy.core.firebase.messaging

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class FCMService: FirebaseMessagingService() {

    private val localBroadcastManager: LocalBroadcastManager by lazy { LocalBroadcastManager.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        localBroadcastManager
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM Token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (Firebase.auth.currentUser?.uid == message.data["sendTo"]) sendNotification(message)

        Timber.d("FCM Message: ${message.data}")
    }

    private fun sendNotification(message: RemoteMessage) {
        localBroadcastManager.sendBroadcast(Intent("New Message").apply {
            putExtra("message", message.data["body"])
            putExtra("messageId", message.data["messageId"])
            message.data["sentOn"]?.let { putExtra("sentOn", it.toLong()) }
        })
    }
}