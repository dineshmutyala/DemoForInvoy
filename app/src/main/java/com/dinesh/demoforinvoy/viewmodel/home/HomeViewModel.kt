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
import com.dinesh.demoforinvoy.datamodels.message.Message
import com.dinesh.demoforinvoy.datamodels.weightlog.WeightLog
import com.dinesh.demoforinvoy.repositories.AccountRepository
import com.dinesh.demoforinvoy.repositories.ChatRepository
import com.dinesh.demoforinvoy.repositories.WeightJournalRepository
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val stringUtils: StringUtils,
    private val schedulerProvider: SchedulerProvider,
    private val weightJournalRepository: WeightJournalRepository,
    private val graphStyler: GraphStyler,
    private val chatRepository: ChatRepository,
    private val accountRepository: AccountRepository
): BaseViewModel() {

    private val wishText = MutableLiveData<LiveDataResponse<String>>(LiveDataResponse(isLoading = true))

    private val weightTodayTrigger = MutableLiveData<LiveDataResponse<String>>()
    private val enterWeightTrigger = MutableLiveData<LiveDataResponse<Boolean>>()

    private val pastWeekWeights = MutableLiveData<LiveDataResponse<LineData>>()

    private val graphSentToCoachTrigger = MutableLiveData<LiveDataResponse<Boolean>>()

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
            weightJournalRepository.getWeightLogsForPast(8).also  { listWeights ->
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

    fun generateTestData() {
        CoroutineScope(Dispatchers.IO).launch {

            weightJournalRepository.clearAllWeightLog()

            val now = LocalDate.now()
            var date = now.minusMonths(2)

            while (date.isBefore(now)) {
                Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                WeightLog(
                    weightOn = SynchronizedTimeUtils.getFormattedDateSlashedMDY(
                        Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        TimeZone.getDefault()
                    ),
                    weight = (Random().nextInt(30) + 140).toString()
                ).also { weightJournalRepository.addWeightLog(it) }
                date = date.plusDays(1)
            }

            CoroutineScope(Dispatchers.Main).launch { refreshData() }

        }
    }

    fun sendGraphToCoach(byteArray: ByteArray) {

        val observable = chatRepository.sendImageToCoach(byteArray)
        var observer: Observer<LiveDataResponse<Message>>? = null

        observer = Observer<LiveDataResponse<Message>> { response ->
            when {
                response.isLoading -> graphSentToCoachTrigger.postValue(LiveDataResponse(isLoading = true))
                response.errorMessage != null -> {
                    schedulerProvider.computation().scheduleDirect({
                        graphSentToCoachTrigger.postValue(
                            LiveDataResponse(errorMessage = response.errorMessage, isLoading = false)
                        )
                    }, 1000, TimeUnit.MILLISECONDS)
                    observer?.let { it -> observable.removeObserver(it) }
                }
                response.data != null -> {
                    schedulerProvider.computation().scheduleDirect({
                        graphSentToCoachTrigger.postValue(
                            LiveDataResponse(true, isLoading = false)
                        )
                    }, 1000, TimeUnit.MILLISECONDS)
                    observer?.let { it -> observable.removeObserver(it) }
                }
            }
        }
        observable.observeForever(observer)
    }

    fun getGraphSentToCoachTrigger(): LiveData<LiveDataResponse<Boolean>> = graphSentToCoachTrigger
    fun clearNavigateTrigger() {
        graphSentToCoachTrigger.postValue(LiveDataResponse(isLoading = false))
    }

    fun clearAllData() {
        CoroutineScope(Dispatchers.IO).launch {
            weightJournalRepository.clearAllWeightLog()
            CoroutineScope(Dispatchers.Main).launch { refreshData() }
        }
    }

    fun signOut() {
        accountRepository.signOut()
    }

    override fun clearReferences() {
    }

}
