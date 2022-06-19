package com.dinesh.demoforinvoy.roompersistence.weightlog

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dinesh.demoforinvoy.datamodels.weightlog.WeightLog
import com.dinesh.demoforinvoy.roompersistence.RoomConstants

@Dao
interface WeightJournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeightLog(log: WeightLog): Long

    @Query("SELECT * FROM ${RoomConstants.WEIGHTS_TABLE_NAME} WHERE weightOn = :forDate LIMIT 1")
    fun getWeightLog(forDate: String): LiveData<WeightLog?>

    @Query("SELECT * FROM ${RoomConstants.WEIGHTS_TABLE_NAME}")
    fun fetchAllLogs(): LiveData<List<WeightLog>>

    @Query("SELECT * FROM ${RoomConstants.WEIGHTS_TABLE_NAME} ORDER BY date DESC LIMIT :numDays")
    fun fetchLogsForPast(numDays: Int): LiveData<List<WeightLog>>

    @Query("SELECT * FROM ${RoomConstants.WEIGHTS_TABLE_NAME}")
    fun fetchAllLogsSync(): List<WeightLog>

    @Query("SELECT * FROM ${RoomConstants.WEIGHTS_TABLE_NAME} ORDER BY date DESC LIMIT :numDays")
    fun fetchLogsForPastSync(numDays: Int): List<WeightLog>
}
