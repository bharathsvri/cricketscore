package com.b47tech.cricketscore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.b47tech.cricketscore.core.ads.AdConfig
import com.b47tech.cricketscore.ui.theme.DarkSurface
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdBanner(
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current
    var isAdLoaded by remember { mutableStateOf(false) }

    if (isPreview) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(DarkSurface),
            contentAlignment = Alignment.Center
        ) {
            // Preview placeholder
        }
    } else {
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = AdConfig.bannerAdUnitId
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isAdLoaded = true
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            isAdLoaded = false
                        }
                    }
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
