package com.dinesh.demoforinvoy.misc

import android.content.Context
import android.content.SharedPreferences
import com.dinesh.demoforinvoy.di.AppContext
import com.dinesh.demoforinvoy.di.DINames
import javax.inject.Inject
import javax.inject.Named

class UserPersistence @Inject constructor(
    @Named(DINames.USER_PERSISTENCE_FILE_NAME) userPersistenceFileName: String,
    @AppContext appContext: Context,
) {
    companion object {
        const val KEY_USER_ID = "User_Id"
    }

    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences(userPersistenceFileName, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor
        get() = sharedPreferences.edit()

    private fun keyExists(forKey: String): Boolean = sharedPreferences.contains(forKey)

    fun addToPersistence(key: String, value: Any): Boolean {
        editor.apply {
            when(value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is Boolean -> putBoolean(key, value)
                else -> return false
            }
            apply()
            return false
        }
    }

    private fun getString(key: String, default: String): String = sharedPreferences.getString(key, default) ?: default

    private fun getStringOnNull(key: String): String? =
        if (keyExists(key)) sharedPreferences.getString(key, "") else null

    private fun getInt(key: String, default: Int): Int = sharedPreferences.getInt(key, default)

    private fun getIntOnNull(key: String): Int? =
        if (keyExists(key)) sharedPreferences.getInt(key, -1) else null

    private fun getFloat(key: String, default: Float): Float = sharedPreferences.getFloat(key, default)

    private fun getFloatOnNull(key: String): Float? =
        if (keyExists(key)) sharedPreferences.getFloat(key, 0f) else null

    private fun getLong(key: String, default: Long): Long = sharedPreferences.getLong(key, default)

    private fun getLongOnNull(key: String): Long? =
        if (keyExists(key)) sharedPreferences.getLong(key, 0L) else null

    private fun getBoolean(key: String, default: Boolean): Boolean = sharedPreferences.getBoolean(key, default)

    private fun getBooleanOnNull(key: String): Boolean? =
        if (keyExists(key)) sharedPreferences.getBoolean(key, false) else null
}