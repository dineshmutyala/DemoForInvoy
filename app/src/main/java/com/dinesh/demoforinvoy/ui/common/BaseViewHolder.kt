package com.dinesh.demoforinvoy.ui.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dinesh.demoforinvoy.core.events.EventListener

open class BaseViewHolder<T: BasePresentationModel>(view: View): RecyclerView.ViewHolder(view) {

    private var listener: EventListener? = null

    private var data: T? = null

    init {
        view.setOnClickListener { data?.let { data -> listener?.onEvent(ListItemClickedEvent(data.id, adapterPosition)) } }
    }

    open fun bindData(data: T) {
        this.data = data
    }

    fun setEventListener(eventListener: EventListener?) {
        listener = eventListener
    }
}