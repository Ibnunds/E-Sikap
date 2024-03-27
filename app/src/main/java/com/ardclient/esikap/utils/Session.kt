package com.ardclient.esikap.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.ardclient.esikap.model.UserSessionModel

object SessionUtils {
    private lateinit var sharedPreferences: SharedPreferences

    interface OnSessionClear{
        fun onSessionCleared()
    }

    fun getUserSession(activity: Activity): UserSessionModel {
        sharedPreferences =
            activity.getSharedPreferences(Constants.USER_SESSION_PREFS_KEY, Context.MODE_PRIVATE)

        val userId = sharedPreferences.getInt(Constants.USERID_KEY, 0)
        val userName = sharedPreferences.getString(Constants.USERNAME_KEY, "")
        val name = sharedPreferences.getString(Constants.NAME_KEY, "")
        val userWilayah = sharedPreferences.getString(Constants.WILAYAH_KEY, "")
        val userLevel = sharedPreferences.getString(Constants.USER_LEVEL, "")

        return UserSessionModel(userId, userName, name, userWilayah, userLevel)
    }

    fun clearUserSession(activity: Activity, listener: OnSessionClear?){
        sharedPreferences = activity.getSharedPreferences(Constants.USER_SESSION_PREFS_KEY, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        listener?.onSessionCleared()
    }
}