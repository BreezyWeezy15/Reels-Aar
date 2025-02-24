package com.cars.reels

import android.content.Context
import android.util.Log


object Reels {
    private var shouldRunBanner: Boolean = false
    private var bannerFrequency: Int = 1

    private var shouldRunInter: Boolean = false
    private var interFrequency: Int = 1

    private var shouldRunVideo: Boolean = false
    private var videoFrequency: Int = 1

    fun initialize(shouldRunBanner: Boolean, bannerFreq: Int,
                   shouldRunInter: Boolean, interFreq: Int,
                   shouldRunVideo: Boolean, videoFreq: Int) {
        this.shouldRunBanner = shouldRunBanner
        this.bannerFrequency = bannerFreq

        this.shouldRunInter = shouldRunInter
        this.interFrequency = interFreq

        this.shouldRunVideo = shouldRunVideo
        this.videoFrequency = videoFreq
    }

    fun getBannerAdParams(): Map<String, Any> {
        return mapOf("shouldRunBanner" to shouldRunBanner, "bannerFrequency" to bannerFrequency)
    }

    fun getInterAdParams(): Map<String, Any> {
        return mapOf("shouldRunInter" to shouldRunInter, "interFrequency" to interFrequency)
    }

    fun getVideoAdParams(): Map<String, Any> {
        return mapOf("shouldRunVideo" to shouldRunVideo, "videoFrequency" to videoFrequency)
    }
}

