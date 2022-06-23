package com.dinesh.demoforinvoy.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.dinesh.demoforinvoy.core.events.Event
import com.dinesh.demoforinvoy.core.events.EventListener
import com.dinesh.demoforinvoy.databinding.ListItemConversationBinding
import com.dinesh.demoforinvoy.ui.common.BaseAdapter
import com.dinesh.demoforinvoy.ui.common.BaseViewHolder
import com.dinesh.demoforinvoy.ui.common.ListItemClickedEvent

class ConversationAdapter(private var eventListener: EventListener?) : BaseAdapter<ConversationPresentationModel>(), EventListener {

    fun updateData(newData: List<ConversationPresentationModel>) {
        triggerDiffCallback(listData + newData)
    }

    private fun triggerDiffCallback(newData: List<ConversationPresentationModel>) {
        val diffResult = DiffUtil.calculateDiff(Companion.DiffCallback(newData, this.listData))
        diffResult.dispatchUpdatesTo(this)
        listData.clear()
        listData.addAll(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ConversationPresentationModel> {
        return ConversationViewHolder(
            ListItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ).also {
            it.setEventListener(this)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onEvent(event: Event) {
        when(event) {
            is ListItemClickedEvent -> {
                eventListener?.onEvent(ViewConversationEvent(forUserWithId = listData[event.position].userId))
            }
        }
    }

    fun clearReferences() {
        eventListener = null
    }
}