package com.dinesh.demoforinvoy.roompersistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dinesh.demoforinvoy.datamodels.weightlog.WeightLog
import com.dinesh.demoforinvoy.di.AppContext
import com.dinesh.demoforinvoy.roompersistence.weightlog.WeightJournalDao

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        WeightLog::class
    ]
)
@TypeConverters(Converters::class)
abstract class WeightJournalDatabase: RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: WeightJournalDatabase? = null

        @Synchronized
        fun getInstance(@AppContext context: Context): WeightJournalDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                context,
                WeightJournalDatabase::class.java,
                RoomConstants.DATABASE_NAME
            ).build().also {
                INSTANCE = it
            }
        }
    }

    abstract fun weightJournalDao(): WeightJournalDao
}