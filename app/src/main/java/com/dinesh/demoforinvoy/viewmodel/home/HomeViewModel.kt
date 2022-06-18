package com.dinesh.demoforinvoy.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.StringUtils
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.core.scheduler.SchedulerProvider
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val stringUtils: StringUtils,
    private val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    private val wishText = MutableLiveData<LiveDataResponse<String>>(LiveDataResponse(isLoading = true))

    private val weightTodayTrigger = MutableLiveData<LiveDataResponse<String>>()
    private val enterWeightTrigger = MutableLiveData<LiveDataResponse<Boolean>>()
    override fun refreshData() {
        super.refreshData()
        prepareWishText()
        prepareWeightToday()
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
        enterWeightTrigger.postValue(LiveDataResponse(data = true, isLoading = false))
    }

    fun getWishTextData(): LiveData<LiveDataResponse<String>> = wishText
    fun getWeightTodayData(): LiveData<LiveDataResponse<String>> = weightTodayTrigger
    fun getEnterWeightTodayTrigger(): LiveData<LiveDataResponse<Boolean>> = enterWeightTrigger

    override fun clearReferences() = Unit

}