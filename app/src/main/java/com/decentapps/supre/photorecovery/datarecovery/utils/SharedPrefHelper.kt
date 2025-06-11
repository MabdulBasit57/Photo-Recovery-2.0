package com.decentapps.supre.photorecovery.datarecovery.utils

import android.content.Context

object SharedPrefHelper {
    private const val PREF_NAME = "onboarding_pref"
    private const val IS_FIRST_TIME = "is_first_time"

    fun isFirstTime(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(IS_FIRST_TIME, true)
    }

    fun setNotFirstTime(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(IS_FIRST_TIME, false).apply()
    }
}
