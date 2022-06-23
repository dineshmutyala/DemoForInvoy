package com.dinesh.demoforinvoy.ui.chat

import com.dinesh.demoforinvoy.databinding.ListItemConversationBinding
import com.dinesh.demoforinvoy.ui.common.BaseViewHolder

class ConversationViewHolder(
    private val binding: ListItemConversationBinding
): BaseViewHolder<ConversationPresentationModel>(binding.root) {
    override fun bindData(data: ConversationPresentationModel) {
        super.bindData(data)
        binding.userName.text = data.userName
    }
}