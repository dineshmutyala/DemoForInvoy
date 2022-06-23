package com.dinesh.demoforinvoy.ui.common

import com.dinesh.demoforinvoy.databinding.ItemLoadMoreBinding

class LoadMoreViewHolder<T: BasePresentationModel>(binding: ItemLoadMoreBinding): BaseViewHolder<T>(binding.root) {
    override fun bindData(data: T) = Unit
}
