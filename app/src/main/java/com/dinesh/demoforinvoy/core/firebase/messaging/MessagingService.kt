package com.dinesh.demoforinvoy.core.firebase.messaging

import com.dinesh.demoforinvoy.datamodels.cloudmessaging.FCMResponse
import com.dinesh.demoforinvoy.datamodels.cloudmessaging.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MessagingService {

    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAA9NOm6xs:APA91bE0P6o2sJ2Eeeh1EetayjIifxDMBM4D7PDEKTTejTUblYlIaUlgALIdJRiEkH9_yQexL0Z6phFDbpdZE8jc78-J5mC_gejLujziVL6Na2bqKsgZQGjpaUWj7Vlafi7GxN4x-1gb"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: Sender): Call<FCMResponse>
}