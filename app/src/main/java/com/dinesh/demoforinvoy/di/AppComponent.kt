package com.dinesh.demoforinvoy.di

import android.app.Application
import com.dinesh.demoforinvoy.DemoForInvoyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(
    modules = [
        AndroidInjectionModule::class
    ],
    dependencies = []
)
interface AppComponent: AndroidInjector<DemoForInvoyApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}