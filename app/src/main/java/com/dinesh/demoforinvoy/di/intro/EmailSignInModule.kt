package com.dinesh.demoforinvoy.di.intro

import com.dinesh.demoforinvoy.di.ViewModelFactoryModule
import com.dinesh.demoforinvoy.ui.intro.EmailSignInFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class EmailSignInModule {
    @ContributesAndroidInjector(modules = [ViewModelFactoryModule::class, EmailSignInViewModelModule::class])
    abstract fun bindEmailSignInFragment(): EmailSignInFragment
}