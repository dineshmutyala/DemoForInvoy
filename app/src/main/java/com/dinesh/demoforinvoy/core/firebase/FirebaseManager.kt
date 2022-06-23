package com.dinesh.demoforinvoy.core.firebase

import com.dinesh.demoforinvoy.core.firebase.messaging.MessagingManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseManager @Inject constructor(
    private val messagingManager: MessagingManager,
    private val accountManager: AccountManager
) {
    fun initialize() {
        messagingManager.observeToken {
            accountManager.updateFCMToken(it)
        }
    }
}