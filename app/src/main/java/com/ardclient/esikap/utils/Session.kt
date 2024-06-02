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
        val userLevel = sharedPreferences.getString(Constants.USER_LEVEL, "")
        val userPassword = sharedPreferences.getString(Constants.USERPASSWORD_KEY, "")
        val userAktif = sharedPreferences.getInt(Constants.USER_AKTIF, 0)
        val userAvatar = sharedPreferences.getString(Constants.AVATAR_KEY, "")

        return UserSessionModel(
            userId = userId,
            userName = userName,
            name = name,
            userLevel = userLevel,
            userPassword = userPassword,
            userAktif = userAktif,
            userAvatar = userAvatar
        )
    }

    fun clearUserSession(activity: Activity, listener: OnSessionClear?){
        sharedPreferences = activity.getSharedPreferences(Constants.USER_SESSION_PREFS_KEY, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        listener?.onSessionCleared()
    }
}