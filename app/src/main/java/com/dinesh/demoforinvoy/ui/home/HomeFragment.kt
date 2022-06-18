package com.dinesh.demoforinvoy.ui.home

import androidx.lifecycle.ViewModelProvider
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.home.HomeViewModel

class HomeFragment: BaseDaggerFragment<HomeViewModel>() {

    override val fragmentLayoutId: Int = R.layout.fragment_home

    override fun viewModel(): HomeViewModel =
        ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
}