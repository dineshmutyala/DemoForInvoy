package com.dinesh.demoforinvoy.di

import android.app.Application
import android.content.Context
import com.dinesh.demoforinvoy.core.StringUtils
import com.dinesh.demoforinvoy.core.misc.graph.GraphStyler
import com.dinesh.demoforinvoy.core.misc.graph.GraphValueFormatter
import com.dinesh.demoforinvoy.core.preferences.UserPersistence
import com.dinesh.demoforinvoy.core.preferences.UserPersistenceImpl
import com.dinesh.demoforinvoy.di.core.SchedulerProviderModule
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [
    SchedulerProviderModule::class
])
object AppModule {

    @Provides
    @Singleton
    @AppContext
    fun provideApplicationContext(app: Application): Context = app

    @Provides
    @Named(DINames.USER_PERSISTENCE_FILE_NAME)
    fun provideUserPersistenceFileName(): String = "sd8asd98afad0a.json"

    @Provides
    fun provideUserPersistence(userPersistenceImpl: UserPersistenceImpl): UserPersistence = userPersistenceImpl

    @Provides
    @Singleton
    fun provideStringUtils(@AppContext appContext: Context): StringUtils = StringUtils(appContext)

    @Provides
    @Singleton
    fun provideGraphStyler(
        @AppContext appContext: Context,
        valueFormatter: GraphValueFormatter
    ): GraphStyler = GraphStyler(appContext, valueFormatter)
}