package com.le.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cars.reels.HomeActivity
import com.cars.reels.Reels

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startActivity(Intent(this, HomeActivity::class.java))

        Reels.enableInterAd(this, true, 4)  // Show interstitial ad every 4 swipes
        Reels.enableBannerAd(this, true, 2) // Show banner ad every 2 swipes
        Reels.enableVideoAd(this, true, 2)  // Show video ad every 2 swipes
    }
}