package com.prahlin.cinerific.ui

import android.content.Context
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.prahlin.cinerific.R
import kotlin.math.max

private const val HOME_FRAME_WIDTH = 1194f
private const val HERO_REEL_VIEWPORT_ASPECT = 1194f / 834f
private const val CARD_ASPECT = 350f / 263f
private const val CARD_SCALE = 0.8f

private val HomeBackgroundTop = Color(0xFF080007)
private val HomeBackgroundMid = Color(0xFF23001F)
private val HomeBackgroundBottom = Color(0xFF060004)
private val HomeText = Color(0xFFE7E7E7)

@Composable
internal fun CinerificHomeScreen(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(HomeBackgroundBottom)
    ) {
        val scale = maxWidth.value / HOME_FRAME_WIDTH
        val density = LocalDensity.current
        val horizontalPadding = figmaDp(50f, scale)
        val cardWidth = figmaDp(350f, scale) * CARD_SCALE
        val cardHeight = cardWidth / CARD_ASPECT
        val cardGap = figmaDp(50f, scale)
        val interStackGap = 80.dp
        val bottomSystemPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            HomeBackgroundTop,
                            HomeBackgroundMid,
                            HomeBackgroundBottom
                        )
                    )
                )
        ) {
            HomeHeroHeader(scale = scale)

            HomeProgramRows.forEachIndexed { index, row ->
                HomeProgramRow(
                    title = row.title,
                    cardIds = row.cardIds,
                    horizontalPadding = horizontalPadding,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    cardGap = cardGap,
                    topPadding = if (index == 0) 52.dp else interStackGap
                )
            }

            Spacer(modifier = Modifier.height(interStackGap + bottomSystemPadding))
        }
    }
}

@Composable
private fun HomeHeroHeader(scale: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(HERO_REEL_VIEWPORT_ASPECT)
    ) {
        HeroReelVideo(modifier = Modifier.fillMaxSize())

        Image(
            painter = painterResource(R.drawable.logo_simple_large),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(figmaDp(300f, scale))
                .height(figmaDp(214f, scale)),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
private fun HeroReelVideo(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { LoopingHeroVideoView(context) }
    )
}

@Composable
private fun HomeProgramRow(
    title: String,
    @DrawableRes cardIds: List<Int>,
    horizontalPadding: Dp,
    cardWidth: Dp,
    cardHeight: Dp,
    cardGap: Dp,
    topPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = HomeText,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "$title row",
                tint = HomeText,
                modifier = Modifier.size(48.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = horizontalPadding, end = horizontalPadding)
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(cardGap)
        ) {
            cardIds.forEach { cardId ->
                ProgramCard(
                    drawableId = cardId,
                    width = cardWidth,
                    height = cardHeight
                )
            }
        }
    }
}

