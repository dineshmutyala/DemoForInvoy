package com.dinesh.demoforinvoy.ui.chat

import android.view.View
import com.bumptech.glide.Glide
import com.dinesh.demoforinvoy.databinding.ListItemChatMessageReceivedBinding

class ChatMessageReceivedViewHolder(
    private val binding: ListItemChatMessageReceivedBinding
): ChatMessageBaseViewHolder(binding.root) {
    override fun bindData(data: ChatMessagePresentationModel) {
        super.bindData(data)
        binding.messageTimestamp.text = data.sentOn
        if (data.loadAsImage) {
            binding.message.visibility = View.GONE
            binding.image.visibility = View.VISIBLE
            Glide
                .with(binding.root.context)
                .load(data.message)
                .centerCrop()
                .into(binding.image)
        } else {
            binding.message.visibility = View.VISIBLE
            binding.image.visibility = View.GONE
            binding.message.text = data.message
        }
    }
}