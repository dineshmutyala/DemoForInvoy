package com.dinesh.demoforinvoy.ui.home

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.dinesh.demoforinvoy.core.misc.graph.GraphStyler
import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.dinesh.demoforinvoy.databinding.FragmentHomeBinding
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.home.HomeViewModel
import java.util.*
import javax.inject.Inject

class HomeFragment: BaseDaggerFragment<HomeViewModel>(), MenuProvider {

    @Inject
    lateinit var graphStyler: GraphStyler

    private var binding: FragmentHomeBinding? = null

    override val fragmentLayoutId: Int = R.layout.fragment_home

    override fun viewModel(): HomeViewModel =
        ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            show()
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
        binding.expandGraph.setOnClickListener { }
    }

    private fun inputWeightFromUser() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToWeightInputFragment(
                SynchronizedTimeUtils.getFormattedDateSlashedMDY(Date(), TimeZone.getDefault())
            )
        )
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_home, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuOptionGenerateData -> viewModel.generateTestData()
            R.id.menuOptionClearData -> viewModel.clearAllData()
        }
        return true
    }

    override fun setup() {
        super.setup()
        binding?.lineGraph?.also { graphStyler.styleChart(it) }
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.getWishTextData().observe(viewLifecycleOwner) {
            binding?.wishingText?.text = it.data
        }

        viewModel.getEnterWeightTodayTrigger().observe(viewLifecycleOwner) {
            val binding = binding.guardAgainstNull { return@observe }
            if (it.data == true) {
                fadeOutView(binding.weightToday)
                fadeInView(binding.enterWeight)
            }
        }

        viewModel.getWeightTodayData().observe(viewLifecycleOwner) {
            val binding = binding.guardAgainstNull { return@observe }
            if (it.data != null) {
                binding.weightToday.text = it.data
                fadeOutView(binding.enterWeight)
                fadeInView(binding.weightToday)
            }
        }

        viewModel.getPastWeekWeightsData().observe(viewLifecycleOwner) { response ->
            when {
                response.data != null -> {
                    binding?.lineGraph?.also {
                        it.data = response.data
                        it.invalidate()
                        binding?.noDataAvailable?.visibility = if(response.data.entryCount == 0) View.VISIBLE
                            else View.GONE
                        binding?.expandGraph?.visibility = if(response.data.entryCount == 0) View.INVISIBLE
                        else View.VISIBLE
                    }
                }
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