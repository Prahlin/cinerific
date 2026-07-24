package com.prahlin.cinerific.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import androidx.annotation.StringRes
import com.prahlin.cinerific.R
import androidx.compose.ui.res.stringResource
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
internal fun CinerificHomeScreen(
    onProgramSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                    title = stringResource(row.titleResId),
                    cardIds = row.cardIds,
                    onProgramSelected = onProgramSelected,
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
    var activeReelIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(HERO_REEL_VIEWPORT_ASPECT)
    ) {
        HeroReelVideo(
            onReelChanged = { activeReelIndex = it },
            modifier = Modifier.fillMaxSize()
        )

        HeroPresentationTextAnimation(
            presentation = HeroPresentation.forReelIndex(activeReelIndex),
            playKey = activeReelIndex,
            modifier = Modifier.fillMaxSize()
        )

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
private fun HeroReelVideo(
    onReelChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentOnReelChanged by rememberUpdatedState(onReelChanged)
    AndroidView(
        modifier = modifier,
        factory = {
            LoopingHeroVideoView(context).apply {
                reelChangedListener = { currentOnReelChanged(it) }
            }
        },
        update = { view ->
            view.reelChangedListener = { currentOnReelChanged(it) }
        }
    )
}

@Composable
private fun HomeProgramRow(
    title: String,
    @DrawableRes cardIds: List<Int>,
    onProgramSelected: (String) -> Unit,
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
                val programTitle = homeProgramTitleForCard(cardId)
                ProgramCard(
                    drawableId = cardId,
                    width = cardWidth,
                    height = cardHeight,
                    onClick = programTitle?.let { title ->
                        { onProgramSelected(title) }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProgramCard(
    @DrawableRes drawableId: Int,
    width: Dp,
    height: Dp,
    onClick: (() -> Unit)? = null
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
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
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

private class LoopingHeroVideoView(context: Context) : FrameLayout(context) {
    private var activeSlot = VideoSlot(context)
    private var incomingSlot = VideoSlot(context)
    private val handler = Handler(Looper.getMainLooper())
    private var transitionAnimator: ValueAnimator? = null
    private var currentVideoIndex = 0
    private var initialPlaybackStarted = false
    private var transitionInProgress = false
    private var queuedTransitionIndex: Int? = null
    var reelChangedListener: ((Int) -> Unit)? = null

    init {
        setBackgroundColor(android.graphics.Color.BLACK)
        clipChildren = true
        addVideoSlot(activeSlot, initiallyOffscreen = false)
        addVideoSlot(incomingSlot, initiallyOffscreen = true)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        activeSlot.applyCenterCropTransform(width, height)
        incomingSlot.applyCenterCropTransform(width, height)
        if (!transitionInProgress) {
            incomingSlot.layer.translationX = width.toFloat()
        }
    }

    private fun addVideoSlot(slot: VideoSlot, initiallyOffscreen: Boolean) {
        slot.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                slot.surface = Surface(surfaceTexture)
                if (slot === activeSlot && !initialPlaybackStarted) {
                    startInitialVideo()
                } else if (slot === incomingSlot) {
                    preloadNextVideo()
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                slot.applyCenterCropTransform(this@LoopingHeroVideoView.width, this@LoopingHeroVideoView.height)
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                releaseSlot(slot, releaseSurface = true)
                return true
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) = Unit
        }
        if (initiallyOffscreen) {
            slot.layer.translationX = width.toFloat()
        }
        addView(
            slot.layer,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
        )
    }

    private fun startInitialVideo() {
        initialPlaybackStarted = true
        prepareSlot(
            slot = activeSlot,
            videoIndex = currentVideoIndex,
            autoStart = true
        ) { player ->
            reelChangedListener?.invoke(currentVideoIndex)
            preloadNextVideo()
            scheduleSlideTransition(player)
        }
    }

    private fun prepareSlot(
        slot: VideoSlot,
        videoIndex: Int,
        autoStart: Boolean,
        onPrepared: (MediaPlayer) -> Unit = {}
    ) {
        val surface = slot.surface ?: return
        releaseSlot(slot, releaseSurface = false)
        slot.videoIndex = videoIndex

        val player = MediaPlayer()
        slot.mediaPlayer = player

        player.apply {
            resources.openRawResourceFd(HeroReelVideos[videoIndex]).use { descriptor ->
                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            }
            setSurface(surface)
            isLooping = false
            setVolume(0f, 0f)
            setOnVideoSizeChangedListener { _, videoWidth, videoHeight ->
                slot.videoWidth = videoWidth
                slot.videoHeight = videoHeight
                slot.applyCenterCropTransform(this@LoopingHeroVideoView.width, this@LoopingHeroVideoView.height)
            }
            setOnPreparedListener { player ->
                if (slot.mediaPlayer !== player) return@setOnPreparedListener
                slot.isPrepared = true
                slot.applyCenterCropTransform(this@LoopingHeroVideoView.width, this@LoopingHeroVideoView.height)
                if (autoStart) {
                    player.start()
                } else {
                    player.seekTo(0)
                }
                onPrepared(player)
            }
            setOnCompletionListener { completedPlayer ->
                if (slot.mediaPlayer === completedPlayer && slot === activeSlot && !transitionInProgress) {
                    beginSlideTransition()
                }
            }
            setOnErrorListener { player, _, _ ->
                if (slot.mediaPlayer === player && slot === activeSlot) {
                    beginSlideTransition()
                }
                true
            }
            prepareAsync()
        }
    }

    private fun preloadNextVideo() {
        if (incomingSlot.surface == null || transitionInProgress) return
        val nextIndex = nextVideoIndex()
        if (incomingSlot.mediaPlayer != null && incomingSlot.videoIndex == nextIndex) return

        incomingSlot.layer.leadingEdgeBlendWidthPx = 0f
        incomingSlot.layer.translationX = width.toFloat()
        prepareSlot(
            slot = incomingSlot,
            videoIndex = nextIndex,
            autoStart = false
        ) {
            val queuedIndex = queuedTransitionIndex
            if (queuedIndex == nextIndex) {
                queuedTransitionIndex = null
                animateIncomingVideo(nextIndex)
            }
        }
    }

    private fun scheduleSlideTransition(player: MediaPlayer) {
        handler.removeCallbacksAndMessages(null)
        val remainingMs = player.duration - player.currentPosition - HERO_REEL_SLIDE_DURATION_MS
        handler.postDelayed(
            { beginSlideTransition() },
            remainingMs.coerceAtLeast(0).toLong()
        )
    }

    private fun beginSlideTransition() {
        if (transitionInProgress || width == 0) return
        val nextIndex = nextVideoIndex()
        if (incomingSlot.mediaPlayer == null || incomingSlot.videoIndex != nextIndex || !incomingSlot.isPrepared) {
            queuedTransitionIndex = nextIndex
            preloadNextVideo()
            return
        }

        animateIncomingVideo(nextIndex)
    }

    private fun animateIncomingVideo(nextIndex: Int) {
        if (transitionInProgress || width == 0) return
        transitionInProgress = true
        handler.removeCallbacksAndMessages(null)

        incomingSlot.layer.apply {
            leadingEdgeBlendWidthPx = width * HERO_REEL_EDGE_BLEND_WIDTH_FRACTION
            translationX = width.toFloat()
            bringToFront()
        }
        incomingSlot.mediaPlayer?.apply {
            seekTo(0)
            start()
        }

        transitionAnimator?.cancel()
        transitionAnimator = ValueAnimator.ofFloat(width.toFloat(), 0f).apply {
            var canceled = false
            duration = HERO_REEL_SLIDE_DURATION_MS.toLong()
            interpolator = DecelerateInterpolator(1.35f)
            addUpdateListener { animator ->
                incomingSlot.layer.translationX = animator.animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    canceled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!canceled) {
                        finishSlideTransition(nextIndex)
                    }
                }
            })
            start()
        }
    }

    private fun finishSlideTransition(nextIndex: Int) {
        if (!transitionInProgress) return

        transitionAnimator = null
        incomingSlot.layer.translationX = 0f
        incomingSlot.layer.leadingEdgeBlendWidthPx = 0f

        val oldActiveSlot = activeSlot
        activeSlot = incomingSlot
        incomingSlot = oldActiveSlot
        releaseSlot(incomingSlot, releaseSurface = false)
        incomingSlot.layer.leadingEdgeBlendWidthPx = 0f
        incomingSlot.layer.translationX = width.toFloat()

        currentVideoIndex = nextIndex
        transitionInProgress = false
        reelChangedListener?.invoke(currentVideoIndex)
        preloadNextVideo()
        activeSlot.mediaPlayer?.let { scheduleSlideTransition(it) }
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null)
        transitionAnimator?.cancel()
        releaseSlot(activeSlot, releaseSurface = true)
        releaseSlot(incomingSlot, releaseSurface = true)
        super.onDetachedFromWindow()
    }

    private fun releaseSlot(slot: VideoSlot, releaseSurface: Boolean) {
        slot.mediaPlayer?.apply {
            setOnPreparedListener(null)
            setOnVideoSizeChangedListener(null)
            setOnCompletionListener(null)
            setOnErrorListener(null)
            release()
        }
        slot.mediaPlayer = null
        slot.isPrepared = false
        slot.videoIndex = -1
        slot.videoWidth = 0
        slot.videoHeight = 0
        if (releaseSurface) {
            slot.surface?.release()
            slot.surface = null
        }
    }

    private fun nextVideoIndex(): Int = (currentVideoIndex + 1) % HeroReelVideos.size

    private class VideoSlot(context: Context) {
        val layer = BlendedVideoLayer(context)
        val textureView: TextureView = layer.textureView
        val centerCropMatrix = Matrix()
        var surface: Surface? = null
        var mediaPlayer: MediaPlayer? = null
        var videoIndex = -1
        var videoWidth = 0
        var videoHeight = 0
        var isPrepared = false

        fun applyCenterCropTransform(viewWidthPx: Int, viewHeightPx: Int) {
            if (viewWidthPx == 0 || viewHeightPx == 0 || videoWidth == 0 || videoHeight == 0) {
                return
            }

            val viewWidth = viewWidthPx.toFloat()
            val viewHeight = viewHeightPx.toFloat()
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
    }

    private class BlendedVideoLayer(context: Context) : FrameLayout(context) {
        val textureView = TextureView(context)
        private val edgeMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isDither = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }
        private var edgeMaskShader: LinearGradient? = null
        private var edgeMaskShaderWidth = -1f

        var leadingEdgeBlendWidthPx = 0f
            set(value) {
                field = value.coerceAtLeast(0f)
                invalidate()
            }

        init {
            setWillNotDraw(false)
            addView(
                textureView,
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER
                )
            )
        }

        override fun dispatchDraw(canvas: Canvas) {
            val edgeWidth = leadingEdgeBlendWidthPx.coerceIn(0f, width.toFloat())
            if (edgeWidth <= 0f || width == 0 || height == 0) {
                super.dispatchDraw(canvas)
                return
            }

            val layerBounds = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
            super.dispatchDraw(canvas)
            edgeMaskPaint.shader = edgeMaskShaderFor(edgeWidth)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), edgeMaskPaint)
            edgeMaskPaint.shader = null
            canvas.restoreToCount(layerBounds)
        }

        private fun edgeMaskShaderFor(edgeWidth: Float): LinearGradient {
            val existingShader = edgeMaskShader
            if (existingShader != null && edgeMaskShaderWidth == edgeWidth) {
                return existingShader
            }

            return LinearGradient(
                0f,
                0f,
                edgeWidth,
                0f,
                intArrayOf(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.argb(110, 0, 0, 0),
                    android.graphics.Color.BLACK
                ),
                floatArrayOf(0f, 0.42f, 1f),
                Shader.TileMode.CLAMP
            ).also {
                edgeMaskShader = it
                edgeMaskShaderWidth = edgeWidth
            }
        }
    }

    companion object {
        private const val HERO_REEL_SLIDE_DURATION_MS = 700
        private const val HERO_REEL_EDGE_BLEND_WIDTH_FRACTION = 0.16f

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
    @StringRes val titleResId: Int,
    @DrawableRes val cardIds: List<Int>
)

