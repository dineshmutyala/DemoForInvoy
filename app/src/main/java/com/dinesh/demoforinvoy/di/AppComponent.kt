package com.dinesh.demoforinvoy.di

import android.app.Application
import com.dinesh.demoforinvoy.DemoForInvoyApplication
import com.dinesh.demoforinvoy.di.buildermodules.ActivitiesBuilderModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivitiesBuilderModule::class,
        AppModule::class
    ],
    dependencies = []
)
interface AppComponent: AndroidInjector<DemoForInvoyApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}