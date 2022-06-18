package com.dinesh.demoforinvoy.di.core

import com.dinesh.demoforinvoy.core.scheduler.AndroidSchedulerProvider
import com.dinesh.demoforinvoy.core.scheduler.SchedulerProvider
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class SchedulerProviderModule {

    @Binds
    @Singleton
    abstract fun provideSchedulerProvider(provider: AndroidSchedulerProvider): SchedulerProvider

}