private fun homeProgramTitleForCard(@DrawableRes cardId: Int): String? = when (cardId) {
    R.drawable.one_last_breath_card -> "One Last Breath"
    R.drawable.sink_or_swim_card -> "Sink or Swim"
    R.drawable.troublemaker_card -> "Troublemaker"
    R.drawable.ignition_card -> "Ignition"
    R.drawable.the_baller_card -> "The Baller"
    R.drawable.eruption_card -> "Eruption"
    R.drawable.under_attack_card -> "Under Attack"
    R.drawable.if_i_may_card -> "If I May"
    R.drawable.the_playmate_card -> "The Playmate"
    R.drawable.help_card -> "Help"
    R.drawable.no_trespassing_card -> "No Trespassing"
    R.drawable.hungry_heart_card -> "Hungry Heart"
    R.drawable.enlightenment_card -> "Enlightenment"
    R.drawable.deadbeat_card -> "Deadbeat"
    R.drawable.playing_with_fire_card -> "Playing with Fire"
    R.drawable.morbid_temptations_card -> "Morbid Temptations"
    R.drawable.citric_card -> "Citric"
    R.drawable.laughing_matters_card -> "Laughing Matters"
    R.drawable.lost_in_time_card -> "Lost in Time"
    R.drawable.operation_firefly_card -> "Operation Firefly"
    R.drawable.smoke_card -> "Smoke"
    R.drawable.joyriders_card -> "Joyriders"
    R.drawable.breathing_card -> "Breathing"
    R.drawable.infatuation_card -> "Infatuation"
    R.drawable.falling_behind_card -> "Falling Behind"
    R.drawable.still_there_card -> "Still There"
    R.drawable.moments_card -> "Moments"
    R.drawable.chasing_light_card -> "Chasing Light"
    R.drawable.light_as_air_card -> "Light As Air"
    R.drawable.into_the_wild_card -> "Into The Wild"
    R.drawable.incan_descent_card -> "Incan Descent"
    R.drawable.or_not_to_be_card -> "Or Not To Be"
    R.drawable.surfside_card -> "Surfside"
    R.drawable.wheels_card -> "Wheels"
    R.drawable.light_as_a_feather_card -> "Light as a Feather"
    R.drawable.skin_and_bones_card -> "Skin and Bones"
    R.drawable.the_appetizer_card -> "The Appetizer"
    else -> null
}

