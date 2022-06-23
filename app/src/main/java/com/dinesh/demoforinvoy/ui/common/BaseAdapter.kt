package com.dinesh.demoforinvoy.ui.common

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T: BasePresentationModel>(): RecyclerView.Adapter<BaseViewHolder<T>>() {
    companion object {
        class DiffCallback(
            private val newList: List<BasePresentationModel>,
            private val oldList: List<BasePresentationModel>
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
    }

    protected val listData: MutableList<T> = mutableListOf()

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bindData(listData[position])
    }
}