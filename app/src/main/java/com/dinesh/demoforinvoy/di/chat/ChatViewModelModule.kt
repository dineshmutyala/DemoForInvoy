package com.dinesh.demoforinvoy.di.chat

import androidx.lifecycle.ViewModel
import com.dinesh.demoforinvoy.di.ViewModelKey
import com.dinesh.demoforinvoy.viewmodel.chat.ChatViewModel
import com.dinesh.demoforinvoy.viewmodel.chat.ConversationsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ChatViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    abstract fun bindChatViewModel(viewModel: ChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConversationsViewModel::class)
    abstract fun bindConversationsViewModel(viewModel: ConversationsViewModel): ViewModel
}