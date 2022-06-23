package com.dinesh.demoforinvoy.ui.chat

import com.dinesh.demoforinvoy.datamodels.user.User
import com.dinesh.demoforinvoy.ui.common.BasePresentationModel
import java.util.*

data class ConversationPresentationModel(
    override val id: String = UUID.randomUUID().toString(),
    val userName: String,
    val userId: String,
    val fcmToken: String
): BasePresentationModel {
    companion object {
        val EMPTY = ConversationPresentationModel("", "", "", "")

        fun fromUser(user: User) = ConversationPresentationModel(
            userName = user.name,
            userId = user.userId,
            fcmToken = user.token
        )
    }
}