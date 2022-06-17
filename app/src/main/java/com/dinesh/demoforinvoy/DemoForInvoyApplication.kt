package com.dinesh.demoforinvoy

import com.dinesh.demoforinvoy.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class DemoForInvoyApplication: DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this).also {
            it.inject(this)
        }
    }
}