package com.dinesh.demoforinvoy.repositories

import com.dinesh.demoforinvoy.core.firestore.FireStoreManager
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatManager: FireStoreManager
) {
    init {
        chatManager.initializeChat(
            onSuccess = {},
            onFailure = {}
        )
    }
}