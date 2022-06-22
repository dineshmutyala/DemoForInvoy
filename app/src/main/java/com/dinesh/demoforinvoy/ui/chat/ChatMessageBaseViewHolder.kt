package com.dinesh.demoforinvoy.ui.chat

import android.view.View
import com.dinesh.demoforinvoy.ui.common.BaseViewHolder

abstract class ChatMessageBaseViewHolder(view: View): BaseViewHolder<ChatMessagePresentationModel>(view) {
    override fun bindData(data: ChatMessagePresentationModel) {
        itemView.alpha = if (data.isLoading) 0.5f else 1f
    }
}