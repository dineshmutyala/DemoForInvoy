package com.dinesh.demoforinvoy

import com.dinesh.demoforinvoy.di.DaggerAppComponent
import com.google.firebase.FirebaseApp
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class DemoForInvoyApplication: DaggerApplication() {

    init {
        Timber.plant(Timber.DebugTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this).also {
            it.inject(this)
        }
    }
}