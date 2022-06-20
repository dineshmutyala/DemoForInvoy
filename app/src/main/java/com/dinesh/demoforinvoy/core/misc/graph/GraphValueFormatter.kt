package com.dinesh.demoforinvoy.core.misc.graph

import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class GraphValueFormatter @Inject constructor(): ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value.toInt().toFloat() == value) {
            String.format("%d", value.toInt())
        } else {
            String.format("%.1f", value)
        }
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return if (axis is XAxis) {
            SynchronizedTimeUtils.getFormattedDateSlashedMD(Date(value.toLong()), TimeZone.getDefault())
        } else {
            super.getAxisLabel(value, axis)
        }
    }
}