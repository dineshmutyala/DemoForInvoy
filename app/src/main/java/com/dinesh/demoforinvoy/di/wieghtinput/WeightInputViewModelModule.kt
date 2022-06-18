package com.dinesh.demoforinvoy.di.wieghtinput

import androidx.lifecycle.ViewModel
import com.dinesh.demoforinvoy.di.ViewModelKey
import com.dinesh.demoforinvoy.viewmodel.wieghtinput.WeightInputViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WeightInputViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(WeightInputViewModel::class)
    abstract fun bindWeightInputViewModel(viewModel: WeightInputViewModel): ViewModel
}