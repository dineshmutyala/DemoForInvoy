package com.dinesh.demoforinvoy.ui.chat

import androidx.lifecycle.ViewModelProvider
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.databinding.FragmentChatBinding
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.chat.ChatViewModel

class ChatFragment: BaseDaggerFragment<ChatViewModel>() {

    private var binding: FragmentChatBinding? = null

    override val fragmentLayoutId: Int = R.layout.fragment_chat

    override fun viewModel(): ChatViewModel {
        return ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
    }
}