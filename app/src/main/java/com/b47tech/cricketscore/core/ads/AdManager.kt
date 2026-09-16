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
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager private constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private var lastInterstitialShownTime: Long = 0
    private var interstitialsShownCount: Int = 0

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    fun initialize(context: Context) {
        try {
            MobileAds.initialize(context) {
                // Preload an interstitial for when a match eventually completes
                preloadInterstitial(context)
                preloadRewarded(context)
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

    fun preloadRewarded(context: Context) {
        if (isRewardedLoading || rewardedAd != null) return

        try {
            isRewardedLoading = true
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context.applicationContext,
                AdConfig.rewardedAdUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        isRewardedLoading = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        isRewardedLoading = false
                    }
                }
            )
        } catch (e: Exception) {
            isRewardedLoading = false
            rewardedAd = null
        }
    }

    /**
     * Shows Rewarded Ad for premium actions like PDF export or Database backup.
     * If the ad is not ready, device is offline, or ad fails:
     * - Gracefully invokes onRewardEarned so user is never blocked.
     */
    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdDismissed: (() -> Unit)? = null
    ) {
        val ad = rewardedAd
        if (ad == null) {
            // Ad unavailable or offline: grant reward gracefully
            onRewardEarned()
            onAdDismissed?.invoke()
            preloadRewarded(activity)
            return
        }

        var userEarnedReward = false

        try {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    if (userEarnedReward) {
                        onRewardEarned()
                    }
                    onAdDismissed?.invoke()
                    preloadRewarded(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    // Fail gracefully: grant reward
                    onRewardEarned()
                    onAdDismissed?.invoke()
                    preloadRewarded(activity)
                }
            }

            ad.show(activity) { _ ->
                userEarnedReward = true
            }
        } catch (e: Exception) {
            rewardedAd = null
            onRewardEarned()
            onAdDismissed?.invoke()
            preloadRewarded(activity)
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
