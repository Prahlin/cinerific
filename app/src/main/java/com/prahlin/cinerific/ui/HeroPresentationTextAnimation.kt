package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prahlin.cinerific.R

internal enum class HeroPresentation(
    val frames: List<HeroPresentationFrame>,
    val textBands: HeroPresentationTextBands,
    val copy: HeroPresentationCopy
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
        ),
        textBands = HeroPresentationTextBands(descriptionTopPx = 225f, genresTopPx = 275f),
        copy = HeroPresentationCopy(
            description = "Take to the skies in a riveting new documentary about man's unbounded will to soar",
            genres = listOf("documentary", "adventure", "home improvement"),
            descriptionTopPx = 238f,
            genresTopPx = 283f
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
        ),
        textBands = HeroPresentationTextBands(descriptionTopPx = 125f, genresTopPx = 175f),
        copy = HeroPresentationCopy(
            description = "Young love confronts old traditions in small-town Victorian England",
            genres = listOf("romance", "drama", "history"),
            descriptionTopPx = 136f,
            genresTopPx = 181f
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
        ),
        textBands = HeroPresentationTextBands(descriptionTopPx = 120f, genresTopPx = 158f),
        copy = HeroPresentationCopy(
            description = "Discover the incredible wonders - and blood-curdling dangers - of North America's wildlife",
            genres = listOf("documentary", "nature", "educational"),
            descriptionTopPx = 125f,
            genresTopPx = 163f
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
        ),
        textBands = HeroPresentationTextBands(descriptionTopPx = 140f, genresTopPx = 192f),
        copy = HeroPresentationCopy(
            description = "An extramarital affair unravels a web of lies with lethal consequences",
            genres = listOf("passion", "drama", "suspense"),
            descriptionTopPx = 151f,
            genresTopPx = 201f
        )
    );

    val viewportWidthPx: Float = frames.maxOf { it.exportWidthPx } / PRESENTATION_EXPORT_SCALE
    val viewportHeightPx: Float = frames.maxOf { it.exportHeightPx } / PRESENTATION_EXPORT_SCALE
    val contentBottomPx: Float = copy.genresTopPx + PRESENTATION_BODY_TEXT_LINE_HEIGHT_PX

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

internal data class HeroPresentationTextBands(
    val descriptionTopPx: Float,
    val genresTopPx: Float
)

internal data class HeroPresentationCopy(
    val description: String,
    val genres: List<String>,
    val descriptionTopPx: Float,
    val genresTopPx: Float
)

@Composable
internal fun HeroPresentationTextAnimation(
    presentation: HeroPresentation,
    playKey: Any,
    modifier: Modifier = Modifier
) {
    var elapsedMs by remember(presentation, playKey) { mutableStateOf(0L) }

    LaunchedEffect(presentation, playKey) {
        elapsedMs = 0L
        val startNanos = withFrameNanos { it }
        while (true) {
            elapsedMs = (withFrameNanos { it } - startNanos) / 1_000_000L
        }
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.BottomStart
    ) {
        val scale = maxWidth.value / PRESENTATION_HOME_FRAME_WIDTH
        val density = LocalDensity.current
        val safeBottomPadding = with(density) {
            WindowInsets.safeDrawing.getBottom(this).toDp()
        }
        val elapsed = elapsedMs

        Box(
            modifier = Modifier
                .offset(
                    x = (PRESENTATION_REEL_LEFT_PADDING_PX * scale).dp,
                    y = (
                        (
                            presentation.viewportHeightPx -
                                presentation.contentBottomPx -
                                PRESENTATION_REEL_BOTTOM_PADDING_PX
                        ) * scale
                    ).dp - safeBottomPadding
                )
                .width((presentation.viewportWidthPx * scale).dp)
                .height((presentation.viewportHeightPx * scale).dp),
            contentAlignment = Alignment.TopStart
        ) {
            val titleLayer = HeroPresentationTextLayer.Title
            HeroPresentationFrameBandImage(
                frame = presentation.frames[titleLayer.frameIndex],
                band = titleLayer.band,
                textBands = presentation.textBands,
                scale = scale,
                alpha = titleLayer.alphaFor(elapsed),
                slideYPx = titleLayer.slideFor(elapsed)
            )
            HeroPresentationDescriptionText(
                text = presentation.copy.description,
                topPx = presentation.copy.descriptionTopPx,
                scale = scale,
                alpha = HeroPresentationTextLayer.Description.alphaFor(elapsed)
            )
            HeroPresentationGenresText(
                genres = presentation.copy.genres,
                topPx = presentation.copy.genresTopPx,
                scale = scale,
                alpha = HeroPresentationTextLayer.Genres.alphaFor(elapsed),
                slideYPx = HeroPresentationTextLayer.Genres.slideFor(elapsed)
            )
        }
    }
}

@Composable
private fun HeroPresentationFrameBandImage(
    frame: HeroPresentationFrame,
    band: HeroPresentationTextBand,
    textBands: HeroPresentationTextBands,
    scale: Float,
    alpha: Float,
    slideYPx: Float,
    modifier: Modifier = Modifier
) {
    val clipTopPx = band.clipTop(textBands).coerceIn(0f, frame.figmaHeightPx)
    val clipBottomPx = band.clipBottom(textBands, frame).coerceIn(clipTopPx, frame.figmaHeightPx)
    val clipHeightPx = clipBottomPx - clipTopPx
    if (clipHeightPx <= 0f || alpha <= 0.001f) return

    val painter = painterResource(frame.drawableId)

    Box(
        modifier = modifier
            .width((frame.figmaWidthPx * scale).dp)
            .height((frame.figmaHeightPx * scale).dp)
    ) {
        Canvas(
            modifier = Modifier
                .offset(
                    y = ((clipTopPx + slideYPx) * scale).dp
                )
                .width((frame.figmaWidthPx * scale).dp)
                .height((clipHeightPx * scale).dp)
                .clipToBounds()
                .graphicsLayer {
                    this.alpha = alpha.coerceIn(0f, 1f)
                }
        ) {
            val frameWidth = (frame.figmaWidthPx * scale).dp.toPx()
            val frameHeight = (frame.figmaHeightPx * scale).dp.toPx()
            translate(top = -(clipTopPx * scale).dp.toPx()) {
                with(painter) {
                    draw(size = Size(frameWidth, frameHeight))
                }
            }
        }
    }
}

