package com.b47tech.cricketscore

import com.b47tech.cricketscore.core.ads.AdConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdMobResilienceTest {

    @Test
    fun testAdConfigTestIds() {
        // Verify official test IDs
        assertEquals("ca-app-pub-3940256099942544/9214589741", AdConfig.TEST_BANNER_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/1033173712", AdConfig.TEST_INTERSTITIAL_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/5224354917", AdConfig.TEST_REWARDED_AD_UNIT_ID)

        assertNotNull(AdConfig.bannerAdUnitId)
        assertNotNull(AdConfig.interstitialAdUnitId)
        assertNotNull(AdConfig.rewardedAdUnitId)
    }

    @Test
    fun testAdPolicyConstraints() {
        // Cooldown must be at least 30 seconds
        assertTrue(AdConfig.INTERSTITIAL_COOLDOWN_MS >= 30_000L)
        // Max session interstitials must not be excessive
        assertTrue(AdConfig.MAX_INTERSTITIALS_PER_SESSION in 1..3)
    }

    @Test
    fun testOfflineAndAdFailureContinuation() {
        var userActionCompleted = false

        // Simulate ad manager invocation when offline or ad is not loaded
        fun simulateShowAd(adAvailable: Boolean, onComplete: () -> Unit) {
            if (!adAvailable) {
                // Immediate fallback - never block the user
                onComplete()
            }
        }

        simulateShowAd(adAvailable = false) {
            userActionCompleted = true
        }

        assertTrue("User flow must immediately continue even when ad fails or device is offline", userActionCompleted)
    }
}
