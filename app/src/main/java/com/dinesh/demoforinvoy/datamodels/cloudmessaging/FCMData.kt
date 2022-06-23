package com.dinesh.demoforinvoy.datamodels.cloudmessaging

data class FCMData(
    val sentBy: String,
    val sendTo: String,
    val title: String,
    val body: String,
    val sentOn: Long,
    val messageId: String
)