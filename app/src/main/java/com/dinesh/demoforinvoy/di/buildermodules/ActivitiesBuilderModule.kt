package com.dinesh.demoforinvoy.di.buildermodules

import com.dinesh.demoforinvoy.di.intro.IntroModule
import com.dinesh.demoforinvoy.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesBuilderModule {
    @ContributesAndroidInjector(modules = [
        IntroModule::class
    ])
    abstract fun bindMainActivity(): MainActivity
}
