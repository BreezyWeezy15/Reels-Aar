package com.le.app

import android.app.Application
import com.cars.reels.Reels

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Reels.enableInterAd(this, true, 1)  // Show interstitial ad every 4 swipes
        Reels.enableBannerAd(this, true, 1) // Show banner ad every 2 swipes
        Reels.enableVideoAd(this, true, 1)  // Show video ad every 2 swipes
    }
}