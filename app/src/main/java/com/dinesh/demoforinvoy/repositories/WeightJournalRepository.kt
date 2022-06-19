package com.dinesh.demoforinvoy.repositories

import androidx.lifecycle.LiveData
import com.dinesh.demoforinvoy.datamodels.weightlog.WeightLog
import com.dinesh.demoforinvoy.roompersistence.weightlog.WeightJournalDao
import javax.inject.Inject

class WeightJournalRepository @Inject constructor(
    private val weightJournalDao: WeightJournalDao
) {
    val allLogs: LiveData<List<WeightLog>> = weightJournalDao.fetchAllLogs()

    fun getWeightLog(forDate: String): LiveData<WeightLog?> = weightJournalDao.getWeightLog(forDate)

    suspend fun addWeightLog(weightLog: WeightLog): Long = weightJournalDao.addWeightLog(weightLog)
}