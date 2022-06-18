package com.dinesh.demoforinvoy.di.home

import androidx.lifecycle.ViewModel
import com.dinesh.demoforinvoy.di.ViewModelKey
import com.dinesh.demoforinvoy.viewmodel.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HomeViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel
}