package com.dinesh.demoforinvoy.di.viewlogs

import androidx.lifecycle.ViewModel
import com.dinesh.demoforinvoy.di.ViewModelKey
import com.dinesh.demoforinvoy.viewmodel.viewlogs.ViewLogsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewLogsViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ViewLogsViewModel::class)
    abstract fun bindViewLogsViewModel(viewModel: ViewLogsViewModel): ViewModel
}