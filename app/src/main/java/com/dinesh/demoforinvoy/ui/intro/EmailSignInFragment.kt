package com.dinesh.demoforinvoy.ui.intro

import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.dinesh.demoforinvoy.databinding.FragmentEmailSignFragmentBinding
import com.dinesh.demoforinvoy.databinding.FullscreenBlockingLoadingBinding
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.intro.EmailSignInViewModel

class EmailSignInFragment: BaseDaggerFragment<EmailSignInViewModel>() {

    private var binding: FragmentEmailSignFragmentBinding? = null

    override val fragmentLayoutId: Int = R.layout.fragment_email_sign_fragment

    override fun viewModel(): EmailSignInViewModel {
        return ViewModelProvider(this, viewModelFactory)[EmailSignInViewModel::class.java]
    }

    override fun initViewBindings(view: View) {
        super.initViewBindings(view)
        binding = FragmentEmailSignFragmentBinding.bind(view)
        bufferingBinding = FullscreenBlockingLoadingBinding.bind(view)
    }

    override fun attachListeners() {
        super.attachListeners()
        binding?.signInButton?.setOnClickListener {
            val binding = binding.guardAgainstNull { return@setOnClickListener }
            startLoading()
            viewModel.signInClicked(binding.email.text.toString(), binding.password.text.toString())
        }
    }

    override fun setup() {
        super.setup()
        binding?.email?.text?.apply {
            clear()
            append("coach@weighttrackerdemo.com")
        }
        binding?.password?.text?.apply {
            clear()
            append("Coach@12349")
        }

        stopLoading()
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.getUserSignInSuccessTrigger().observe(viewLifecycleOwner) {
            when {
                it.errorMessage != null -> {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                    binding?.signInButton?.isEnabled = true
                }
                it.data != null -> stopLoading()
            }
        }

        viewModel.getNavigationTrigger().observe(viewLifecycleOwner) {
            findNavController().navigate(
                when(it.data) {
                    true -> EmailSignInFragmentDirections.actionEmailSignInFragmentToConversationsFragment()
                    else -> EmailSignInFragmentDirections.actionEmailSignInFragmentToHomeFragment()
                }
            )
        }

    }

    override fun clearListeners() {
        super.clearListeners()
    }

    override fun clearViewBindings() {
        super.clearViewBindings()
        binding = null
    }

    override fun clearReferences() {
        super.clearReferences()
    }

}