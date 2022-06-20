package com.dinesh.demoforinvoy.ui.viewlogs

import androidx.recyclerview.widget.RecyclerView
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.databinding.ListItemViewLogBinding

class ViewLogListItemViewHolder(
    private val binding: ListItemViewLogBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bindData(data: WeightLogPresentationModel) {
        binding.weight.text = binding.weight.context.getString(R.string.x_lbs, data.weight)
        binding.weightOn.text = binding.weight.context.getString(R.string.on_y, data.weightOn)
    }

}