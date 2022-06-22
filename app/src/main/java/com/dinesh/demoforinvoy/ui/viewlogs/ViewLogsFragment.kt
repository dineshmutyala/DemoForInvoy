package com.dinesh.demoforinvoy.ui.viewlogs

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.misc.graph.GraphStyler
import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.dinesh.demoforinvoy.core.misc.isLastItemShowing
import com.dinesh.demoforinvoy.core.misc.isValidToCheckForLastItem
import com.dinesh.demoforinvoy.databinding.FragmentViewLogsBinding
import com.dinesh.demoforinvoy.databinding.FullscreenBlockingLoadingBinding
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.viewlogs.ViewLogsViewModel
import com.dinesh.demoforinvoy.viewmodel.viewlogs.ViewLogsViewModel.Companion.SIZE_PER_PAGE_FOR_LIST
import javax.inject.Inject

class ViewLogsFragment: BaseDaggerFragment<ViewLogsViewModel>() {

    @Inject
    lateinit var graphStyler: GraphStyler

    private var binding: FragmentViewLogsBinding? = null

    private lateinit var listdapter: ViewLogsAdapter

    override val fragmentLayoutId: Int = R.layout.fragment_view_logs

    override fun viewModel(): ViewLogsViewModel {
        return ViewModelProvider(this, viewModelFactory)[ViewLogsViewModel::class.java]
    }

    override fun initViewBindings(view: View) {
        super.initViewBindings(view)
        binding = FragmentViewLogsBinding.bind(view)
        bufferingBinding = FullscreenBlockingLoadingBinding.bind(view)
    }

    override fun initListeners() {
        super.initListeners()
    }

    override fun attachListeners() {
        super.attachListeners()
        val binding = binding.guardAgainstNull { return }

        binding.goToPreviousPage.setOnClickListener { viewModel.goToPreviousPage() }
        binding.goToNextPage.setOnClickListener { viewModel.goToNextPage() }
        binding.goToLastPage.setOnClickListener { viewModel.resetGraph() }

        binding.switchGraphAndList.setOnClickListener { viewModel.toggleGraphAndList() }

        attachListScrollListener(binding)

    }

    private fun attachListScrollListener(binding: FragmentViewLogsBinding) {
        binding.listData.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (recyclerView.isValidToCheckForLastItem(newState) &&
                        recyclerView.isLastItemShowing() &&
                        !listdapter.isWaitingForMoreResults()
                    ) { handleLoadMoreSongs() }
                }
            }
        )
    }

    private fun handleLoadMoreSongs() {
        listdapter.loadingMore()
        viewModel.goToPreviousPage()
    }

    override fun setup() {
        super.setup()
        binding?.lineGraph?.also { graphStyler.styleChart(it) }
        setupList()
    }

    private fun setupList() {
        listdapter = ViewLogsAdapter()
        binding?.listData?.apply {
            adapter = listdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
    }

    override fun setupObservers() {
        super.setupObservers()

        val binding = binding.guardAgainstNull { return }

        viewModel.getCanNavigateForward().observe(viewLifecycleOwner) {
            when(it.data) {
                true -> {
                    enableButton(binding.goToNextPage)
                    enableButton(binding.goToLastPage)
                }
                else -> {
                    disableButton(binding.goToNextPage)
                    disableButton(binding.goToLastPage)
                }
            }
        }
        viewModel.getCanNavigateBack().observe(viewLifecycleOwner) {
            when(it.data) {
                true -> enableButton(binding.goToPreviousPage)
                else -> disableButton(binding.goToPreviousPage)
            }
        }

        viewModel.getWeightsDataForGraph().observe(viewLifecycleOwner) {
            when{
                it.isLoading -> startLoading()
                it.data == null -> stopLoading()
                else -> {
                    stopLoading()
                    binding.lineGraph.data = it.data
                    binding.lineGraph.invalidate()
                }
            }
        }

        viewModel.getShowingGraphOrList().observe(viewLifecycleOwner) {
            when (it.data) {
                ViewLogsViewModel.Companion.ViewMode.LIST -> {
                    binding.switchGraphAndList.setImageResource(R.drawable.ic_show_chart)
                    binding.lineGraph.visibility = View.GONE
                    binding.graphNavigationLayout.visibility = View.GONE
                    binding.listData.visibility = View.VISIBLE
                }
                ViewLogsViewModel.Companion.ViewMode.GRAPH -> {
                    binding.switchGraphAndList.setImageResource(R.drawable.ic_show_list)
                    binding.listData.visibility = View.GONE
                    binding.graphNavigationLayout.visibility = View.VISIBLE
                    binding.lineGraph.visibility = View.VISIBLE
                    listdapter.clearOut()
                }
                else -> Unit
            }
        }

        viewModel.getWeightsDataForList().observe(viewLifecycleOwner) {
            when{
                it.isLoading -> {
                    if (listdapter.itemCount == 0) startLoading()
                }
                it.data == null -> {
                    stopLoading()
                    listdapter.notifyEndOfResults()
                }
                else -> {
                    stopLoading()
                    listdapter.updateData(it.data)
                    if (it.data.size < SIZE_PER_PAGE_FOR_LIST) listdapter.notifyEndOfResults()
                }
            }
        }
    }

    private fun enableButton(imageView: ImageView) {
        imageView.isEnabled = true
        imageView.setTint(R.color.primaryTextColor)
    }

    private fun disableButton(imageView: ImageView) {
        imageView.isEnabled = false
        imageView.setTint(R.color.disabledColor)
    }

    private fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
    }

    override fun clearListeners() {
        super.clearListeners()
        binding?.goToPreviousPage?.setOnClickListener(null)
        binding?.goToNextPage?.setOnClickListener(null)
        binding?.goToLastPage?.setOnClickListener(null)

    }

    override fun clearViewBindings() {
        super.clearViewBindings()
        binding = null
    }

    override fun clearReferences() {
        super.clearReferences()
    }

}