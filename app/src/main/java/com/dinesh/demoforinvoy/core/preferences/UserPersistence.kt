package com.dinesh.demoforinvoy.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.dinesh.demoforinvoy.di.AppContext
import com.dinesh.demoforinvoy.di.DINames
import javax.inject.Inject
import javax.inject.Named

interface UserPersistence {
    companion object {
        const val KEY_USER_ID = "User_Id"
    }
    fun addToPersistence(key: String, value: Any): Boolean
    fun removeFromPersistence(key: String)

    fun getString(key: String, default: String): String
    fun getStringOnNull(key: String): String?

    fun getInt(key: String, default: Int): Int
    fun getIntOnNull(key: String): Int?

    fun getFloat(key: String, default: Float): Float
    fun getFloatOnNull(key: String): Float?

    fun getLong(key: String, default: Long): Long
    fun getLongOnNull(key: String): Long?

    fun getBoolean(key: String, default: Boolean): Boolean
    fun getBooleanOnNull(key: String): Boolean?
}

class UserPersistenceImpl @Inject constructor(
    @Named(DINames.USER_PERSISTENCE_FILE_NAME) userPersistenceFileName: String,
    @AppContext appContext: Context,
): UserPersistence {

    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences(userPersistenceFileName, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor
        get() = sharedPreferences.edit()

    @Synchronized
    private fun keyExists(forKey: String): Boolean = sharedPreferences.contains(forKey)

    @Synchronized
    override fun addToPersistence(key: String, value: Any): Boolean {
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

    @Synchronized
    override fun removeFromPersistence(key: String) {
        editor.remove(key).apply()
    }

    @Synchronized
    override fun getString(key: String, default: String): String = sharedPreferences.getString(key, default) ?: default

    @Synchronized
    override fun getStringOnNull(key: String): String? =
        if (keyExists(key)) sharedPreferences.getString(key, "") else null

    @Synchronized
    override fun getInt(key: String, default: Int): Int = sharedPreferences.getInt(key, default)

    @Synchronized
    override fun getIntOnNull(key: String): Int? =
        if (keyExists(key)) sharedPreferences.getInt(key, -1) else null

    @Synchronized
    override fun getFloat(key: String, default: Float): Float = sharedPreferences.getFloat(key, default)

    @Synchronized
    override fun getFloatOnNull(key: String): Float? =
        if (keyExists(key)) sharedPreferences.getFloat(key, 0f) else null

    @Synchronized
    override fun getLong(key: String, default: Long): Long = sharedPreferences.getLong(key, default)

    @Synchronized
    override fun getLongOnNull(key: String): Long? =
        if (keyExists(key)) sharedPreferences.getLong(key, 0L) else null

    @Synchronized
    override fun getBoolean(key: String, default: Boolean): Boolean = sharedPreferences.getBoolean(key, default)

    @Synchronized
    override fun getBooleanOnNull(key: String): Boolean? =
        if (keyExists(key)) sharedPreferences.getBoolean(key, false) else null
}