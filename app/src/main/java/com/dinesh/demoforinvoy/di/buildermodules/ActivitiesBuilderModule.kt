package com.dinesh.demoforinvoy.di.buildermodules

import com.dinesh.demoforinvoy.di.splash.SplashModule
import com.dinesh.demoforinvoy.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesBuilderModule {
    @ContributesAndroidInjector(modules = [
        SplashModule::class
    ])
    abstract fun bindMainActivity(): MainActivity
}
