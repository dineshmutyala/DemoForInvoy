package com.dinesh.demoforinvoy.di.viewlogs

import com.dinesh.demoforinvoy.di.ViewModelFactoryModule
import com.dinesh.demoforinvoy.ui.viewlogs.ViewLogsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ViewLogsModule {
    @ContributesAndroidInjector(modules = [ViewModelFactoryModule::class, ViewLogsViewModelModule::class])
    abstract fun bindViewLogsFragment(): ViewLogsFragment
}