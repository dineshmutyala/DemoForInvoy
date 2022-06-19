package com.dinesh.demoforinvoy.viewmodel.wieghtinput

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.datamodels.weightlog.WeightLog
import com.dinesh.demoforinvoy.repositories.WeightJournalRepository
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class WeightInputViewModel @Inject constructor(
    private val weightJournalRepository: WeightJournalRepository
): BaseViewModel() {

    private val currentValue = MutableLiveData<LiveDataResponse<String>>(LiveDataResponse(isLoading = true))

    private val updatedTrigger = MutableLiveData<LiveDataResponse<Boolean>>()
    private val navigateBackTrigger = MutableLiveData<LiveDataResponse<Boolean>>()

    override fun refreshData() {
        super.refreshData()
        var observer: Observer<WeightLog?>? = null
        val weightLogLiveData = weightJournalRepository.getWeightLog(
            SynchronizedTimeUtils.getFormattedDateSlashedMDY(Date(), TimeZone.getDefault())
        )
        observer = Observer<WeightLog?> {
            observer?.let { observer -> weightLogLiveData.removeObserver(observer) }
            if (it == null) {
                currentValue.postValue(LiveDataResponse(isLoading = false))
            } else {
                currentValue.postValue(LiveDataResponse(data = it.weight, isLoading = false))
            }
        }
        weightLogLiveData.observeForever(observer)
    }

    fun addWeight(forDate: String, weight: String) {
        updatedTrigger.postValue(LiveDataResponse())
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            val newId = weightJournalRepository.addWeightLog(WeightLog(forDate, weight = weight))
            if (newId != 0L) {
                updatedTrigger.postValue(LiveDataResponse(data = true, isLoading = false))
                CoroutineScope(Dispatchers.Default).launch {
                    navigateBackTrigger.postValue(LiveDataResponse(data = true, isLoading = false))
                }
            } else {
                updatedTrigger.postValue(LiveDataResponse(
                    errorMessage = "Could not update your weight! Please try again.",
                    isLoading = false
                ))
            }
        }
    }

    fun getCurrentValue(): LiveData<LiveDataResponse<String>> = currentValue
    fun getUpdateTrigger(): LiveData<LiveDataResponse<Boolean>> = updatedTrigger
    fun getNavigateBackTrigger(): LiveData<LiveDataResponse<Boolean>> = navigateBackTrigger

    override fun clearReferences() = Unit
}