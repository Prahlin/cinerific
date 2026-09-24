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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.annotation.StringRes
import com.prahlin.cinerific.R
import androidx.compose.ui.res.stringResource
import kotlin.math.abs
import kotlin.math.max

private const val HOME_FRAME_WIDTH = 1194f
private const val HERO_REEL_VIEWPORT_ASPECT = 1194f / 834f
private const val CARD_ASPECT = 350f / 263f
private const val CARD_SCALE = 0.8f
private const val CARD_CORNER_RADIUS_DP = 22f
private const val SELECTED_CARD_CLEAR_STROKE_PX = 10f
private const val SELECTED_CARD_OUTER_STROKE_PX = 3f
private const val HOME_ROW_HEADER_HEIGHT_DP = 48f
private const val HOME_ROW_CARDS_TOP_PADDING_DP = 20f
private const val PORTRAIT_HERO_HEIGHT_FRACTION = 0.48f
private const val PORTRAIT_CARD_VISIBLE_COUNT = 2.75f
private const val PORTRAIT_BOTTOM_NAV_CLEARANCE = 118f
private const val HOME_HERO_SWIPE_THRESHOLD_DP = 48f
private const val PHONE_PORTRAIT_HERO_ART_SCALE = 1.44f

private val HomeBackgroundTop = Color(0xFF080007)
private val HomeBackgroundMid = Color(0xFF23001F)
private val HomeBackgroundBottom = Color(0xFF060004)
private val HomeText = Color(0xFFE7E7E7)
private val HomeSelectedCardStroke = Color(0xFFE7E7E7)

@Composable
internal fun CinerificHomeScreen(
    onProgramSelected: (String) -> Unit = {},
    onCatalogSelected: (CinerificCatalogRoute) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(HomeBackgroundBottom)
    ) {
        val scale = maxWidth.value / HOME_FRAME_WIDTH
        val density = LocalDensity.current
        val isPortrait = maxHeight > maxWidth
        val horizontalPadding = figmaDp(50f, scale)
        val endPadding = horizontalPadding
        val cardGap = figmaDp(if (isPortrait) 42f else 50f, scale)
        val selectedCardClearStroke = figmaDp(SELECTED_CARD_CLEAR_STROKE_PX, scale)
        val selectedCardOuterStroke = figmaDp(SELECTED_CARD_OUTER_STROKE_PX, scale)
        val selectionStrokeAlpha = rememberCinerificSelectionStrokeAlpha()
        val cardWidth = if (isPortrait) {
            val availableWidth = (
                maxWidth.value -
                    horizontalPadding.value -
                    endPadding.value -
                    cardGap.value * (PORTRAIT_CARD_VISIBLE_COUNT - 1f)
                ).coerceAtLeast(0f)
            (availableWidth / PORTRAIT_CARD_VISIBLE_COUNT).dp
        } else {
            figmaDp(350f, scale) * CARD_SCALE
        }
        val cardHeight = cardWidth / CARD_ASPECT
        val interStackGap = if (isPortrait) 58.dp else 80.dp
        val bottomSystemPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        val isPhonePortrait = isPortrait && maxWidth < 600.dp
        val phonePortraitLogoTopPadding = if (isPhonePortrait) {
            with(density) { WindowInsets.statusBars.getTop(this).toDp() } - 2.dp
        } else {
            0.dp
        }
        val naturalHeroHeight = (maxWidth.value / HERO_REEL_VIEWPORT_ASPECT).dp
        val visibleHeroHeight = (maxHeight - bottomSystemPadding).coerceAtLeast(0.dp)
        val portraitHeroHeight = visibleHeroHeight * PORTRAIT_HERO_HEIGHT_FRACTION
        val heroHeight = if (isPortrait) {
            minOf(maxOf(naturalHeroHeight, portraitHeroHeight), visibleHeroHeight)
        } else {
            minOf(naturalHeroHeight, visibleHeroHeight)
        }
        val scrollState = rememberScrollState()
        var requestedReelIndex by rememberSaveable { mutableStateOf(0) }
        var displayedReelIndex by rememberSaveable { mutableStateOf(0) }
        val selectedCarouselIndex = homeSelectedCarouselIndex(
            scrollOffsetPx = scrollState.value,
            viewportHeight = maxHeight,
            heroHeight = heroHeight,
            cardHeight = cardHeight,
            interStackGap = interStackGap,
            isPortrait = isPortrait,
            density = density
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
            HomeHeroHeader(
                scale = scale,
                height = heroHeight,
                isPortrait = isPortrait,
                isPhonePortrait = isPhonePortrait,
                logoTopPadding = phonePortraitLogoTopPadding,
                requestedReelIndex = requestedReelIndex,
                displayedReelIndex = displayedReelIndex,
                onReelRequested = { requestedReelIndex = it },
                onReelSettled = { settledIndex ->
                    val wasFollowingPlayback = requestedReelIndex == displayedReelIndex
                    displayedReelIndex = settledIndex
                    if (wasFollowingPlayback) requestedReelIndex = settledIndex
                },
                onProgramSelected = onProgramSelected
            )

            HomeProgramRows.forEachIndexed { index, row ->
                val catalogRoute = cinerificHomeCatalogRoute(
                    genre = row.genre,
                    titles = row.cardIds.mapNotNull(::homeProgramTitleForCard)
                )
                HomeProgramRow(
                    title = stringResource(row.titleResId),
                    cardIds = row.cardIds,
                    onProgramSelected = onProgramSelected,
                    onCatalogSelected = { onCatalogSelected(catalogRoute) },
                    horizontalPadding = horizontalPadding,
                    endPadding = endPadding,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    cardGap = cardGap,
                    selectedCardClearStroke = selectedCardClearStroke,
                    selectedCardOuterStroke = selectedCardOuterStroke,
                    selectionStrokeAlpha = selectionStrokeAlpha,
                    selected = selectedCarouselIndex == index,
                    topPadding = homeRowTopPadding(index, isPortrait, interStackGap)
                )
            }

            Spacer(
                modifier = Modifier.height(
                    interStackGap +
                        bottomSystemPadding +
                        if (isPortrait) figmaDp(PORTRAIT_BOTTOM_NAV_CLEARANCE, scale) else 0.dp
                )
            )
        }
    }
}

