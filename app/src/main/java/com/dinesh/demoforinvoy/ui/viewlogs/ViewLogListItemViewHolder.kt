package com.dinesh.demoforinvoy.ui.viewlogs

import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.databinding.ListItemViewLogBinding
import com.dinesh.demoforinvoy.ui.common.BaseViewHolder

internal class ViewLogListItemViewHolder(
    private val binding: ListItemViewLogBinding
): BaseViewHolder<WeightLogPresentationModel>(binding.root) {

    override fun bindData(data: WeightLogPresentationModel) {
        super.bindData(data)
        binding.weight.text = binding.weight.context.getString(R.string.x_lbs, data.weight)
        binding.weightOn.text = binding.weight.context.getString(R.string.on_y, data.weightOn)
    }

}