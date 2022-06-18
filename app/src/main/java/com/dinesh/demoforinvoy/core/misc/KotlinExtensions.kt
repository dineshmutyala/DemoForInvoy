package com.dinesh.demoforinvoy.core.misc

inline fun<T> T?.guardAgainstNull(doThisIfNull: () -> Nothing): T {
    return this ?: doThisIfNull()
}