@Composable
private fun HomeHeroHeader(
    scale: Float,
    height: Dp,
    isPortrait: Boolean,
    isPhonePortrait: Boolean,
    logoTopPadding: Dp,
    requestedReelIndex: Int,
    displayedReelIndex: Int,
    onReelRequested: (Int) -> Unit,
    onReelSettled: (Int) -> Unit,
    onProgramSelected: (String) -> Unit
) {
    val displayedPresentation = HeroPresentation.forReelIndex(displayedReelIndex)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clipToBounds()
    ) {
        HeroReelVideo(
            targetReelIndex = requestedReelIndex,
            onReelChanged = onReelSettled,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(requestedReelIndex, displayedPresentation) {
                    val swipeThresholdPx = HOME_HERO_SWIPE_THRESHOLD_DP.dp.toPx()
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var totalX = 0f
                        var totalY = 0f
                        var horizontalGesture = false
                        var released = false

                        while (!released) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            val delta = change.positionChange()
                            totalX += delta.x
                            totalY += delta.y
                            if (
                                !horizontalGesture &&
                                abs(totalX) > viewConfiguration.touchSlop &&
                                abs(totalX) > abs(totalY)
                            ) {
                                horizontalGesture = true
                            }
                            if (horizontalGesture) change.consume()
                            released = !change.pressed
                        }

                        when {
                            horizontalGesture && totalX <= -swipeThresholdPx -> {
                                onReelRequested(
                                    (requestedReelIndex + 1) % HeroPresentation.values().size
                                )
                            }
                            horizontalGesture && totalX >= swipeThresholdPx -> {
                                onReelRequested(
                                    (requestedReelIndex - 1 + HeroPresentation.values().size) %
                                        HeroPresentation.values().size
                                )
                            }
                            !horizontalGesture &&
                                abs(totalX) <= viewConfiguration.touchSlop &&
                                abs(totalY) <= viewConfiguration.touchSlop -> {
                                onProgramSelected(displayedPresentation.programTitle)
                            }
                        }
                    }
                }
        )

        HeroPresentationTextAnimation(
            presentation = displayedPresentation,
            playKey = displayedReelIndex,
            isPortrait = isPortrait,
            titleVisualScale = if (isPhonePortrait) PHONE_PORTRAIT_HERO_ART_SCALE else 1f,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(R.drawable.logo_simple_large),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = logoTopPadding)
                .width(
                    figmaDp(300f, scale) *
                        if (isPhonePortrait) PHONE_PORTRAIT_HERO_ART_SCALE else 1f
                )
                .height(
                    figmaDp(214f, scale) *
                        if (isPhonePortrait) PHONE_PORTRAIT_HERO_ART_SCALE else 1f
                ),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
