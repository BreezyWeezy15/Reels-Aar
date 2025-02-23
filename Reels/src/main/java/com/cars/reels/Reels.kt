package com.cars.reels

import android.content.Context
import android.util.Log

object Reels {

    fun enableBannerAd(context: Context, shouldRun: Boolean, frequency: Int) {
        val prefs = context.getSharedPreferences("bannerPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("shouldRun", shouldRun)
        editor.putInt("frequency", frequency)
        editor.apply()

        Log.d("AdDebug", "Banner Ad Enabled - shouldRun: $shouldRun, frequency: $frequency")
    }

    fun enableInterAd(context: Context, shouldRun: Boolean, frequency: Int) {
        val prefs = context.getSharedPreferences("interPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("shouldRun", shouldRun)
        editor.putInt("frequency", frequency)
        editor.apply()

        Log.d("AdDebug", "Interstitial Ad Enabled - shouldRun: $shouldRun, frequency: $frequency")
    }

    fun enableVideoAd(context: Context, shouldRun: Boolean, frequency: Int) {
        val prefs = context.getSharedPreferences("videoPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("shouldRun", shouldRun)
        editor.putInt("frequency", frequency)
        editor.apply()

        Log.d("AdDebug", "Video Ad Enabled - shouldRun: $shouldRun, frequency: $frequency")
    }


    fun getBannerAdParams(context: Context) : Map<String,Any> {
        val map = hashMapOf<String,Any>()
        val prefs = context.getSharedPreferences("bannerPrefs",Context.MODE_PRIVATE)
        val shouldRun = prefs.getBoolean("shouldRun",true)
        val frequency = prefs.getInt("frequency",4)

        map["shouldRun"] = shouldRun
        map["frequency"] = frequency

        return map
    }


    fun getInterAdParams(context: Context) : Map<String,Any> {
        val map = hashMapOf<String,Any>()
        val prefs = context.getSharedPreferences("interPrefs",Context.MODE_PRIVATE)
        val shouldRun = prefs.getBoolean("shouldRun",true)
        val frequency = prefs.getInt("frequency",2)

        map["shouldRun"] = shouldRun
        map["frequency"] = frequency

        return map
    }


    fun getVideoAdParams(context: Context) : Map<String,Any> {
        val map = hashMapOf<String,Any>()
        val prefs = context.getSharedPreferences("videoPrefs",Context.MODE_PRIVATE)
        val shouldRun = prefs.getBoolean("shouldRun",true)
        val frequency = prefs.getInt("frequency",2)

        map["shouldRun"] = shouldRun
        map["frequency"] = frequency

        return map
    }


}