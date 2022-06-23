package com.dinesh.demoforinvoy.ui.viewlogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dinesh.demoforinvoy.databinding.ItemLoadMoreBinding
import com.dinesh.demoforinvoy.databinding.ListItemViewLogBinding
import com.dinesh.demoforinvoy.ui.common.BaseAdapter
import com.dinesh.demoforinvoy.ui.common.BaseViewHolder
import com.dinesh.demoforinvoy.ui.common.LoadMoreViewHolder

class ViewLogsAdapter: BaseAdapter<WeightLogPresentationModel>() {

    companion object {
        const val VIEW_TYPE_DEFAULT = 0
        const val VIEW_TYPE_LOADING = 1

    }

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
        val diffResult = DiffUtil.calculateDiff(BaseAdapter.Companion.DiffCallback(newData, this.listData))
        diffResult.dispatchUpdatesTo(this)
        listData.clear()
        listData.addAll(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<WeightLogPresentationModel> {
        return when (viewType) {
            VIEW_TYPE_LOADING -> LoadMoreViewHolder(
                ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ViewLogListItemViewHolder(
                ListItemViewLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<WeightLogPresentationModel>, position: Int) {
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