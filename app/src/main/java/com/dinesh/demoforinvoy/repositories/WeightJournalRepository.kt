package com.dinesh.demoforinvoy.repositories

import androidx.lifecycle.LiveData
import com.dinesh.demoforinvoy.datamodels.weightlog.WeightLog
import com.dinesh.demoforinvoy.roompersistence.weightlog.WeightJournalDao
import javax.inject.Inject

class WeightJournalRepository @Inject constructor(
    private val weightJournalDao: WeightJournalDao
) {

    fun getWeightLog(forDate: String): LiveData<WeightLog?> = weightJournalDao.getWeightLog(forDate)

    fun getWeightLogsForPastAsync(numDays: Int = -1): LiveData<List<WeightLog>> {
        return when (numDays) {
            -1 -> weightJournalDao.fetchAllLogsAsync()
            else -> weightJournalDao.fetchLogsForPastAsync(numDays)
        }
    }

    fun getWeightLogsForPast(numDays: Int = -1): List<WeightLog> {
        return when (numDays) {
            -1 -> weightJournalDao.fetchAllLogs()
            else -> weightJournalDao.fetchLogsForPast(numDays)
        }
    }

    suspend fun addWeightLog(weightLog: WeightLog): Long = weightJournalDao.addWeightLog(weightLog)

    fun clearAllWeightLog() = weightJournalDao.clearAllWeightLog()
}