package com.dinesh.demoforinvoy.di.splash

import com.dinesh.demoforinvoy.di.ViewModelFactoryModule
import com.dinesh.demoforinvoy.ui.splash.SplashFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SplashModule {
    @ContributesAndroidInjector(modules = [ViewModelFactoryModule::class, SplashViewModelModule::class])
    abstract fun bindSplashFragment(): SplashFragment
}