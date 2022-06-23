package com.dinesh.demoforinvoy.di.firebase

import com.dinesh.demoforinvoy.core.firebase.messaging.MessagingService
import com.dinesh.demoforinvoy.di.RetrofitModule
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [RetrofitModule::class])
class MessagingServiceModule {
    @Singleton
    @Provides
    internal fun provideMessagingService(retrofit: Retrofit): MessagingService {
        return retrofit.create(MessagingService::class.java)
    }
}