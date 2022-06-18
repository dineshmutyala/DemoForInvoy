package com.dinesh.demoforinvoy.di.home

import com.dinesh.demoforinvoy.di.ViewModelFactoryModule
import com.dinesh.demoforinvoy.ui.home.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeModule {
    @ContributesAndroidInjector(modules = [ViewModelFactoryModule::class, HomeViewModelModule::class])
    abstract fun bindHomeFragment(): HomeFragment
}