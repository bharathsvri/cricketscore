package com.b47tech.cricketscore.core.ads

object AdConfig {
    // Official Google AdMob Test Ad Units
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    // Production ad unit placeholders (Replace with your actual AdMob IDs before publishing)
    var bannerAdUnitId: String = TEST_BANNER_AD_UNIT_ID
    var interstitialAdUnitId: String = TEST_INTERSTITIAL_AD_UNIT_ID
    var rewardedAdUnitId: String = TEST_REWARDED_AD_UNIT_ID

    // Ad policy rules
    const val INTERSTITIAL_COOLDOWN_MS = 60_000L // Minimum 60 seconds between interstitials
    const val MAX_INTERSTITIALS_PER_SESSION = 2
}
