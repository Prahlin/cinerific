package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.prahlin.cinerific.R

private val ChromeTop = Color(0xFF070005)
private val ChromeMid = Color(0xFF24001F)
private val ChromeBottom = Color(0xFF050003)

@Composable
internal fun CinerificChromeBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    @DrawableRes backgroundResId: Int = R.drawable.chrome_nav_bgfill
) {
    val visibleAlpha = alpha.coerceIn(0f, 1f)

    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    ChromeTop.copy(alpha = visibleAlpha),
                    ChromeMid.copy(alpha = visibleAlpha),
                    ChromeBottom.copy(alpha = visibleAlpha)
                )
            )
        )
    ) {
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { this.alpha = visibleAlpha }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.02f * visibleAlpha))
        )
    }
}