private fun HeroReelVideo(
    targetReelIndex: Int,
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
            view.showReel(targetReelIndex)
        }
    )
}

@Composable
private fun HomeProgramRow(
    title: String,
    @DrawableRes cardIds: List<Int>,
    onProgramSelected: (String) -> Unit,
    onCatalogSelected: () -> Unit,
    horizontalPadding: Dp,
    endPadding: Dp,
    cardWidth: Dp,
    cardHeight: Dp,
    cardGap: Dp,
    selectedCardClearStroke: Dp,
    selectedCardOuterStroke: Dp,
    selectionStrokeAlpha: Float,
    selected: Boolean,
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
                .padding(start = horizontalPadding, end = endPadding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onCatalogSelected()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = HomeText,
                fontFamily = CinerificAppTextFontFamily,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "$title row",
                tint = HomeText,
                modifier = Modifier.size(48.dp)
            )
        }

        CinerificCircularCardRow(
            itemCount = cardIds.size,
            selected = selected,
            itemSpacing = cardGap,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            contentPadding = PaddingValues(start = horizontalPadding, end = endPadding)
        ) { index, cardSelected ->
            val cardId = cardIds[index]
            val programTitle = homeProgramTitleForCard(cardId)
            ProgramCard(
                drawableId = cardId,
                width = cardWidth,
                height = cardHeight,
                selected = cardSelected,
                selectedCardClearStroke = selectedCardClearStroke,
                selectedCardOuterStroke = selectedCardOuterStroke,
                selectionStrokeAlpha = selectionStrokeAlpha,
                onClick = programTitle?.let { title ->
                    { onProgramSelected(title) }
                }
            )
        }
    }
}

