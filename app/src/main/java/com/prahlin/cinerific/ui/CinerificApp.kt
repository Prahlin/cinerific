package com.prahlin.cinerific.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

private const val FIGMA_FRAME_WIDTH = 1194f
private const val FIGMA_FRAME_HEIGHT = 834f

private val ColorFrame1Background = Color(0xFF000000)
private val ColorFrame2Background = Color(0xFF62070D)
private val ColorFrame3Background = Color(0xFF1F1F1F)
private val ColorFrame4Background = Color(0xFF1F1F1F)

@Composable
fun CinerificApp() {
    val currentFrame = remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentFrame.intValue) {
            0 -> IntroFrame1FromFigma()
            1 -> IntroFrame2FromFigma()
            2 -> IntroFrame3FromFigma()
            3 -> SignInFrameFromFigma()
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (currentFrame.intValue > 0) currentFrame.intValue-- },
                enabled = currentFrame.intValue > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    disabledContainerColor = Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = Color.White)
                Text("  Prev", color = Color.White)
            }

            Spacer(modifier = Modifier.weight(0.5f))

            Text(
                text = "Frame ${currentFrame.intValue + 1}/4",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Button(
                onClick = { if (currentFrame.intValue < 3) currentFrame.intValue++ },
                enabled = currentFrame.intValue < 3,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    disabledContainerColor = Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Next  ", color = Color.White)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.White)
            }
        }
    }
}

@Composable
private fun IntroFrame1FromFigma() {
    // Node 474:7309 has only a pure black fill.
    FigmaStage(background = ColorFrame1Background) { }
}

@Composable
private fun IntroFrame2FromFigma() {
    // Node 474:7311 with Logo Slide 1 instance 478:6917.
    val transition = rememberInfiniteTransition(label = "logo-slide-1")
    val slideYOffset by transition.animateFloat(
        initialValue = -417f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slide-y"
    )

    FigmaStage(background = ColorFrame2Background) { scale ->
        LogoSlide1Layer(scale = scale, yOffset = slideYOffset)
    }
}

@Composable
private fun IntroFrame3FromFigma() {
    // Node 476:6937 with PROMO background1 2 (793:7049) and Logo Slide 2 (478:6970).
    FigmaStage(background = ColorFrame3Background) { scale ->
        PromoBackgroundLayer(scale = scale, darkOverlay = 0f)
        LogoSlide2Layer(scale = scale, topY = -417f)
    }
}

