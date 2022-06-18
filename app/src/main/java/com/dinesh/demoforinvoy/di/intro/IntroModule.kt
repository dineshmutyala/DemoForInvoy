package com.dinesh.demoforinvoy.di.intro

import com.dinesh.demoforinvoy.di.ViewModelFactoryModule
import com.dinesh.demoforinvoy.ui.intro.IntroFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IntroModule {
    @ContributesAndroidInjector(modules = [ViewModelFactoryModule::class, IntroViewModelModule::class])
    abstract fun bindIntroFragment(): IntroFragment
}