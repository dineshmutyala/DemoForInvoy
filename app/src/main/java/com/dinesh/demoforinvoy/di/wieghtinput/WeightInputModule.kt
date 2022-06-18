package com.dinesh.demoforinvoy.di.wieghtinput

import com.dinesh.demoforinvoy.di.ViewModelFactoryModule
import com.dinesh.demoforinvoy.ui.wieghtinput.WeightInputFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class WeightInputModule {
    @ContributesAndroidInjector(modules = [ViewModelFactoryModule::class, WeightInputViewModelModule::class])
    abstract fun bindWeightFragment(): WeightInputFragment
}