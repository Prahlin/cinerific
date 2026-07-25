package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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

private const val SPINNER_LOOP_MS = 2_800
private const val RED_STAR_ROTATION_DEGREES = 720f
private const val WHEEL_ROTATION_DEGREES = -1_080f
private const val MINI_STAR_ROTATION_DEGREES = 360f

@Composable
internal fun CinerificLoadingSpinner(
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    val spinnerTransition = rememberInfiniteTransition(label = "loading-spinner")
    val redSpin by spinnerTransition.animateFloat(
        initialValue = 0f,
        targetValue = RED_STAR_ROTATION_DEGREES,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SPINNER_LOOP_MS,
                easing = LinearEasing
            )
        ),
        label = "spinner-red-star-rotation"
    )
    val wheelSpin by spinnerTransition.animateFloat(
        initialValue = 0f,
        targetValue = WHEEL_ROTATION_DEGREES,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SPINNER_LOOP_MS,
                easing = LinearEasing
            )
        ),
        label = "spinner-wheel-rotation"
    )
    val miniStarSpin by spinnerTransition.animateFloat(
        initialValue = 0f,
        targetValue = MINI_STAR_ROTATION_DEGREES,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SPINNER_LOOP_MS,
                easing = LinearEasing
            )
        ),
        label = "spinner-mini-star-rotation"
    )

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha.coerceIn(0f, 1f)
        }
    ) {
        CinerificLoadingSpinnerLayer(
            resId = R.drawable.loading_spinner_red_star_normalized,
            rotation = redSpin
        )
        CinerificLoadingSpinnerLayer(
            resId = R.drawable.loading_spinner_wheel_centered,
            rotation = wheelSpin
        )
        CinerificLoadingSpinnerLayer(
            resId = R.drawable.loading_spinner_mini_star_centered,
            rotation = miniStarSpin
        )
    }
}

@Composable
private fun CinerificLoadingSpinnerLayer(
    @DrawableRes resId: Int,
    rotation: Float
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = rotation
            }
    )
}
