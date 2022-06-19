package com.dinesh.demoforinvoy.ui.home

import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.dinesh.demoforinvoy.databinding.FragmentHomeBinding
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.home.HomeViewModel
import java.util.*

class HomeFragment: BaseDaggerFragment<HomeViewModel>() {

    private var binding: FragmentHomeBinding? = null

    override val fragmentLayoutId: Int = R.layout.fragment_home

    override fun viewModel(): HomeViewModel =
        ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            show()
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar_view)
        }
    }

    override fun initViewBindings(view: View) {
        super.initViewBindings(view)
        binding = FragmentHomeBinding.bind(view)
    }

    override fun initListeners() {
        super.initListeners()
    }

    override fun attachListeners() {
        super.attachListeners()
        val binding = binding.guardAgainstNull { return }
        binding.enterWeight.setOnClickListener { inputWeightFromUser() }
        binding.weightToday.setOnClickListener { inputWeightFromUser() }
    }

    private fun inputWeightFromUser() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToWeightInputFragment(
                SynchronizedTimeUtils.getFormattedDateSlashedMDY(Date(), TimeZone.getDefault())
            )
        )
    }

    override fun setup() {
        super.setup()
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.getWishTextData().observe(viewLifecycleOwner) {
            binding?.wishingText?.text = it.data
        }

        viewModel.getEnterWeightTodayTrigger().observe(viewLifecycleOwner) {
            val binding = binding.guardAgainstNull { return@observe }
            fadeOutView(binding.weightToday) {
                fadeInView(binding.enterWeight)
            }
        }

        viewModel.getWeightTodayData().observe(viewLifecycleOwner) {
            val binding = binding.guardAgainstNull { return@observe }
            binding.weightToday.text = it.data
            fadeOutView(binding.enterWeight) {
                fadeInView(binding.weightToday)
            }
        }
    }

    override fun clearListeners() {
        super.clearListeners()
        val binding = binding.guardAgainstNull { return }
        binding.enterWeight.setOnClickListener(null)
    }

    override fun clearViewBindings() {
        super.clearViewBindings()
        binding = null
    }

    override fun clearReferences() {
        super.clearReferences()
    }

}