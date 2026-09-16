package com.b47tech.cricketscore.core.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdManager private constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private var lastInterstitialShownTime: Long = 0
    private var interstitialsShownCount: Int = 0

    fun initialize(context: Context) {
        try {
            MobileAds.initialize(context) {
                // Preload an interstitial for when a match eventually completes
                preloadInterstitial(context)
            }
        } catch (e: Exception) {
            // AdMob initialization failure must never disrupt application flow
            e.printStackTrace()
        }
    }

    fun preloadInterstitial(context: Context) {
        if (isAdLoading || interstitialAd != null) return

        try {
            isAdLoading = true
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context.applicationContext,
                AdConfig.interstitialAdUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isAdLoading = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        isAdLoading = false
                    }
                }
            )
        } catch (e: Exception) {
            isAdLoading = false
            interstitialAd = null
        }
    }

    /**
     * Shows interstitial ONLY at match completion.
     * Enforces frequency limiting:
     * - Cooldown period check
     * - Max session limit check
     * If ad is not ready, offline, or fails: immediately invokes onComplete with zero delay.
     */
    fun showMatchCompletedInterstitial(activity: Activity, onComplete: () -> Unit) {
        val now = System.currentTimeMillis()
        val isCooldownActive = (now - lastInterstitialShownTime) < AdConfig.INTERSTITIAL_COOLDOWN_MS
        val isSessionLimitReached = interstitialsShownCount >= AdConfig.MAX_INTERSTITIALS_PER_SESSION

        val ad = interstitialAd
        if (ad == null || isCooldownActive || isSessionLimitReached) {
            // Ad not available or throttled: continue immediately!
            onComplete()
            // Try preloading for future
            preloadInterstitial(activity)
            return
        }

        try {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastInterstitialShownTime = System.currentTimeMillis()
                    interstitialsShownCount++
                    onComplete()
                    preloadInterstitial(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    onComplete()
                    preloadInterstitial(activity)
                }
            }
            ad.show(activity)
        } catch (e: Exception) {
            // Never crash or block user on ad error
            interstitialAd = null
            onComplete()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AdManager? = null

        fun getInstance(): AdManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AdManager()
                INSTANCE = instance
                instance
            }
        }
    }
}