@Composable
private fun SignInFrameFromFigma() {
    // Node 478:6975 + Star Overlay 482:7696.
    val spinTransition = rememberInfiniteTransition(label = "spinner")
    val spin by spinTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing)
        ),
        label = "spinner-rot"
    )

    FigmaStage(background = ColorFrame4Background) { scale ->
        PromoBackgroundLayer(scale = scale, darkOverlay = 0.5f)
        LogoSlide2Layer(scale = scale, topY = 0f)

        // Node 480:7611 (Ellipse 43) x:894 y:266 size:200x200.
        Box(
            modifier = Modifier
                .absoluteOffset(x = figma(894f, scale), y = figma(266f, scale))
                .requiredSize(figma(200f, scale), figma(200f, scale))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFE8E8E8), Color(0xFF8A8A8A), Color(0xFF1D1D1D))
                    )
                )
        )

        // Node 485:7113 loading spinner instance at x:447 y:267 size:300x300.
        Box(
            modifier = Modifier
                .absoluteOffset(x = figma(447f, scale), y = figma(267f, scale))
                .requiredSize(figma(300f, scale), figma(300f, scale))
                .graphicsLayer(rotationZ = spin)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        colors = listOf(
                            Color(0x00FFFFFF),
                            Color(0x66FFE08D),
                            Color(0xCCFFB347),
                            Color(0x00FFFFFF)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .absoluteOffset(x = figma(501f, scale), y = figma(321f, scale))
                .requiredSize(figma(189f, scale), figma(189f, scale))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFFD978), Color(0x99FFB347), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
private fun LogoSlide1Layer(scale: Float, yOffset: Float) {
    Box(
        modifier = Modifier
            .absoluteOffset(y = figma(yOffset, scale))
            .requiredSize(figma(1194f, scale), figma(1668f, scale))
    ) {
        // Node I478:6917;478:6923 logo simple 1 at x:447 y:0 size:300x214.
        FigmaImageBlock(
            x = 447f,
            y = 0f,
            w = 300f,
            h = 214f,
            scale = scale,
            colors = listOf(Color(0xFFEBD07F), Color(0xFF8A5B10))
        )

        // Node I478:6917;478:6925 logo eyes only 2 at x:453 y:1454 size:300x214.
        FigmaImageBlock(
            x = 453f,
            y = 1454f,
            w = 300f,
            h = 214f,
            scale = scale,
            colors = listOf(Color(0xFF7AC7FF), Color(0xFF2D466B))
        )
    }
}

@Composable
private fun LogoSlide2Layer(scale: Float, topY: Float) {
    Box(
        modifier = Modifier
            .absoluteOffset(y = figma(topY, scale))
            .requiredSize(figma(1194f, scale), figma(1668f, scale))
    ) {
        // Node I478:6970;478:6963 logo simple 1 at x:222 y:567 size:750x535.
        FigmaImageBlock(
            x = 222f,
            y = 567f,
            w = 750f,
            h = 535f,
            scale = scale,
            colors = listOf(Color(0xFFCEC9B8), Color(0xFF5E4A2A), Color(0xFF222222))
        )

        // Node I478:6970;478:6964 logo eyes only 2 at same bounds.
        FigmaImageBlock(
            x = 222f,
            y = 567f,
            w = 750f,
            h = 535f,
            scale = scale,
            colors = listOf(Color.Transparent, Color(0x2282D7FF), Color.Transparent)
        )
    }
}

@Composable
private fun PromoBackgroundLayer(scale: Float, darkOverlay: Float) {
    // Nodes 793:7049 / 793:7050 at x:0 y:0 size:1584x1584.
    Box(
        modifier = Modifier
            .absoluteOffset(x = figma(0f, scale), y = figma(0f, scale))
            .requiredSize(figma(1584f, scale), figma(1584f, scale))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF4B4B4B), Color(0xFF2A2A2A), Color(0xFF151515))
                )
            )
    )

    if (darkOverlay > 0f) {
        Box(
            modifier = Modifier
                .absoluteOffset(x = figma(0f, scale), y = figma(0f, scale))
                .requiredSize(figma(1584f, scale), figma(1584f, scale))
                .background(Color.Black.copy(alpha = darkOverlay))
        )
    }
}

@Composable
private fun FigmaImageBlock(
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    scale: Float,
    colors: List<Color>
) {
    Box(
        modifier = Modifier
            .absoluteOffset(x = figma(x, scale), y = figma(y, scale))
            .requiredSize(figma(w, scale), figma(h, scale))
            .clip(RoundedCornerShape(figma(24f, scale)))
            .background(Brush.linearGradient(colors = colors))
    )
}

@Composable
private fun FigmaStage(background: Color, content: @Composable BoxWithConstraintsScope.(Float) -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val outerMaxWidth = this.maxWidth
        val outerMaxHeight = this.maxHeight
        val scale = min(outerMaxWidth.value / FIGMA_FRAME_WIDTH, outerMaxHeight.value / FIGMA_FRAME_HEIGHT)
        val stageWidth = figma(FIGMA_FRAME_WIDTH, scale)
        val stageHeight = figma(FIGMA_FRAME_HEIGHT, scale)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .align(Alignment.Center)
                    .requiredSize(stageWidth, stageHeight)
            ) {
                content(scale)
            }
        }
    }
}

private fun figma(px: Float, scale: Float) = (px * scale).dp
