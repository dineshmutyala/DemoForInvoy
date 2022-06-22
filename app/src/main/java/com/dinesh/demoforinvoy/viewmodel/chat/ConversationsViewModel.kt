package com.dinesh.demoforinvoy.viewmodel.chat

import com.dinesh.demoforinvoy.repositories.ChatRepository
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import javax.inject.Inject

class ConversationsViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): BaseViewModel() {
    override fun clearReferences() {}
}