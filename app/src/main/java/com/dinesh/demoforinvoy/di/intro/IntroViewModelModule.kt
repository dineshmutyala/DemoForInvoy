package com.dinesh.demoforinvoy.di.intro

import androidx.lifecycle.ViewModel
import com.dinesh.demoforinvoy.di.ViewModelKey
import com.dinesh.demoforinvoy.viewmodel.intro.IntroViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class IntroViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(IntroViewModel::class)
    abstract fun bindIntroViewModel(viewModel: IntroViewModel): ViewModel
}