@Composable
private fun ProgramCard(
    @DrawableRes drawableId: Int,
    width: Dp,
    height: Dp
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(
                elevation = 12.dp,
                shape = shape,
                clip = false
            )
            .clip(shape)
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(drawableId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

private fun figmaDp(px: Float, scale: Float): Dp = (px * scale).dp

private class LoopingHeroVideoView(context: Context) : FrameLayout(context), TextureView.SurfaceTextureListener {
    private val textureView = TextureView(context)
    private val centerCropMatrix = Matrix()
    private var videoSurface: Surface? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentVideoIndex = 0
    private var videoWidth = 0
    private var videoHeight = 0

    init {
        setBackgroundColor(android.graphics.Color.BLACK)
        clipChildren = true
        textureView.surfaceTextureListener = this
        addView(
            textureView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
        )
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        videoSurface = Surface(surfaceTexture)
        startVideo()
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        applyCenterCropTransform()
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        releaseVideo()
        releaseSurface()
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) = Unit

    private fun startVideo() {
        val surface = videoSurface ?: return
        releaseVideo()

        val player = MediaPlayer()
        mediaPlayer = player

        player.apply {
            resources.openRawResourceFd(HeroReelVideos[currentVideoIndex]).use { descriptor ->
                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            }
            setSurface(surface)
            isLooping = false
            setVolume(0f, 0f)
            setOnVideoSizeChangedListener { _, videoWidth, videoHeight ->
                this@LoopingHeroVideoView.videoWidth = videoWidth
                this@LoopingHeroVideoView.videoHeight = videoHeight
                applyCenterCropTransform()
            }
            setOnPreparedListener { player ->
                if (mediaPlayer !== player) return@setOnPreparedListener
                applyCenterCropTransform()
                player.start()
            }
            setOnCompletionListener { completedPlayer ->
                if (mediaPlayer !== completedPlayer) return@setOnCompletionListener
                currentVideoIndex = (currentVideoIndex + 1) % HeroReelVideos.size
                startVideo()
            }
            setOnErrorListener { player, _, _ ->
                if (mediaPlayer === player) {
                    currentVideoIndex = (currentVideoIndex + 1) % HeroReelVideos.size
                    startVideo()
                }
                true
            }
            prepareAsync()
        }
    }

    override fun onDetachedFromWindow() {
        releaseVideo()
        releaseSurface()
        super.onDetachedFromWindow()
    }

    private fun applyCenterCropTransform() {
        if (width == 0 || height == 0 || videoWidth == 0 || videoHeight == 0) return

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val scaleX = viewWidth / videoWidth.toFloat()
        val scaleY = viewHeight / videoHeight.toFloat()
        val scale = max(scaleX, scaleY)
        val scaledWidth = videoWidth * scale
        val scaledHeight = videoHeight * scale

        centerCropMatrix.reset()
        centerCropMatrix.setScale(
            scaledWidth / viewWidth,
            scaledHeight / viewHeight,
            viewWidth / 2f,
            viewHeight / 2f
        )
        textureView.setTransform(centerCropMatrix)
    }

    private fun releaseVideo() {
        mediaPlayer?.apply {
            setOnPreparedListener(null)
            setOnVideoSizeChangedListener(null)
            setOnCompletionListener(null)
            setOnErrorListener(null)
            release()
        }
        mediaPlayer = null
        videoWidth = 0
        videoHeight = 0
    }

    private fun releaseSurface() {
        videoSurface?.release()
        videoSurface = null
    }

    companion object {
        @RawRes
        private val HeroReelVideos = intArrayOf(
            R.raw.home_hero_reel_01,
            R.raw.home_hero_reel_02,
            R.raw.home_hero_reel_03,
            R.raw.home_hero_reel_04
        )
    }
}

private data class HomeProgramRowSpec(
    val title: String,
    @DrawableRes val cardIds: List<Int>
)

private val HomeProgramRows = listOf(
    HomeProgramRowSpec(
        title = "Crime",
        cardIds = listOf(
            R.drawable.home_crime_01,
            R.drawable.home_crime_02,
            R.drawable.home_crime_03,
            R.drawable.home_crime_04
        )
    ),
    HomeProgramRowSpec(
        title = "Thriller",
        cardIds = listOf(
            R.drawable.home_thriller_01,
            R.drawable.home_thriller_02,
            R.drawable.home_thriller_03,
            R.drawable.home_thriller_04
        )
    ),
    HomeProgramRowSpec(
        title = "Comedy",
        cardIds = listOf(
            R.drawable.home_comedy_01,
            R.drawable.home_comedy_02,
            R.drawable.home_comedy_03,
            R.drawable.home_comedy_04,
            R.drawable.home_comedy_05
        )
    ),
    HomeProgramRowSpec(
        title = "Action",
        cardIds = listOf(
            R.drawable.home_action_01,
            R.drawable.home_action_02,
            R.drawable.home_action_03,
            R.drawable.home_action_04,
            R.drawable.home_action_05
        )
    ),
    HomeProgramRowSpec(
        title = "Drama",
        cardIds = listOf(
            R.drawable.home_drama_01,
            R.drawable.home_drama_02,
            R.drawable.home_drama_03,
            R.drawable.home_drama_04,
            R.drawable.home_drama_05,
            R.drawable.home_drama_06
        )
    ),
    HomeProgramRowSpec(
        title = "Documentary",
        cardIds = listOf(
            R.drawable.home_documentary_01,
            R.drawable.home_documentary_02,
            R.drawable.home_documentary_03,
            R.drawable.home_documentary_04,
            R.drawable.home_documentary_05
        )
    ),
    HomeProgramRowSpec(
        title = "Horror",
        cardIds = listOf(
            R.drawable.home_horror_01,
            R.drawable.home_horror_02,
            R.drawable.home_horror_03,
            R.drawable.home_horror_04
        )
    )
)
