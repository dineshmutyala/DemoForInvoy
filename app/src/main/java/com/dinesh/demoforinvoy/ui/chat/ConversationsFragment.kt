package com.dinesh.demoforinvoy.ui.chat

import androidx.lifecycle.ViewModelProvider
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.chat.ConversationsViewModel

class ConversationsFragment: BaseDaggerFragment<ConversationsViewModel>() {

    override val fragmentLayoutId: Int = R.layout.fragment_conversations

    override fun viewModel(): ConversationsViewModel {
        return ViewModelProvider(this, viewModelFactory)[ConversationsViewModel::class.java]
    }
}