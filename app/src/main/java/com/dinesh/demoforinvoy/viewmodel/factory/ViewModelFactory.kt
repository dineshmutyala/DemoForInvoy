package com.dinesh.demoforinvoy.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return (creators[modelClass] ?: getProviderFor(modelClass))?.let { provider ->
            try {
                provider.get() as T
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        } ?: throw IllegalArgumentException("Unknown Model Class: $modelClass")
    }

    private fun <T> getProviderFor(modelClass: Class<T>): @JvmSuppressWildcards Provider<ViewModel>? {
        return creators.firstNotNullOfOrNull { if (it.key.isAssignableFrom(modelClass)) return it.value else null }
    }
}