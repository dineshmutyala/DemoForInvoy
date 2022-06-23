package com.dinesh.demoforinvoy.core.firebase.messaging

import com.dinesh.demoforinvoy.datamodels.cloudmessaging.FCMData
import com.dinesh.demoforinvoy.datamodels.cloudmessaging.FCMResponse
import com.dinesh.demoforinvoy.datamodels.cloudmessaging.Sender
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class MessagingManager @Inject constructor(
    private val messagingService: MessagingService
) {

    private val messaging: FirebaseMessaging by lazy { Firebase.messaging }

    fun observeToken(onSuccess: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w("Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Timber.d("FCM Token: $token")
            onSuccess(token)
        }
    }

    fun postMessage(
        sentBy: String,
        sendTo: String,
        message: String,
        messageId: String,
        sentOn: Long,
        toToken: String
    ) {
        messagingService.sendNotification(
            Sender(
                data = FCMData(
                    sentBy = sentBy,
                    sendTo = sendTo,
                    body = message,
                    title = "New Message",
                    messageId = messageId,
                    sentOn = sentOn
                ),
                to = toToken
            )
        ).enqueue(object : Callback<FCMResponse> {
            override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {
                Timber.d("Dinesh: Posted notification: ${response.body()} \n ${response.errorBody()}")
            }

            override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
                Timber.d("Dinesh: Failed to post notification: $t")
            }
        })
    }
}