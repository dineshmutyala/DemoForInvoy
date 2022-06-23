package com.dinesh.demoforinvoy.ui.chat

import com.dinesh.demoforinvoy.core.events.Event

sealed class ConversationListEvents: Event

data class ViewConversationEvent(val forUserWithId: String): ConversationListEvents()