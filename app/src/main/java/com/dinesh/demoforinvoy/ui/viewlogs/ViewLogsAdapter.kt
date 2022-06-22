package com.dinesh.demoforinvoy.ui.viewlogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dinesh.demoforinvoy.databinding.ItemLoadMoreBinding
import com.dinesh.demoforinvoy.databinding.ListItemViewLogBinding
import com.dinesh.demoforinvoy.ui.common.LoadMoreViewHolder

class ViewLogsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        class DiffCallback(
            private val newList: List<WeightLogPresentationModel>,
            private val oldList: List<WeightLogPresentationModel>
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

        const val VIEW_TYPE_DEFAULT = 0
        const val VIEW_TYPE_LOADING = 1

    }

    private val listData = mutableListOf<WeightLogPresentationModel>()
    private var isLoadingMore = false
    private var canLoadMore = true

    fun isWaitingForMoreResults() = isLoadingMore

    fun notifyEndOfResults() {
        canLoadMore = false
        triggerDiffCallback(listData.dropLastWhile { it == WeightLogPresentationModel.EMPTY })
    }

    fun loadingMore() {
        if (isLoadingMore || !canLoadMore) return
        isLoadingMore = true
        triggerDiffCallback(listData.dropLastWhile { it == WeightLogPresentationModel.EMPTY })
        triggerDiffCallback(listData + WeightLogPresentationModel.EMPTY)
    }

    fun updateData(newData: List<WeightLogPresentationModel>) {
        isLoadingMore = false
        triggerDiffCallback(listData.dropLastWhile { it == WeightLogPresentationModel.EMPTY })
        triggerDiffCallback(listData + newData)
    }

    private fun triggerDiffCallback(newData: List<WeightLogPresentationModel>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(newData, this.listData))
        diffResult.dispatchUpdatesTo(this)
        listData.clear()
        listData.addAll(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> LoadMoreViewHolder<WeightLogPresentationModel>(
                ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ViewLogListItemViewHolder(
                ListItemViewLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewLogListItemViewHolder -> holder.bindData(listData[position])
        }
    }

    override fun getItemCount(): Int = listData.size

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_DEFAULT
    }

    fun clearOut() {
        listData.clear()
        isLoadingMore = false
        canLoadMore = true
//        triggerDiffCallback(listData)
    }
}