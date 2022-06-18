package com.dinesh.demoforinvoy.core

import android.content.Context
import androidx.annotation.StringRes

class StringUtils(private val appContext: Context) {
    fun getString(@StringRes resId: Int) = appContext.getString(resId)
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?) = appContext.getString(resId, *formatArgs)
}
