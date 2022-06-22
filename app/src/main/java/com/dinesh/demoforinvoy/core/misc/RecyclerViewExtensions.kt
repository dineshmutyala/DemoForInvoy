package com.dinesh.demoforinvoy.core.misc

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.isValidToCheckForLastItem(newState: Int) =
    newState == RecyclerView.SCROLL_STATE_IDLE && safeItemCount > 0

fun RecyclerView.isLastItemShowing(): Boolean {
    val linearLayoutManager = (layoutManager as? LinearLayoutManager).guardAgainstNull { return false }
    val lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition()
    return lastVisiblePosition >= safeItemCount - 1
}

private val RecyclerView.safeItemCount get() = adapter?.itemCount ?: 0