@Composable
private fun HeroPresentationDescriptionText(
    text: String,
    topPx: Float,
    scale: Float,
    alpha: Float
) {
    if (alpha <= 0.001f) return

    Text(
        text = text,
        maxLines = 1,
        color = Color.White,
        fontFamily = FontFamily.SansSerif,
        fontSize = (PRESENTATION_BODY_TEXT_SIZE_PX * scale).sp,
        lineHeight = (PRESENTATION_BODY_TEXT_LINE_HEIGHT_PX * scale).sp,
        letterSpacing = 0.sp,
        style = HeroPresentationBodyTextStyle(scale),
        modifier = Modifier
            .offset(y = (topPx * scale).dp)
            .graphicsLayer {
                this.alpha = alpha.coerceIn(0f, 1f)
            }
    )
}

@Composable
private fun HeroPresentationGenresText(
    genres: List<String>,
    topPx: Float,
    scale: Float,
    alpha: Float,
    slideYPx: Float
) {
    if (alpha <= 0.001f) return

    Row(
        horizontalArrangement = Arrangement.spacedBy((PRESENTATION_GENRE_GAP_PX * scale).dp),
        modifier = Modifier
            .offset(y = ((topPx + slideYPx) * scale).dp)
            .graphicsLayer {
                this.alpha = alpha.coerceIn(0f, 1f)
            }
    ) {
        genres.forEach { genre ->
            Text(
                text = genre,
                maxLines = 1,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontSize = (PRESENTATION_BODY_TEXT_SIZE_PX * scale).sp,
                lineHeight = (PRESENTATION_BODY_TEXT_LINE_HEIGHT_PX * scale).sp,
                letterSpacing = 0.sp,
                style = HeroPresentationBodyTextStyle(scale)
            )
        }
    }
}

private fun HeroPresentationBodyTextStyle(scale: Float): TextStyle = TextStyle(
    shadow = Shadow(
        color = Color.Black.copy(alpha = 0.78f),
        offset = Offset(
            x = PRESENTATION_TEXT_SHADOW_PX * scale,
            y = PRESENTATION_TEXT_SHADOW_PX * scale
        ),
        blurRadius = PRESENTATION_TEXT_SHADOW_BLUR_PX * scale
    )
)

private enum class HeroPresentationTextBand {
    Title,
    Description,
    Genres;

    fun clipTop(textBands: HeroPresentationTextBands): Float = when (this) {
        Title -> 0f
        Description -> textBands.descriptionTopPx
        Genres -> textBands.genresTopPx
    }

    fun clipBottom(
        textBands: HeroPresentationTextBands,
        frame: HeroPresentationFrame
    ): Float = when (this) {
        Title -> textBands.descriptionTopPx
        Description -> textBands.genresTopPx
        Genres -> frame.figmaHeightPx
    }
}

private enum class HeroPresentationTextLayer(
    val frameIndex: Int,
    val band: HeroPresentationTextBand,
    val revealStartMs: Int,
    val revealDurationMs: Int,
    val movesVertically: Boolean = false
) {
    Title(
        frameIndex = 1,
        band = HeroPresentationTextBand.Title,
        revealStartMs = 0,
        revealDurationMs = 500
    ),
    Description(
        frameIndex = 2,
        band = HeroPresentationTextBand.Description,
        revealStartMs = 500,
        revealDurationMs = 500
    ),
    Genres(
        frameIndex = 3,
        band = HeroPresentationTextBand.Genres,
        revealStartMs = 1_000,
        revealDurationMs = 550,
        movesVertically = true
    );

    fun alphaFor(elapsedMs: Long): Float = revealProgress(elapsedMs)

    fun slideFor(elapsedMs: Long): Float {
        if (!movesVertically) return 0f

        return PRESENTATION_GENRE_SLIDE_PX * (1f - revealProgress(elapsedMs))
    }

    private fun revealProgress(elapsedMs: Long): Float {
        val linearProgress = ((elapsedMs - revealStartMs).toFloat() / revealDurationMs)
            .coerceIn(0f, 1f)
        return lineRevealProgress(linearProgress)
    }
}

private const val PRESENTATION_EXPORT_SCALE = 2f
private const val PRESENTATION_HOME_FRAME_WIDTH = 1194f
private const val PRESENTATION_REEL_LEFT_PADDING_PX = 25f
private const val PRESENTATION_REEL_BOTTOM_PADDING_PX = 112.5f
private const val PRESENTATION_GENRE_SLIDE_PX = 22f
private const val PRESENTATION_BODY_TEXT_SIZE_PX = 20f
private const val PRESENTATION_BODY_TEXT_LINE_HEIGHT_PX = 30.5f
private const val PRESENTATION_GENRE_GAP_PX = 54f
private const val PRESENTATION_TEXT_SHADOW_PX = 1.4f
private const val PRESENTATION_TEXT_SHADOW_BLUR_PX = 1.1f

private fun lineRevealProgress(progress: Float): Float {
    val t = progress.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}
