package com.dinesh.demoforinvoy.di.chat

import com.dinesh.demoforinvoy.di.ViewModelFactoryModule
import com.dinesh.demoforinvoy.ui.chat.ChatFragment
import com.dinesh.demoforinvoy.ui.chat.ConversationsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatModule {
    @ContributesAndroidInjector(modules = [ViewModelFactoryModule::class, ChatViewModelModule::class])
    abstract fun bindChatFragment(): ChatFragment

    @ContributesAndroidInjector(modules = [ViewModelFactoryModule::class, ChatViewModelModule::class])
    abstract fun bindConversationsFragment(): ConversationsFragment
}