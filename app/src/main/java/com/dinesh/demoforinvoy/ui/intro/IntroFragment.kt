package com.dinesh.demoforinvoy.ui.intro

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.databinding.FragmentIntroBinding
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.intro.IntroViewModel

class IntroFragment : BaseDaggerFragment<IntroViewModel>() {

    companion object {
        fun newInstance() = IntroFragment()
    }

    private var binding: FragmentIntroBinding? = null

    private var activityResultsLauncher: ActivityResultLauncher<Intent>? = null

    override fun viewModel(): IntroViewModel {
        return ViewModelProvider(this, viewModelFactory)[IntroViewModel::class.java]
    }

    override val fragmentLayoutId = R.layout.fragment_intro

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getActivityResultsLauncher()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initViewBindings(view: View) {
        super.initViewBindings(view)
        binding = FragmentIntroBinding.bind(view)
    }

    override fun initListeners() {
        super.initListeners()
    }

    override fun attachListeners() {
        super.attachListeners()
        binding?.signInButton?.setOnClickListener { context?.let { context -> viewModel.signInClicked(context) } }
    }

    override fun setup() {
        super.setup()
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.getIntroTrigger().observe(viewLifecycleOwner) {
            if (it.data == true) {
                handleNoUserSignedIn()
            }
        }
        viewModel.getUserSignInSuccessTrigger().observe(viewLifecycleOwner) {
            when {
                it.errorMessage != null -> {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                    binding?.signInButton?.isEnabled = true
                }
                it.data != null -> {
                    binding?.welcomeMessage?.text = it.data
                    fadeOutLoadingAnimation(false)
                    fadeOutSignInButton()
                    fadeInWelcomeText()
                }
            }
        }
        viewModel.getSignInIntentTrigger().observe(viewLifecycleOwner) {
            it.data?.let{ intent -> handleSignInRequestIntent(intent) }
        }

        viewModel.getNavigationTrigger().observe(viewLifecycleOwner) {
//            findNavController().navigate()
        }
    }

    private fun handleSignInRequestIntent(intent: Intent) {
        fadeInLoadingAnimation()
        binding?.signInButton?.isEnabled = false
        getActivityResultsLauncher().launch(intent)
    }

    private fun getActivityResultsLauncher(): ActivityResultLauncher<Intent> {
        return activityResultsLauncher ?: registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.receivedSignInResult(it.data)
        }.also {
            activityResultsLauncher = it
        }
    }

    private fun handleNoUserSignedIn() {
        fadeOutLoadingAnimation(shouldShowSignInButton = true)
    }

    private fun fadeInLoadingAnimation() {
        binding?.loadingAnimation?.also {
            fadeInView(it)
            it.playAnimation()
        }
    }

    private fun fadeInSignInButton() {
        binding?.signInButton?.also { fadeInView(it)}
    }

    private fun fadeInView(view: View) {
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(250L).start()
        }
    }

    private fun fadeInWelcomeText() {
        binding?.welcomeMessage?.also { fadeInView(it)}
    }

    private fun fadeOutLoadingAnimation(shouldShowSignInButton: Boolean) {
        binding?.loadingAnimation?.also { loadingView ->
            fadeOutView(loadingView) {
                loadingView.cancelAnimation()
                loadingView.visibility = View.GONE
                if (shouldShowSignInButton) fadeInSignInButton()
            }
        }
    }

    private fun fadeOutSignInButton() {
        binding?.signInButton?.also { fadeOutView(it) {} }
    }

    private fun fadeOutView(view: View, onFadeOutComplete: () -> Unit) {
        view.animate()
            .alpha(0f)
            .setDuration(250)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) = Unit
                override fun onAnimationCancel(animation: Animator?) = Unit
                override fun onAnimationRepeat(animation: Animator?) = Unit
                override fun onAnimationEnd(animation: Animator?) = onFadeOutComplete()
            })
    }

    override fun clearListeners() {
        super.clearListeners()
        binding?.signInButton?.setOnClickListener(null)
    }

    override fun clearViewBindings() {
        super.clearViewBindings()
        binding = null
    }

    override fun clearReferences() {
        super.clearReferences()
        activityResultsLauncher = null
    }
}