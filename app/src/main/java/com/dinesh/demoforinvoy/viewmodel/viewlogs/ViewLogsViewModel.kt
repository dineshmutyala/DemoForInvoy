package com.dinesh.demoforinvoy.viewmodel.viewlogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dinesh.demoforinvoy.core.StringUtils
import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.core.misc.graph.GraphStyler
import com.dinesh.demoforinvoy.core.scheduler.SchedulerProvider
import com.dinesh.demoforinvoy.repositories.WeightJournalRepository
import com.dinesh.demoforinvoy.ui.viewlogs.WeightLogPresentationModel
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*
import javax.inject.Inject

class ViewLogsViewModel @Inject constructor(
    private val stringUtils: StringUtils,
    private val schedulerProvider: SchedulerProvider,
    private val weightJournalRepository: WeightJournalRepository,
    private val graphStyler: GraphStyler
): BaseViewModel() {

    companion object {
        const val SIZE_PER_PAGE_FOR_GRAPH = 7
        const val SIZE_PER_PAGE_FOR_LIST = 20

        enum class ViewMode {
            GRAPH,
            LIST
        }
    }

    private var viewMode = ViewMode.GRAPH

    private val sizePerPage
        get() = if (viewMode == ViewMode.GRAPH) SIZE_PER_PAGE_FOR_GRAPH else SIZE_PER_PAGE_FOR_LIST

    private var pageNumber = 1

    private var canFetchMore = true

    private val graphWeightsData = MutableLiveData<LiveDataResponse<LineData>>()

    private val listWeightsData = MutableLiveData<LiveDataResponse<List<WeightLogPresentationModel>>>()

    private val canNavigateBack = MutableLiveData<LiveDataResponse<Boolean>>()
    private val canNavigateForward = MutableLiveData<LiveDataResponse<Boolean>>()

    private val showingGraphOrList = MutableLiveData<LiveDataResponse<ViewMode>>()

    override fun refreshData() {
        super.refreshData()
        resetGraph()
    }

    private fun fetchWeightsData(forPage: Int) {
        when(viewMode) {
            ViewMode.GRAPH -> fetchWeightsDataForGraph(forPage)
            ViewMode.LIST -> fetchWeightsDataForList(forPage)
        }
    }

    private fun fetchWeightsDataForGraph(forPage: Int) {
        graphWeightsData.postValue(LiveDataResponse(null, isLoading = true))
        schedulerProvider.io().scheduleDirect {
            weightJournalRepository.getWeightLogs(sizePerPage + 1, (forPage - 1) * sizePerPage).also { listWeights ->
                if (listWeights.size < sizePerPage) {
                    canFetchMore = false
                }

                if (listWeights.isEmpty()) {
                    canNavigateBack.postValue(LiveDataResponse(false, isLoading = false))
                    graphWeightsData.postValue(LiveDataResponse(null, isLoading = false))
                } else {

                    val differences = sequence {
                        for (i in 0 until listWeights.size - 1) {
                            yield(listWeights[i].weight.toFloat() - listWeights[i + 1].weight.toFloat())
                        }
                        yield(0f)
                    }.toList().reversed()

                    listWeights.reversed().mapIndexed { index, log ->
                        Entry(log.date?.time?.toFloat() ?: 0f, log.weight.toFloat(), differences[index])
                    }.drop(1).also {
                        graphStyler.styleLineData(LineDataSet(it, "")).also { lineDataSet ->
                            graphWeightsData.postValue(LiveDataResponse(LineData(lineDataSet), isLoading = false))
                        }
                    }
                    pageNumber = forPage
                }

                canNavigateForward.postValue(LiveDataResponse(pageNumber > 1, isLoading = false))
                canNavigateBack.postValue(LiveDataResponse(canFetchMore, isLoading = false))
            }
        }
    }

    private fun fetchWeightsDataForList(forPage: Int) {
        listWeightsData.postValue(LiveDataResponse(null, isLoading = true))
        schedulerProvider.io().scheduleDirect {
            weightJournalRepository.getWeightLogs(sizePerPage, (forPage - 1) * sizePerPage).also { listWeights ->
                if (listWeights.size < sizePerPage) {
                    canFetchMore = false
                }

                if (listWeights.isEmpty()) listWeightsData.postValue(LiveDataResponse(null, isLoading = false))
                else {
                    listWeights.map { log ->

                        val formattedDate = SynchronizedTimeUtils.parseDateSlashedMDY(log.weightOn)?.let {
                            SynchronizedTimeUtils.getFormattedShortDayLongMonthDayAndYear(it, TimeZone.getDefault())
                        } ?: log.weightOn

                        WeightLogPresentationModel(weightOn = formattedDate, weight = log.weight)
                    }.also {
                        listWeightsData.postValue(LiveDataResponse(it, isLoading = false))
                    }
                    pageNumber = forPage
                }
            }
        }
    }

    fun goToPreviousPage() {
        if(canFetchMore) {
            fetchWeightsData(pageNumber + 1)
        }
    }

    fun goToNextPage() {
        if(pageNumber > 0) {
            fetchWeightsData(pageNumber - 1)
        }
    }

    fun resetGraph() {
        canFetchMore = true
        fetchWeightsData(1)
    }

    fun toggleGraphAndList() {
        viewMode = when(viewMode) {
            ViewMode.GRAPH -> ViewMode.LIST
            ViewMode.LIST -> ViewMode.GRAPH
        }
        showingGraphOrList.postValue(LiveDataResponse(viewMode))
        pageNumber = 1
        resetGraph()
    }

    fun getWeightsDataForGraph(): LiveData<LiveDataResponse<LineData>> = graphWeightsData
    fun getWeightsDataForList(): LiveData<LiveDataResponse<List<WeightLogPresentationModel>>> = listWeightsData

    fun getCanNavigateForward(): LiveData<LiveDataResponse<Boolean>> = canNavigateForward
    fun getCanNavigateBack(): LiveData<LiveDataResponse<Boolean>> = canNavigateBack

    fun getShowingGraphOrList(): LiveData<LiveDataResponse<ViewMode>> = showingGraphOrList

    override fun clearReferences() = Unit
}