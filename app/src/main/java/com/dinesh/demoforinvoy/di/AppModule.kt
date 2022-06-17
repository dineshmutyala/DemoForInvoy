package com.dinesh.demoforinvoy.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    @AppContext
    fun provideApplicationContext(app: Application): Context = app

    @Provides
    @Named(DINames.USER_PERSISTENCE_FILE_NAME)
    fun provideUserPersistenceFileName(): String = "sd8asd98afad0a.json"
}