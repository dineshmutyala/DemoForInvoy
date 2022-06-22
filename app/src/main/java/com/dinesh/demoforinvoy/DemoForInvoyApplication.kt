package com.dinesh.demoforinvoy

import com.dinesh.demoforinvoy.core.firebase.FirebaseManager
import com.dinesh.demoforinvoy.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

class DemoForInvoyApplication: DaggerApplication() {

    @Inject lateinit var firebaseManager: FirebaseManager

    init {
        Timber.plant(Timber.DebugTree())
    }

    override fun onCreate() {
        super.onCreate()
        firebaseManager.initialize()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this).also {
            it.inject(this)
        }
    }
}