package com.dinesh.demoforinvoy.ui.viewlogs

import java.util.*

data class WeightLogPresentationModel(
    val id: String = UUID.randomUUID().toString(),
    val weightOn: String,
    val weight: String
){
    companion object {
        val EMPTY = WeightLogPresentationModel(
            id = "",
            weightOn = "",
            weight = "",
        )
    }

}