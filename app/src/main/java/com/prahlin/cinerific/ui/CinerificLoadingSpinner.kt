package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.prahlin.cinerific.R

internal const val CINERIFIC_LOADING_SPINNER_CANVAS_WIDTH = 416f
internal const val CINERIFIC_LOADING_SPINNER_CANVAS_HEIGHT = 422f

private const val SPINNER_VARIANT_SEGMENT_MS = 700
private const val SPINNER_INSTANT_VARIANTS_MS = 2
private const val SPINNER_ACTIVE_LOOP_MS = SPINNER_VARIANT_SEGMENT_MS * 4
private const val SPINNER_CYCLE_MS = SPINNER_ACTIVE_LOOP_MS + SPINNER_INSTANT_VARIANTS_MS

@Composable
internal fun CinerificLoadingSpinner(
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    val spinnerTransition = rememberInfiniteTransition(label = "layered-spinner")
    val redSpin by spinnerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 720f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = SPINNER_CYCLE_MS
                0f at 0 using LinearEasing
                180f at SPINNER_VARIANT_SEGMENT_MS using LinearEasing
                360f at SPINNER_VARIANT_SEGMENT_MS * 2 using LinearEasing
                540f at SPINNER_VARIANT_SEGMENT_MS * 3 using LinearEasing
                720f at SPINNER_ACTIVE_LOOP_MS using LinearEasing
                720f at SPINNER_CYCLE_MS
            }
        ),
        label = "spinner-red-star-rot"
    )
    val wheelSpin by spinnerTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1080f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = SPINNER_CYCLE_MS
                0f at 0 using LinearEasing
                -270f at SPINNER_VARIANT_SEGMENT_MS using LinearEasing
                -540f at SPINNER_VARIANT_SEGMENT_MS * 2 using LinearEasing
                -810f at SPINNER_VARIANT_SEGMENT_MS * 3 using LinearEasing
                -1080f at SPINNER_ACTIVE_LOOP_MS using LinearEasing
                -1080f at SPINNER_CYCLE_MS
            }
        ),
        label = "spinner-wheel-rot"
    )
    val miniStarSpin by spinnerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = SPINNER_CYCLE_MS
                0f at 0 using LinearEasing
                90f at SPINNER_VARIANT_SEGMENT_MS using LinearEasing
                180f at SPINNER_VARIANT_SEGMENT_MS * 2 using LinearEasing
                270f at SPINNER_VARIANT_SEGMENT_MS * 3 using LinearEasing
                360f at SPINNER_ACTIVE_LOOP_MS using LinearEasing
                360f at SPINNER_CYCLE_MS
            }
        ),
        label = "spinner-mini-star-rot"
    )
    val miniStarScale by spinnerTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = SPINNER_CYCLE_MS
                1f at 0 using LinearEasing
                1.75f at SPINNER_VARIANT_SEGMENT_MS using LinearEasing
                1f at SPINNER_VARIANT_SEGMENT_MS * 2 using LinearEasing
                1.75f at SPINNER_VARIANT_SEGMENT_MS * 3 using LinearEasing
                1f at SPINNER_ACTIVE_LOOP_MS using LinearEasing
                1f at SPINNER_CYCLE_MS
            }
        ),
        label = "spinner-mini-star-scale"
    )
    val miniStarAlpha by spinnerTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = SPINNER_CYCLE_MS
                1f at 0 using LinearEasing
                0.25f at SPINNER_VARIANT_SEGMENT_MS using LinearEasing
                1f at SPINNER_VARIANT_SEGMENT_MS * 2 using LinearEasing
                0.25f at SPINNER_VARIANT_SEGMENT_MS * 3 using LinearEasing
                1f at SPINNER_ACTIVE_LOOP_MS using LinearEasing
                1f at SPINNER_CYCLE_MS
            }
        ),
        label = "spinner-mini-star-alpha"
    )

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha.coerceIn(0f, 1f)
        }
    ) {
        CinerificLoadingSpinnerLayer(
            resId = R.drawable.loading_spinner_red_star_centered,
            rotation = redSpin
        )
        CinerificLoadingSpinnerLayer(
            resId = R.drawable.loading_spinner_wheel_centered,
            rotation = wheelSpin
        )
        CinerificLoadingSpinnerLayer(
            resId = R.drawable.loading_spinner_mini_star_centered,
            rotation = miniStarSpin,
            layerScale = miniStarScale,
            alpha = miniStarAlpha
        )
    }
}

@Composable
private fun CinerificLoadingSpinnerLayer(
    @DrawableRes resId: Int,
    rotation: Float,
    layerScale: Float = 1f,
    alpha: Float = 1f
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = rotation
                scaleX = layerScale
                scaleY = layerScale
                this.alpha = alpha
            }
    )
}
