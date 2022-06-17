package com.dinesh.demoforinvoy.ui.splash

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.splash.SplashViewModel

class SplashFragment : BaseDaggerFragment<SplashViewModel>() {

    companion object {
        fun newInstance() = SplashFragment()
    }

    override fun viewModel(): SplashViewModel {
        return ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]
    }

    override val fragmentLayoutId = R.layout.fragment_splash

    override fun initViewBindings(view: View) {
        super.initViewBindings(view)
    }

    override fun initListeners() {
        super.initListeners()
    }

    override fun attachListeners() {
        super.attachListeners()
    }

    override fun setup() {
        super.setup()
    }

    override fun setupObservers() {
        super.setupObservers()
    }

    override fun clearListeners() {
        super.clearListeners()
    }

    override fun clearViewBindings() {
        super.clearViewBindings()
    }

    override fun clearReferences() {
        super.clearReferences()
    }
}