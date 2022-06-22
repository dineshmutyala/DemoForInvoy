package com.dinesh.demoforinvoy.ui.chat

import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.dinesh.demoforinvoy.datamodels.message.Message
import java.util.*

data class ChatMessagePresentationModel(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val sentOn: String,
    val isSentMessage: Boolean = true,
    val isLoading: Boolean = false,
    val sendFailed: Boolean = false,
    val loadAsImage: Boolean = false
) {
    companion object {
        val EMPTY = ChatMessagePresentationModel("", "", "")

        fun fromMessage(message: Message) = ChatMessagePresentationModel(
            id = message.id,
            message = message.message,
            sentOn = SynchronizedTimeUtils.getFormattedTimeWithDateNoYearNoSec(message.sentOnDate, TimeZone.getDefault()),
            isSentMessage = message.isSentMessage,
            // TODO: How about some intelligent Regex to match current user's images folder?
            loadAsImage = message.message.startsWith("https://firebasestorage.googleapis.com/")
        )
    }
}