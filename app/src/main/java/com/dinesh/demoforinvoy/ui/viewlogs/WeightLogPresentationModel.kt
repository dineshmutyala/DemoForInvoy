package com.dinesh.demoforinvoy.ui.viewlogs

import com.dinesh.demoforinvoy.ui.common.BasePresentationModel
import java.util.*

data class WeightLogPresentationModel(
    val id: String = UUID.randomUUID().toString(),
    val weightOn: String,
    val weight: String
): BasePresentationModel {
    companion object {
        val EMPTY = WeightLogPresentationModel(
            id = "",
            weightOn = "",
            weight = "",
        )
    }

}