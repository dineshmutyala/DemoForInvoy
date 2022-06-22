package com.dinesh.demoforinvoy.ui.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder<T>(view: View): RecyclerView.ViewHolder(view) {
    open fun bindData(data: T) = Unit
}