@Composable
private fun ProgramCard(
    @DrawableRes drawableId: Int,
    width: Dp,
    height: Dp,
    selected: Boolean,
    selectedCardClearStroke: Dp,
    selectedCardOuterStroke: Dp,
    selectionStrokeAlpha: Float,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(CARD_CORNER_RADIUS_DP.dp)
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(
                elevation = 12.dp,
                shape = shape,
                clip = false
            )
            .selectedHomeCardFrame(
                selected = selected,
                clearStrokeWidth = selectedCardClearStroke,
                outerStrokeWidth = selectedCardOuterStroke,
                strokeAlpha = selectionStrokeAlpha,
                cornerRadius = CARD_CORNER_RADIUS_DP.dp
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

private fun homeSelectedCarouselIndex(
    scrollOffsetPx: Int,
    viewportHeight: Dp,
    heroHeight: Dp,
    cardHeight: Dp,
    interStackGap: Dp,
    isPortrait: Boolean,
    density: Density
): Int {
    val viewportCenterY = scrollOffsetPx + with(density) { viewportHeight.toPx() / 2f }
    val headerHeightPx = with(density) { HOME_ROW_HEADER_HEIGHT_DP.dp.toPx() }
    val cardTopPaddingPx = with(density) { HOME_ROW_CARDS_TOP_PADDING_DP.dp.toPx() }
    val cardHeightPx = with(density) { cardHeight.toPx() }
    var rowTopY = with(density) { heroHeight.toPx() }
    var selectedIndex = 0
    var selectedDistance = Float.MAX_VALUE

    HomeProgramRows.indices.forEach { index ->
        val topPaddingPx = with(density) {
            homeRowTopPadding(index, isPortrait, interStackGap).toPx()
        }
        val carouselCenterY = rowTopY + topPaddingPx + headerHeightPx + cardTopPaddingPx + cardHeightPx / 2f
        val distance = abs(carouselCenterY - viewportCenterY)
        if (distance < selectedDistance) {
            selectedDistance = distance
            selectedIndex = index
        }
        rowTopY += topPaddingPx + headerHeightPx + cardTopPaddingPx + cardHeightPx
    }

    return selectedIndex
}

private fun homeRowTopPadding(
    index: Int,
    isPortrait: Boolean,
    interStackGap: Dp
): Dp {
    return if (index == 0) {
        if (isPortrait) 38.dp else 52.dp
    } else {
        interStackGap
    }
}

private fun Modifier.selectedHomeCardFrame(
    selected: Boolean,
    clearStrokeWidth: Dp,
    outerStrokeWidth: Dp,
    strokeAlpha: Float,
    cornerRadius: Dp
): Modifier {
    if (!selected) return this

    return this.drawWithContent {
        drawContent()

        val clearStrokePx = clearStrokeWidth.toPx()
        val outerStrokePx = outerStrokeWidth.toPx()
        val strokeCenterOffset = clearStrokePx + outerStrokePx / 2f
        drawRoundRect(
            color = HomeSelectedCardStroke.copy(alpha = strokeAlpha.coerceIn(0f, 1f)),
            topLeft = Offset(-strokeCenterOffset, -strokeCenterOffset),
            size = Size(
                width = size.width + strokeCenterOffset * 2f,
                height = size.height + strokeCenterOffset * 2f
            ),
            cornerRadius = CornerRadius(cornerRadius.toPx() + strokeCenterOffset),
            style = Stroke(width = outerStrokePx)
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
    private var queuedTransitionDirection = 1f
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
            incomingSlot.layer.translationX = width * queuedTransitionDirection
            queuedTransitionIndex?.let {
                requestTransitionToVideo(it, queuedTransitionDirection)
            }
        }
    }

    fun showReel(videoIndex: Int) {
        val targetIndex = normalizeVideoIndex(videoIndex)
        if (!initialPlaybackStarted) {
            currentVideoIndex = targetIndex
            return
        }
        if (targetIndex == currentVideoIndex && !transitionInProgress) {
            queuedTransitionIndex = null
            return
        }

        val direction = if (targetIndex == normalizeVideoIndex(currentVideoIndex - 1)) -1f else 1f
        requestTransitionToVideo(targetIndex, direction)
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
        preloadVideo(nextVideoIndex())
    }

    private fun preloadVideo(videoIndex: Int) {
        if (incomingSlot.surface == null || transitionInProgress) return
        if (incomingSlot.mediaPlayer != null && incomingSlot.videoIndex == videoIndex) {
            if (incomingSlot.isPrepared && queuedTransitionIndex == videoIndex) {
                queuedTransitionIndex = null
                animateIncomingVideo(videoIndex, queuedTransitionDirection)
            }
            return
        }

        incomingSlot.layer.leadingEdgeBlendWidthPx = 0f
        incomingSlot.layer.translationX = width.toFloat()
        prepareSlot(
            slot = incomingSlot,
            videoIndex = videoIndex,
            autoStart = false
        ) {
            val queuedIndex = queuedTransitionIndex
            if (queuedIndex == videoIndex) {
                queuedTransitionIndex = null
                animateIncomingVideo(videoIndex, queuedTransitionDirection)
            }
        }
    }

    private fun requestTransitionToVideo(videoIndex: Int, direction: Float = 1f) {
        if (width == 0) {
            queuedTransitionIndex = videoIndex
            queuedTransitionDirection = direction
            return
        }
        if (transitionInProgress) {
            queuedTransitionIndex = videoIndex
            queuedTransitionDirection = direction
            return
        }
        if (incomingSlot.mediaPlayer != null && incomingSlot.videoIndex == videoIndex && incomingSlot.isPrepared) {
            queuedTransitionIndex = null
            animateIncomingVideo(videoIndex, direction)
            return
        }
        if (queuedTransitionIndex == videoIndex && incomingSlot.mediaPlayer != null && incomingSlot.videoIndex == videoIndex) {
            return
        }

        handler.removeCallbacksAndMessages(null)
        queuedTransitionIndex = videoIndex
        queuedTransitionDirection = direction
        preloadVideo(videoIndex)
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
        val nextIndex = queuedTransitionIndex ?: nextVideoIndex()
        val direction = if (queuedTransitionIndex == null) 1f else queuedTransitionDirection
        if (incomingSlot.mediaPlayer == null || incomingSlot.videoIndex != nextIndex || !incomingSlot.isPrepared) {
            queuedTransitionIndex = nextIndex
            queuedTransitionDirection = direction
            preloadVideo(nextIndex)
            return
        }

        queuedTransitionIndex = null
        animateIncomingVideo(nextIndex, direction)
    }

    private fun animateIncomingVideo(nextIndex: Int, direction: Float) {
        if (transitionInProgress || width == 0) return
        transitionInProgress = true
        handler.removeCallbacksAndMessages(null)
        val maximumBlendWidth = width * HERO_REEL_EDGE_BLEND_WIDTH_FRACTION

        incomingSlot.layer.apply {
            leadingEdgeBlendWidthPx = maximumBlendWidth
            blendOnRightEdge = direction < 0f
            translationX = width * direction
            bringToFront()
        }
        incomingSlot.mediaPlayer?.apply {
            seekTo(0)
            start()
        }

        transitionAnimator?.cancel()
        transitionAnimator = ValueAnimator.ofFloat(width * direction, 0f).apply {
            var canceled = false
            duration = HERO_REEL_SLIDE_DURATION_MS.toLong()
            interpolator = DecelerateInterpolator(1.35f)
            addUpdateListener { animator ->
                val translation = animator.animatedValue as Float
                val remainingTravel = (abs(translation) / width.toFloat()).coerceIn(0f, 1f)
                incomingSlot.layer.translationX = translation
                incomingSlot.layer.leadingEdgeBlendWidthPx = maximumBlendWidth * remainingTravel
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
        incomingSlot.layer.blendOnRightEdge = false

        val oldActiveSlot = activeSlot
        activeSlot = incomingSlot
        incomingSlot = oldActiveSlot
        releaseSlot(incomingSlot, releaseSurface = false)
        incomingSlot.layer.leadingEdgeBlendWidthPx = 0f
        incomingSlot.layer.blendOnRightEdge = false
        incomingSlot.layer.translationX = width.toFloat()

        currentVideoIndex = nextIndex
        transitionInProgress = false
        reelChangedListener?.invoke(currentVideoIndex)

        val queuedIndex = queuedTransitionIndex
        if (queuedIndex != null && queuedIndex != currentVideoIndex) {
            requestTransitionToVideo(queuedIndex, queuedTransitionDirection)
        } else {
            queuedTransitionIndex = null
            preloadNextVideo()
            activeSlot.mediaPlayer?.let { scheduleSlideTransition(it) }
        }
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

    private fun normalizeVideoIndex(videoIndex: Int): Int {
        return ((videoIndex % HeroReelVideos.size) + HeroReelVideos.size) % HeroReelVideos.size
    }

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
        private var edgeMaskShaderOnRight = false

        var leadingEdgeBlendWidthPx = 0f
            set(value) {
                field = value.coerceAtLeast(0f)
                invalidate()
            }

        var blendOnRightEdge = false
            set(value) {
                field = value
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
            if (
                existingShader != null &&
                edgeMaskShaderWidth == edgeWidth &&
                edgeMaskShaderOnRight == blendOnRightEdge
            ) {
                return existingShader
            }

            val startX = if (blendOnRightEdge) width - edgeWidth else 0f
            val endX = if (blendOnRightEdge) width.toFloat() else edgeWidth
            val colors = if (blendOnRightEdge) {
                intArrayOf(
                    android.graphics.Color.BLACK,
                    android.graphics.Color.argb(110, 0, 0, 0),
                    android.graphics.Color.TRANSPARENT
                )
            } else {
                intArrayOf(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.argb(110, 0, 0, 0),
                    android.graphics.Color.BLACK
                )
            }
            return LinearGradient(
                startX,
                0f,
                endX,
                0f,
                colors,
                floatArrayOf(0f, 0.42f, 1f),
                Shader.TileMode.CLAMP
            ).also {
                edgeMaskShader = it
                edgeMaskShaderWidth = edgeWidth
                edgeMaskShaderOnRight = blendOnRightEdge
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
    val genre: ViewportGenre,
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
    R.drawable.morbid_temptations_title_card -> "Morbid Temptations"
    R.drawable.citric_card -> "Citric"
    R.drawable.laughing_matters_card -> "Laughing Matters"
    R.drawable.lost_in_time_card -> "Lost in Time"
    R.drawable.operation_firefly_card -> "Operation Firefly"
    R.drawable.smoke_card -> "Smoke"
    R.drawable.joyriders_card -> "Joyriders"
    R.drawable.breathing_card -> "Breathing"
    R.drawable.infatuation_title_card -> "Infatuation"
    R.drawable.falling_behind_card -> "Falling Behind"
    R.drawable.still_there_card -> "Still There"
    R.drawable.moments_card -> "Moments"
    R.drawable.chasing_light_card -> "Chasing Light"
    R.drawable.light_as_air_title_card -> "Light As Air"
    R.drawable.into_the_wild_title_card -> "Into The Wild"
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
        genre = ViewportGenre.Crime,
        cardIds = listOf(
            R.drawable.no_trespassing_card,
            R.drawable.one_last_breath_card,
            R.drawable.sink_or_swim_card,
            R.drawable.hungry_heart_card
        )
    ),
    HomeProgramRowSpec(
        titleResId = R.string.home_row_thriller,
        genre = ViewportGenre.Thriller,
        cardIds = listOf(
            R.drawable.morbid_temptations_title_card,
            R.drawable.enlightenment_card,
            R.drawable.ignition_card,
            R.drawable.deadbeat_card,
            R.drawable.playing_with_fire_card
        )
    ),
    HomeProgramRowSpec(
        titleResId = R.string.home_row_comedy,
        genre = ViewportGenre.Comedy,
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
        genre = ViewportGenre.Action,
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
        genre = ViewportGenre.Drama,
        cardIds = listOf(
            R.drawable.infatuation_title_card,
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
        genre = ViewportGenre.Documentary,
        cardIds = listOf(
            R.drawable.light_as_air_title_card,
            R.drawable.into_the_wild_title_card,
            R.drawable.incan_descent_card,
            R.drawable.or_not_to_be_card,
            R.drawable.surfside_card,
            R.drawable.wheels_card,
            R.drawable.light_as_a_feather_card
        )
    ),
    HomeProgramRowSpec(
        titleResId = R.string.home_row_horror,
        genre = ViewportGenre.Horror,
        cardIds = listOf(
            R.drawable.the_playmate_card,
            R.drawable.help_card,
            R.drawable.skin_and_bones_card,
            R.drawable.the_appetizer_card
        )
    )
)
