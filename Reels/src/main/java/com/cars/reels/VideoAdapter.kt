package com.cars.reels

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.Locale
import kotlin.math.acos
import kotlin.time.measureTimedValue


class VideoAdapter(
    private val videoPaths: List<VideoModel>,
    private val viewPager: ViewPager2,
    private val context: Activity,
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private var swipeCount = 0
    private var rewardedAd: RewardedAd? = null
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var mPlayer: MediaPlayer
    private var speechRecognizer: SpeechRecognizer? = null
    private var isAutopilotEnabled = false
    private var isListening = false
    private var currentPosition = -1
    private var currentVideoHolder: VideoViewHolder? = null


    init {
        attachViewPager(viewPager)
        loadInterAd()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoModel = videoPaths[position]

        // Set title and description
        holder.videoTitle.text = videoModel.videoTitle
        holder.videoDesc.text = videoModel.videoDesc

        // Stop any previous playback before setting a new video
        holder.videoView.stopPlayback()
        holder.videoView.setVideoPath(videoModel.videoUrl)

        holder.videoView.setOnPreparedListener { mediaPlayer ->
            mPlayer = mediaPlayer
            mediaPlayer.setOnInfoListener { _, what, _ ->
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    holder.videoView.visibility = View.VISIBLE // Show when ready
                }
                false
            }

            // Adjust video scale (avoid stretching)
            val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
            val screenRatio = holder.videoView.width / holder.videoView.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                holder.videoView.scaleX = scaleX
            } else {
                holder.videoView.scaleY = 1f / scaleX
            }

            // Start playback only if it's the current item in ViewPager
            if (position == viewPager.currentItem) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
        }

        // Handle playback errors
        holder.videoView.setOnErrorListener { _, what, extra ->
            Log.e("VideoViewError", "Error: $what, Extra: $extra")

            // Stop playback and retry setting the video
            holder.videoView.stopPlayback()
            holder.videoView.setVideoPath(videoModel.videoUrl)
            holder.videoView.start()

            true // Error handled
        }

        // Pause videos when swiping away, start when visible
        if (position == viewPager.currentItem) {
            currentVideoHolder = holder
            holder.videoView.start()
        } else {
            holder.videoView.pause()
        }

        // Handle button clicks (like, dislike, share, etc.)
        holder.enableAutoPilot.isChecked = isAutopilotEnabled
        holder.enableAutoPilot.setOnCheckedChangeListener { _, isChecked ->
            isAutopilotEnabled = isChecked
            if (isChecked) startListening() else stopListening()
        }

        holder.like.setOnClickListener {
            currentVideoHolder?.likeTxt?.text = "1"
            showToast("Video Liked")
            highlightIcon(holder.like)
        }

        holder.dislike.setOnClickListener {
            currentVideoHolder?.likeTxt?.text = "0"
            showToast("Video Disliked")
            highlightIcon(holder.dislike)
        }

        holder.comment.setOnClickListener {
            showToast("Comment Posted")
            highlightIcon(holder.comment)
        }

        holder.share.setOnClickListener {
            showToast("Shared")
            highlightIcon(holder.share)
        }

        holder.rotate.setOnClickListener {
            showToast("Rotated")
            highlightIcon(holder.rotate)
        }
    }


    private fun attachViewPager(viewPager: ViewPager2) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                swipeCount++

                val bannerAdParams = Reels.getBannerAdParams()
                val interAdParams = Reels.getInterAdParams()
                val videoAdParams = Reels.getVideoAdParams()

                Log.d("AdDebug", "Swipe Count: $swipeCount")

                val shouldRunBanner = bannerAdParams["shouldRunBanner"] as Boolean
                val bannerFrequency = bannerAdParams["bannerFrequency"] as Int
                val showBanner = shouldRunBanner && (swipeCount % bannerFrequency == 0)

                val shouldRunInter = interAdParams["shouldRunInter"] as Boolean
                val interFrequency = interAdParams["interFrequency"] as Int
                val showInterstitial = shouldRunInter && (swipeCount % interFrequency == 0)

                val shouldRunVideo = videoAdParams["shouldRunVideo"] as Boolean
                val videoFrequency = videoAdParams["videoFrequency"] as Int
                val showVideoAd = shouldRunVideo && (swipeCount % videoFrequency == 0)

                Log.d("AdDebug", "Banner - shouldRun: $shouldRunBanner, frequency: $bannerFrequency, Modulo: ${swipeCount % bannerFrequency}, showBanner: $showBanner")
                Log.d("AdDebug", "Interstitial - shouldRun: $shouldRunInter, frequency: $interFrequency, Modulo: ${swipeCount % interFrequency}, showInterstitial: $showInterstitial")
                Log.d("AdDebug", "VideoAd - shouldRun: $shouldRunVideo, frequency: $videoFrequency, Modulo: ${swipeCount % videoFrequency}, showVideoAd: $showVideoAd")

                when {
                    showInterstitial -> {
                        currentVideoHolder?.adView?.visibility = View.GONE
                        showInterAd()
                    }
                    showVideoAd -> {
                        currentVideoHolder?.adView?.visibility = View.GONE
                        showRewardedVideoAd()
                    }
                    showBanner -> {
                        currentVideoHolder?.adView?.visibility = View.VISIBLE
                        showBannerAd(currentVideoHolder?.adView!!)
                    }
                    else -> {
                        currentVideoHolder?.adView?.visibility = View.GONE
                        loadInterAd()
                        loadRewardedVideoAd()
                    }
                }
            }
        })
    }



    override fun getItemCount(): Int = videoPaths.size

    private fun startListening() {
        if (isListening) return

        isListening = true
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {
                    if (isListening) restartListening()
                }

                override fun onResults(results: Bundle?) {
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                        ?.let {
                            handleCommand(it.lowercase(Locale.getDefault()))
                        }
                    if (isListening) restartListening()
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        speechRecognizer?.startListening(createSpeechRecognizerIntent())


    }

    private fun showInterAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(context)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

    private fun showBannerAd(adView: AdView) {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun loadRewardedVideoAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            context.getString(R.string.admob_video_id),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {

                    rewardedAd = ad
                    rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {

                            isAutopilotEnabled = true
                            if (::mPlayer.isInitialized && !mPlayer.isPlaying) {
                                mPlayer.start()
                                currentVideoHolder?.videoView?.resume()
                            }

                            rewardedAd = null
                            showToast("Rewarded Ad Closed")
                            loadRewardedVideoAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            rewardedAd = null
                            showToast("Rewarded Ad Failed to Show: ${adError.message}")
                        }

                        override fun onAdShowedFullScreenContent() {
                            isAutopilotEnabled = false
                            currentVideoHolder?.videoView?.pause()
                            mPlayer.pause()
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    showToast("Rewarded Ad Failed to Load: ${adError.message}")
                }
            }
        )
    }


    private fun showRewardedVideoAd() {
        rewardedAd?.let { ad ->
            ad.show(context) { rewardItem: RewardItem ->
            }
        } ?: run {
            loadRewardedVideoAd()
        }
    }

    private fun stopListening() {
        isListening = false
        speechRecognizer?.apply {
            stopListening()
            destroy()
        }
        speechRecognizer = null
    }

    private fun restartListening() {
        speechRecognizer?.startListening(createSpeechRecognizerIntent())
    }

    private fun createSpeechRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command (e.g., like, dislike, or next)")
        }
    }

    private fun handleCommand(command: String) {
        when (command) {
            "like" -> handleLike(currentVideoHolder?.like, currentVideoHolder?.dislike)
            "dislike" -> handleLikeDislike(currentVideoHolder?.dislike, currentVideoHolder?.like)
            "next" -> moveToNextVideo()
        }
    }

    private fun loadInterAd() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context, context.getString(R.string.admob_inter_id), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd

                    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            isAutopilotEnabled = true

                            mPlayer.start()
                            currentVideoHolder?.videoView?.start()
                            mInterstitialAd = null
                            showToast("Ad Closed")
                            loadInterAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            mInterstitialAd = null
                            showToast("Ad Failed to Show: ${adError.message}")
                        }

                        override fun onAdShowedFullScreenContent() {
                            isAutopilotEnabled = false
                            currentVideoHolder?.videoView?.pause()
                            mPlayer.pause()
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                    showToast("Ad Failed to Load: ${loadAdError.message}")
                }
            }
        )
    }


    private fun moveToNextVideo() {
        if (isAutopilotEnabled) {
            val nextPosition = viewPager.currentItem + 1
            if (nextPosition < videoPaths.size) {
                currentPosition = nextPosition
                viewPager.setCurrentItem(nextPosition, true)
                showToast("Switched to next video")
            } else {
                showToast("No more videos")
            }
        }
    }

    private fun handleLike(selectedIcon: ImageView?, otherIcon: ImageView?) {
        currentVideoHolder?.likeTxt?.text = "1"
        selectedIcon?.let { highlightIcon(it) }
        otherIcon?.clearColorFilter()
    }

    private fun handleLikeDislike(selectedIcon: ImageView?, otherIcon: ImageView?) {
        currentVideoHolder?.likeTxt?.text = "0"
        selectedIcon?.let { highlightIcon(it) }
        otherIcon?.clearColorFilter()
    }

    private fun highlightIcon(icon: ImageView) {
        val orangeTint = 0xFFFFA500.toInt()
        icon.setColorFilter(orangeTint, android.graphics.PorterDuff.Mode.SRC_ATOP)
        icon.postDelayed({ icon.clearColorFilter() }, 300)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: VideoView = itemView.findViewById(R.id.videoView)
        val videoTitle: TextView = itemView.findViewById(R.id.vidTitle)
        val videoDesc: TextView = itemView.findViewById(R.id.vidDescription)
        val like: ImageView = itemView.findViewById(R.id.like)
        val likeTxt: TextView = itemView.findViewById(R.id.likesText)
        val dislike: ImageView = itemView.findViewById(R.id.dislike)
        val comment: ImageView = itemView.findViewById(R.id.comment)
        val share: ImageView = itemView.findViewById(R.id.share)
        val rotate: ImageView = itemView.findViewById(R.id.rotate)
        val adView : AdView  = itemView.findViewById(R.id.adView)
        val enableAutoPilot: SwitchCompat = itemView.findViewById(R.id.switchButton)
    }

}