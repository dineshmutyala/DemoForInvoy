package com.dinesh.demoforinvoy.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dinesh.demoforinvoy.databinding.ItemLoadMoreBinding
import com.dinesh.demoforinvoy.databinding.ListItemChatMessageReceivedBinding
import com.dinesh.demoforinvoy.databinding.ListItemChatMessageSentBinding
import com.dinesh.demoforinvoy.ui.common.BaseViewHolder
import com.dinesh.demoforinvoy.ui.common.LoadMoreViewHolder

class ChatAdapter: RecyclerView.Adapter<BaseViewHolder<ChatMessagePresentationModel>>() {

    companion object {
        class DiffCallback(
            private val newList: List<ChatMessagePresentationModel>,
            private val oldList: List<ChatMessagePresentationModel>
        ) : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].id == newList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }

        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2

    }

    private val listData = mutableListOf<ChatMessagePresentationModel>()
    private var isLoadingMore = false
    private var canLoadMore = true

    fun isWaitingForMoreResults() = isLoadingMore

    fun notifyEndOfResults() {
        canLoadMore = false
        triggerDiffCallback(listData.dropLastWhile { it == ChatMessagePresentationModel.EMPTY })
    }

    fun loadingMore() {
        if (isLoadingMore || !canLoadMore) return
        isLoadingMore = true
        triggerDiffCallback(listData.dropLastWhile { it == ChatMessagePresentationModel.EMPTY })
//        triggerDiffCallback(listData + ChatMessagePresentationModel.EMPTY)
    }

    fun updateData(newData: List<ChatMessagePresentationModel>, forPage: Int = -1, addToTop: Boolean = false) {
        isLoadingMore = false
        triggerDiffCallback(listData.dropLastWhile { it == ChatMessagePresentationModel.EMPTY })
        triggerDiffCallback(if (addToTop) newData + listData else listData + newData)
    }

    fun updateMessage(data: Pair<String, ChatMessagePresentationModel>) {
        listData.indexOfFirst { it.id == data.first }.let {
            when(it) {
                -1 -> updateData(listOf(data.second), addToTop = true)
                else -> {
                    listData.toCollection(mutableListOf()).apply {
                        removeAt(it)
                        add(it, data.second)
                    }.also { updatedList ->
                        triggerDiffCallback(updatedList)
                    }
                }
            }
        }
    }

    private fun triggerDiffCallback(newData: List<ChatMessagePresentationModel>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(newData, this.listData))
        diffResult.dispatchUpdatesTo(this)
        listData.clear()
        listData.addAll(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ChatMessagePresentationModel> {
        return when (viewType) {
            VIEW_TYPE_LOADING -> LoadMoreViewHolder(
                ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            VIEW_TYPE_SENT -> ChatMessageSentViewHolder(
                ListItemChatMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ChatMessageReceivedViewHolder(
                ListItemChatMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ChatMessagePresentationModel>, position: Int) {
        when(holder) {
            is ChatMessageBaseViewHolder -> holder.bindData(listData[position])
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (listData[position].isSentMessage) {
            true -> VIEW_TYPE_SENT
            else -> VIEW_TYPE_RECEIVED
        }
    }

}