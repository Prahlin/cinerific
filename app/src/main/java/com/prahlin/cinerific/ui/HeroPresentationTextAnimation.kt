package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prahlin.cinerific.R

internal enum class HeroPresentation(
    val frames: List<HeroPresentationFrame>
) {
    LightAsAir(
        frames = listOf(
            HeroPresentationFrame(R.drawable.light_as_air_presentation_default, 1490f, 616f),
            HeroPresentationFrame(R.drawable.light_as_air_presentation_variant_2, 1490f, 602f),
            HeroPresentationFrame(R.drawable.light_as_air_presentation_variant_3, 1738f, 602f),
            HeroPresentationFrame(R.drawable.light_as_air_presentation_variant_4, 1738f, 616f),
            HeroPresentationFrame(R.drawable.light_as_air_presentation_variant_5, 1738f, 602f),
            HeroPresentationFrame(R.drawable.light_as_air_presentation_variant_6, 1490f, 602f),
            HeroPresentationFrame(R.drawable.light_as_air_presentation_variant_7, 1490f, 602f)
        )
    ),
    Infatuation(
        frames = listOf(
            HeroPresentationFrame(R.drawable.infatuation_presentation_default, 1392f, 413f),
            HeroPresentationFrame(R.drawable.infatuation_presentation_variant_2, 1392f, 413f),
            HeroPresentationFrame(R.drawable.infatuation_presentation_variant_3, 1392f, 405f),
            HeroPresentationFrame(R.drawable.infatuation_presentation_variant_4, 1392f, 413f),
            HeroPresentationFrame(R.drawable.infatuation_presentation_variant_5, 1392f, 413f),
            HeroPresentationFrame(R.drawable.infatuation_presentation_variant_6, 1392f, 413f),
            HeroPresentationFrame(R.drawable.infatuation_presentation_variant_7, 1392f, 409f)
        )
    ),
    IntoTheWild(
        frames = listOf(
            HeroPresentationFrame(R.drawable.into_the_wild_presentation_default, 1878f, 376f),
            HeroPresentationFrame(R.drawable.into_the_wild_presentation_variant_2, 1878f, 376f),
            HeroPresentationFrame(R.drawable.into_the_wild_presentation_variant_3, 1878f, 376f),
            HeroPresentationFrame(R.drawable.into_the_wild_presentation_variant_4, 1878f, 376f),
            HeroPresentationFrame(R.drawable.into_the_wild_presentation_variant_5, 1878f, 376f),
            HeroPresentationFrame(R.drawable.into_the_wild_presentation_variant_6, 1878f, 376f),
            HeroPresentationFrame(R.drawable.into_the_wild_presentation_variant_7, 1878f, 376f)
        )
    ),
    MorbidTemptations(
        frames = listOf(
            HeroPresentationFrame(R.drawable.morbid_temptations_presentation_default, 1608f, 473f),
            HeroPresentationFrame(R.drawable.morbid_temptations_presentation_variant_2, 1608f, 473f),
            HeroPresentationFrame(R.drawable.morbid_temptations_presentation_variant_3, 1608f, 473f),
            HeroPresentationFrame(R.drawable.morbid_temptations_presentation_variant_4, 1608f, 473f),
            HeroPresentationFrame(R.drawable.morbid_temptations_presentation_variant_5, 1608f, 473f),
            HeroPresentationFrame(R.drawable.morbid_temptations_presentation_variant_6, 1608f, 473f),
            HeroPresentationFrame(R.drawable.morbid_temptations_presentation_variant_7, 1608f, 473f)
        )
    );

    val viewportWidthPx: Float = frames.maxOf { it.exportWidthPx } / PRESENTATION_EXPORT_SCALE
    val viewportHeightPx: Float = frames.maxOf { it.exportHeightPx } / PRESENTATION_EXPORT_SCALE

    companion object {
        fun forReelIndex(index: Int): HeroPresentation = values()[index % values().size]
    }
}

internal data class HeroPresentationFrame(
    @DrawableRes val drawableId: Int,
    val exportWidthPx: Float,
    val exportHeightPx: Float
) {
    val figmaWidthPx: Float = exportWidthPx / PRESENTATION_EXPORT_SCALE
    val figmaHeightPx: Float = exportHeightPx / PRESENTATION_EXPORT_SCALE
}

@Composable
internal fun HeroPresentationTextAnimation(
    presentation: HeroPresentation,
    playKey: Any,
    modifier: Modifier = Modifier
) {
    var currentFrameIndex by remember(presentation, playKey) { mutableStateOf(0) }
    var nextFrameIndex by remember(presentation, playKey) { mutableStateOf<Int?>(null) }
    val transitionProgress = remember(presentation, playKey) { Animatable(0f) }

    LaunchedEffect(presentation, playKey) {
        currentFrameIndex = 0
        nextFrameIndex = null
        transitionProgress.snapTo(0f)

        PresentationHoldMs.forEachIndexed { index, holdMs ->
            kotlinx.coroutines.delay(holdMs.toLong())
            nextFrameIndex = index + 1
            transitionProgress.snapTo(0f)
            transitionProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = PRESENTATION_TRANSITION_MS,
                    easing = FigmaEaseInOutBack
                )
            )
            currentFrameIndex = index + 1
            nextFrameIndex = null
            transitionProgress.snapTo(0f)
        }
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val scale = maxWidth.value / PRESENTATION_HOME_FRAME_WIDTH
        val progress = transitionProgress.value.coerceIn(0f, 1f)
        val nextIndex = nextFrameIndex
        val currentScale = if (nextIndex == null) 1f else 1f + (progress * 0.025f)
        val nextScale = 0.94f + (progress * 0.06f)

        Box(
            modifier = Modifier
                .width((presentation.viewportWidthPx * scale).dp)
                .height((presentation.viewportHeightPx * scale).dp),
            contentAlignment = Alignment.Center
        ) {
            HeroPresentationFrameImage(
                frame = presentation.frames[currentFrameIndex],
                scale = scale,
                alpha = if (nextIndex == null) 1f else 1f - progress,
                visualScale = currentScale
            )
            if (nextIndex != null) {
                HeroPresentationFrameImage(
                    frame = presentation.frames[nextIndex],
                    scale = scale,
                    alpha = progress,
                    visualScale = nextScale
                )
            }
        }
    }
}

@Composable
private fun HeroPresentationFrameImage(
    frame: HeroPresentationFrame,
    scale: Float,
    alpha: Float,
    visualScale: Float,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(frame.drawableId),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .width((frame.figmaWidthPx * scale).dp)
            .height((frame.figmaHeightPx * scale).dp)
            .graphicsLayer {
                this.alpha = alpha.coerceIn(0f, 1f)
                scaleX = visualScale
                scaleY = visualScale
            }
    )
}

private const val PRESENTATION_EXPORT_SCALE = 2f
private const val PRESENTATION_HOME_FRAME_WIDTH = 1194f
private const val PRESENTATION_TRANSITION_MS = 3_000

private val PresentationHoldMs = listOf(
    800,
    800,
    800,
    1_200,
    800,
    800
)

private val FigmaEaseInOutBack = Easing { fraction ->
    val c1 = 1.70158f
    val c2 = c1 * 1.525f
    if (fraction < 0.5f) {
        val progress = 2f * fraction
        (progress * progress * ((c2 + 1f) * progress - c2)) / 2f
    } else {
        val progress = 2f * fraction - 2f
        (progress * progress * ((c2 + 1f) * progress + c2) + 2f) / 2f
    }
}
