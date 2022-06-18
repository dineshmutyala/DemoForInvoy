package com.dinesh.demoforinvoy.core.livedata

data class LiveDataResponse<T>(
    val data: T? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)