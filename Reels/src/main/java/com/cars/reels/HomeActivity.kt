package com.cars.reels

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.MobileAds

class HomeActivity : AppCompatActivity()  {


    private lateinit var viewPager: ViewPager2
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        viewPager = findViewById(R.id.viewPager)

        checkPermissions()
        populateUi()
        initialize()


        val highFreq = 2   // Show ad every 2 swipes
        val lowFreq = 4    // Show ad every 4 swipes

        Reels.initialize(
            shouldRunBanner = true, bannerFreq = highFreq,
            shouldRunInter = true, interFreq = lowFreq,
            shouldRunVideo = true, videoFreq = highFreq
        )

    }

    private fun initialize(){
        MobileAds.initialize(this
        ) { Toast.makeText(this@HomeActivity, " successful ", Toast.LENGTH_SHORT).show() }
    }
    @Suppress("DEPRECATION")
    private fun populateUi(){
        videoAdapter = VideoAdapter(getVideoList(),viewPager,this)
        viewPager.adapter = videoAdapter
    }
    private fun getVideoList(): List<VideoModel> {
        val videoPaths  = mutableListOf(
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        )

        val randomTitles = listOf(
            "Exciting Journey",
            "Amazing Adventure",
            "Unforgettable Moments",
            "Epic Experience",
            "New Horizons",
            "Beyond the Limits",
            "Mystical Quest",
            "Vivid Escapes",
            "Timeless Adventures",
            "Infinite Possibilities",
            "Uncharted Territory",
            "Wonders Await",
            "Journey to the Unknown",
            "Stunning Landscapes",
            "Boundless Realms",
            "Thrilling Escapade",
            "Majestic Views",
            "Boundless Realms",
            "Thrilling Escapade",
            "Majestic Views"
        )

        val randomDescriptions = listOf(
            "Join us on this unforgettable adventure. Explore the unknown and uncover secrets along the way.",
            "This is an exciting journey full of surprises. Expect the unexpected as you dive in.",
            "Experience the thrill of new possibilities. A world of endless adventure awaits.",
            "Get ready for an epic adventure unlike any other. Adventure, excitement, and more are waiting for you.",
            "A new horizon awaits you in this amazing journey. Discover breathtaking views and hidden treasures.",
            "Push the limits with this exhilarating experience. Adventure beyond your imagination is just around the corner.",
            "Embark on a mystical quest filled with wonders. Unlock the mysteries of an ancient world.",
            "Escape to vivid places and experiences beyond imagination. A new adventure is calling your name.",
            "Dive into timeless adventures that leave a mark. Be part of stories that echo through history.",
            "Explore infinite possibilities in an ever-evolving world. Journey through realms that change with every step.",
            "Venture into uncharted territory where surprises await. The path less traveled is waiting for you.",
            "Wonders await at every corner of this incredible journey. Expect awe and wonder at every turn.",
            "Step into the unknown and discover hidden gems. Every twist and turn holds a new surprise.",
            "Experience stunning landscapes like never before. Witness nature’s finest at every moment.",
            "Enter boundless realms of adventure and exploration. Let your curiosity take you to new heights.",
            "Join the thrilling escapade of a lifetime. Your adventure begins here, and it promises to be unforgettable.",
            "Witness majestic views that take your breath away. Every moment will leave you in awe.",
            "Enter boundless realms of adventure and exploration. Let your curiosity take you to new heights.",
            "Join the thrilling escapade of a lifetime. Your adventure begins here, and it promises to be unforgettable.",
            "Witness majestic views that take your breath away. Every moment will leave you in awe."
        )



        return videoPaths.mapIndexed { index, videoUrl ->
            VideoModel(
                videoUrl = videoUrl,
                videoTitle = randomTitles.getOrElse(index) { "Untitled Video" },
                videoDesc = randomDescriptions.getOrElse(index) { "No description available." }
            )
        }
    }
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        }
    }


}
