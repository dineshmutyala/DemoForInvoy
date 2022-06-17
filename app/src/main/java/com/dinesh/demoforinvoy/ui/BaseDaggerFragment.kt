package com.dinesh.demoforinvoy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseDaggerFragment<T: BaseViewModel>: DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModel: T by lazy { viewModel() }

    abstract val fragmentLayoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return view ?: inflater.inflate(fragmentLayoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initListeners()
        postViewSetups(view)
        addBackPressedCallback()
    }

    private fun initViewModel() {
        viewModel.initViewModel()
    }

    protected open fun initListeners() = Unit

    private fun postViewSetups(view: View) {
        view.post {
            initViewBindings(view)
            attachListeners()
            setup()
            setupObservers()
        }

    }

    protected open fun initViewBindings(view: View) = Unit
    protected open fun attachListeners() = Unit
    protected open fun setup() = Unit
    protected open fun setupObservers() = Unit

    private fun addBackPressedCallback() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!onBackPressed()) {
                        isEnabled = false
                        activity?.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        )
    }

    protected open fun onBackPressed(): Boolean = false

    abstract fun viewModel(): T

    override fun onDestroyView() {
        super.onDestroyView()

        clearListeners()
        clearViewBindings()
    }

    protected open fun clearListeners() = Unit
    protected open fun clearViewBindings() = Unit

    override fun onDestroy() {
        super.onDestroy()
        clearReferences()
        viewModel.clearReferences()
    }

    protected open fun clearReferences() = Unit
}