package com.dinesh.demoforinvoy.di

import androidx.lifecycle.ViewModelProvider
import com.dinesh.demoforinvoy.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module(includes = [])
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}