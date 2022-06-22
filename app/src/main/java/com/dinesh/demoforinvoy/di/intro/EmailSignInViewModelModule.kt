package com.dinesh.demoforinvoy.di.intro

import androidx.lifecycle.ViewModel
import com.dinesh.demoforinvoy.di.ViewModelKey
import com.dinesh.demoforinvoy.viewmodel.intro.EmailSignInViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class EmailSignInViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(EmailSignInViewModel::class)
    abstract fun bindEmailSignInViewModel(viewModel: EmailSignInViewModel): ViewModel
}