private val HomeProgramRows = listOf(
    HomeProgramRowSpec(
        titleResId = R.string.home_row_crime,
        cardIds = listOf(
            R.drawable.no_trespassing_card,
            R.drawable.one_last_breath_card,
            R.drawable.sink_or_swim_card,
            R.drawable.hungry_heart_card
        )
    ),
    HomeProgramRowSpec(
        titleResId = R.string.home_row_thriller,
        cardIds = listOf(
            R.drawable.morbid_temptations_card,
            R.drawable.enlightenment_card,
            R.drawable.ignition_card,
            R.drawable.deadbeat_card,
            R.drawable.playing_with_fire_card
        )
    ),
    HomeProgramRowSpec(
        titleResId = R.string.home_row_comedy,
        cardIds = listOf(
            R.drawable.citric_card,
            R.drawable.troublemaker_card,
            R.drawable.laughing_matters_card,
            R.drawable.lost_in_time_card,
            R.drawable.the_baller_card
        )
    ),
    HomeProgramRowSpec(
        titleResId = R.string.home_row_action,
        cardIds = listOf(
            R.drawable.eruption_card,
            R.drawable.under_attack_card,
            R.drawable.operation_firefly_card,
            R.drawable.smoke_card,
            R.drawable.joyriders_card
        )
    ),
    HomeProgramRowSpec(
        titleResId = R.string.home_row_drama,
        cardIds = listOf(
            R.drawable.infatuation_card,
            R.drawable.breathing_card,
            R.drawable.falling_behind_card,
            R.drawable.still_there_card,
            R.drawable.if_i_may_card,
            R.drawable.moments_card,
            R.drawable.chasing_light_card
        )
    ),
    HomeProgramRowSpec(
        titleResId = R.string.home_row_documentary,
        cardIds = listOf(
            R.drawable.light_as_air_card,
            R.drawable.into_the_wild_card,
            R.drawable.incan_descent_card,
            R.drawable.or_not_to_be_card,
            R.drawable.surfside_card,
            R.drawable.wheels_card,
            R.drawable.light_as_a_feather_card
        )
    ),
    HomeProgramRowSpec(
        titleResId = R.string.home_row_horror,
        cardIds = listOf(
            R.drawable.the_playmate_card,
            R.drawable.help_card,
            R.drawable.skin_and_bones_card,
            R.drawable.the_appetizer_card
        )
    )
)
