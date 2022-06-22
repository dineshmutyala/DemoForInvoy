package com.dinesh.demoforinvoy.core.firebase.messaging

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import timber.log.Timber
import javax.inject.Inject

class MessagingManager @Inject constructor() {

    private val messaging: FirebaseMessaging by lazy { Firebase.messaging }

    fun observeToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w("Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Timber.d("FCM Token: $token")
        }
    }
}