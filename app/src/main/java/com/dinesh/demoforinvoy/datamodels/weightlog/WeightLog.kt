package com.dinesh.demoforinvoy.datamodels.weightlog

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.dinesh.demoforinvoy.roompersistence.RoomConstants
import java.util.*

@Entity(tableName = RoomConstants.WEIGHTS_TABLE_NAME)
data class WeightLog(
    @PrimaryKey(autoGenerate = false)
    val weightOn: String,
    val weight: String,
    val sync: Boolean = true,
    val date: Date? = SynchronizedTimeUtils.parseDateSlashedMDY(weightOn)
)
