package com.dinesh.demoforinvoy.core.misc.graph

import android.content.Context
import androidx.core.content.ContextCompat
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.di.AppContext
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineDataSet
import javax.inject.Inject

class GraphStyler @Inject constructor(
    @AppContext private val context: Context,
    private val formatter: GraphValueFormatter
) {

    fun styleChart(lineChart: LineChart) = lineChart.apply {
        axisRight.apply {
            isEnabled = false
        }
        axisLeft.apply {
            isEnabled = false
            axisMinimum = 0f
            //TODO: Make this dynamic, something like => (Max in data + 100)
            axisMaximum = 300f
            setDrawGridLines(false)
        }
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            valueFormatter = formatter

            isGranularityEnabled = false
        }

        description = null

        setTouchEnabled(true)
        isDragEnabled = true
        setPinchZoom(false)

        legend.isEnabled = false

        setDrawGridBackground(true)
        setGridBackgroundColor(ContextCompat.getColor(context, R.color.basil_green_100))

    }

    fun styleLineData(lineDataSet: LineDataSet) = lineDataSet.apply {
        color = ContextCompat.getColor(context, R.color.basil_orange)

        valueTextColor = ContextCompat.getColor(context, R.color.basil_green_800)
        valueTextSize = 13f

        lineWidth = 3f

        isHighlightEnabled = true
        setDrawHighlightIndicators(false)

        setDrawCircleHole(false)
        setCircleColor(ContextCompat.getColor(context, R.color.basil_green_800))

        mode = LineDataSet.Mode.LINEAR

        valueFormatter = formatter
    }

}