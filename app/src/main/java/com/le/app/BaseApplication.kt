package com.le.app

import android.app.Application
import com.cars.reels.Reels

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val highFreq = 2
        val lowFreq = 4 

        Reels.initialize(
            shouldRunBanner = true, bannerFreq = highFreq,
            shouldRunInter = true, interFreq = lowFreq,
            shouldRunVideo = true, videoFreq = highFreq
        )

    }
}