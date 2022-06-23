package com.dinesh.demoforinvoy.datamodels.cloudmessaging

data class Sender(
    val data: FCMData,
    val to: String
)