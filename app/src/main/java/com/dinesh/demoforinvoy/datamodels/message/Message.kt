package com.dinesh.demoforinvoy.datamodels.message

import java.util.*

data class Message(
    val id: String,
    val message: String,
    val sentOn: Long,
    val isSentMessage: Boolean,
    val sentOnDate: Date = Date(sentOn)
)