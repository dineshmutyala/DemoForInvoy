package com.dinesh.demoforinvoy.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.StringUtils
import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.core.misc.graph.GraphStyler
import com.dinesh.demoforinvoy.core.scheduler.SchedulerProvider
import com.dinesh.demoforinvoy.datamodels.weightlog.WeightLog
import com.dinesh.demoforinvoy.repositories.WeightJournalRepository
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val stringUtils: StringUtils,
    private val schedulerProvider: SchedulerProvider,
    private val weightJournalRepository: WeightJournalRepository,
    private val graphStyler: GraphStyler
): BaseViewModel() {

    private val wishText = MutableLiveData<LiveDataResponse<String>>(LiveDataResponse(isLoading = true))

    private val weightTodayTrigger = MutableLiveData<LiveDataResponse<String>>()
    private val enterWeightTrigger = MutableLiveData<LiveDataResponse<Boolean>>()

    private var pastWeekWeightsObserver: Observer<List<WeightLog>>? = null
    private var pastWeekWeightLiveData: LiveData<List<WeightLog>>? = null
    private val pastWeekWeights = MutableLiveData<LiveDataResponse<LineData>>()

    override fun refreshData() {
        super.refreshData()
        prepareWishText()
        prepareWeightToday()
        preparePastWeekWeightsData()
    }

    private fun prepareWishText() {
        val calendar: Calendar = Calendar.getInstance()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 4..11 -> R.string.good_morning
            in 12..15 -> R.string.good_afternoon
            else -> R.string.good_evening
        }.also { wishText.postValue(LiveDataResponse(stringUtils.getString(it))) }
    }

    private fun prepareWeightToday() {
        var observer: Observer<WeightLog?>? = null
        val weightLogLiveData = weightJournalRepository.getWeightLog(
            SynchronizedTimeUtils.getFormattedDateSlashedMDY(Date(), TimeZone.getDefault())
        )
        observer = Observer<WeightLog?> {
            observer?.let { observer -> weightLogLiveData.removeObserver(observer) }
            if (it == null) {
                enterWeightTrigger.postValue(LiveDataResponse(data = true, isLoading = false))
            } else {
                weightTodayTrigger.postValue(LiveDataResponse(
                    data = stringUtils.getString(R.string.weight_today, it.weight),
                    isLoading = false
                ))
            }
        }
        weightLogLiveData.observeForever(observer)
    }

    private fun preparePastWeekWeightsData() {
        schedulerProvider.io().scheduleDirect {
            weightJournalRepository.getWeightLogsForPastSync(7).also  { listWeights ->
                listWeights.reversed().map { log ->
                    Entry(log.date?.time?.toFloat() ?: 0f, log.weight.toFloat())
                }.also {
                    graphStyler.styleLineData(LineDataSet(it, "")).also { lineDataSet ->
                        pastWeekWeights.postValue(LiveDataResponse(LineData(lineDataSet)))
                    }
                }
            }
        }
    }

    fun getWishTextData(): LiveData<LiveDataResponse<String>> = wishText
    fun getWeightTodayData(): LiveData<LiveDataResponse<String>> = weightTodayTrigger
    fun getEnterWeightTodayTrigger(): LiveData<LiveDataResponse<Boolean>> = enterWeightTrigger
    fun getPastWeekWeightsData(): LiveData<LiveDataResponse<LineData>> = pastWeekWeights

    override fun clearReferences() {
        pastWeekWeightsObserver?.let { pastWeekWeightLiveData?.removeObserver(it) }
        pastWeekWeightsObserver = null
        pastWeekWeightLiveData = null
    }

}