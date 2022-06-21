package com.dinesh.demoforinvoy.di.buildermodules

import com.dinesh.demoforinvoy.di.chat.ChatModule
import com.dinesh.demoforinvoy.di.home.HomeModule
import com.dinesh.demoforinvoy.di.intro.IntroModule
import com.dinesh.demoforinvoy.di.viewlogs.ViewLogsModule
import com.dinesh.demoforinvoy.di.wieghtinput.WeightInputModule
import com.dinesh.demoforinvoy.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesBuilderModule {
    @ContributesAndroidInjector(modules = [
        IntroModule::class,
        HomeModule::class,
        WeightInputModule::class,
        ViewLogsModule::class,
        ChatModule::class
    ])
    abstract fun bindMainActivity(): MainActivity
}
