package com.dinesh.demoforinvoy.di.roompersistence

import android.content.Context
import com.dinesh.demoforinvoy.di.AppContext
import com.dinesh.demoforinvoy.di.AppModule
import com.dinesh.demoforinvoy.roompersistence.WeightJournalDatabase
import com.dinesh.demoforinvoy.roompersistence.weightlog.WeightJournalDao
import dagger.Module
import dagger.Provides

@Module(includes = [AppModule::class])
object RoomDataBaseModule {
    @Provides
    fun provideWeightJournalDao(
        @AppContext context: Context
    ): WeightJournalDao = WeightJournalDatabase.getInstance(context).weightJournalDao()
}