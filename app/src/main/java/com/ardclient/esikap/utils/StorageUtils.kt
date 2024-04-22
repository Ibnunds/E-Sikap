package com.ardclient.esikap.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object StorageUtils {
    private lateinit var sharedPreferences: SharedPreferences

    fun getLang(activity: Activity): String {
        sharedPreferences =
            activity.getSharedPreferences(Constants.USER_LANGUAGE_PREFS_KEY, Context.MODE_PRIVATE)

        return sharedPreferences.getString("LANG", "en").toString()
    }
}