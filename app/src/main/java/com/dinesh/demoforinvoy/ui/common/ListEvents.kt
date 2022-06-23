package com.dinesh.demoforinvoy.ui.common

import com.dinesh.demoforinvoy.core.events.Event

sealed class ListEvents: Event

data class ListItemClickedEvent(
    val id: String,
    val position: Int
): ListEvents()