package com.cars.reels

import android.content.Context
import android.util.Log


object Reels {

    private const val PREFS_NAME = "prefs"

    private fun setPreference(context: Context, key: String, value: Any) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            else -> throw IllegalArgumentException("Unsupported preference type")
        }

        editor.commit()
    }

    fun enableBannerAd(context: Context, shouldRun: Boolean, frequency: Int) {
        setPreference(context, "shouldRunBanner", shouldRun)
        setPreference(context, "bannerFrequency", frequency)

        Log.d("Banner Ad TAG","Banner Ad Has Been Called")
    }

    fun enableInterAd(context: Context, shouldRun: Boolean, frequency: Int) {
        setPreference(context, "shouldRunInter", shouldRun)
        setPreference(context, "interFrequency", frequency)
    }

    fun enableVideoAd(context: Context, shouldRun: Boolean, frequency: Int) {
        setPreference(context, "shouldRunVideo", shouldRun)
        setPreference(context, "videoFrequency", frequency)
    }

    private fun getPreferences(context: Context, key: String, defaultValue: Any): Any {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return when (defaultValue) {
            is Boolean -> prefs.getBoolean(key, defaultValue)
            is Int -> prefs.getInt(key, defaultValue)
            else -> throw IllegalArgumentException("Unsupported preference type")
        }
    }

    fun getBannerAdParams(context: Context): Map<String, Any> {
        val shouldRun = getPreferences(context, "shouldRunBanner",false) as Boolean
        val frequency = getPreferences(context, "bannerFrequency", 2) as Int
        return mapOf("shouldRunBanner" to shouldRun, "bannerFrequency" to frequency)
    }

    fun getInterAdParams(context: Context): Map<String, Any> {
        val shouldRun = getPreferences(context, "shouldRunInter", false) as Boolean
        val frequency = getPreferences(context, "interFrequency", 4) as Int
        return mapOf("shouldRunInter" to shouldRun, "interFrequency" to frequency)
    }

    fun getVideoAdParams(context: Context): Map<String, Any> {
        val shouldRun = getPreferences(context, "shouldRunVideo", false) as Boolean
        val frequency = getPreferences(context, "videoFrequency", 6) as Int
        return mapOf("shouldRunVideo" to shouldRun, "videoFrequency" to frequency)
    }
}
