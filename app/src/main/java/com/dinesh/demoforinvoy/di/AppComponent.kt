package com.dinesh.demoforinvoy.di

import android.app.Application
import com.dinesh.demoforinvoy.DemoForInvoyApplication
import com.dinesh.demoforinvoy.di.buildermodules.ActivitiesBuilderModule
import com.dinesh.demoforinvoy.di.firebase.MessagingServiceModule
import com.dinesh.demoforinvoy.di.roompersistence.RoomDataBaseModule
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
        AppModule::class,
        RoomDataBaseModule::class,
        RetrofitModule::class,
        MessagingServiceModule::class
    ],
    dependencies = []
)
interface AppComponent: AndroidInjector<DemoForInvoyApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}