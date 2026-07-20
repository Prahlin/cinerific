package com.prahlin.cinerific.ui

import android.graphics.BitmapFactory
import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import com.prahlin.cinerific.R
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private const val FIGMA_FRAME_WIDTH = 1194f
private const val FIGMA_FRAME_HEIGHT = 834f
private const val SCREEN_RED_MS = 1000
private const val LOGO_ENTRY_START_MS = 250
private const val LOGO_ENTRY_END_MS = 1687
private const val FINAL_SETTLE_START_MS = LOGO_ENTRY_END_MS
private const val FINAL_SETTLE_END_MS = 4812
private const val BOOT_ANIMATION_MS = FINAL_SETTLE_END_MS
private const val AUTO_LOGOUT_TIMEOUT_MS = 10_000L

private val ColorFrame1Background = Color(0xFF000000)
private val ColorFrame2Background = Color(0xFF62070D)
private val ColorFrame3Background = Color(0xFF1F1F1F)
private val ColorFrame4Background = Color(0xFF1F1F1F)
private val ColorIntroGradientTop = Color(0xFF050000)
private val ColorIntroGradientCenter = Color(0xFF62070D)
private val ColorIntroGradientBottom = Color(0xFF100102)

@Composable
fun CinerificApp(bootStartMillis: Long = SystemClock.uptimeMillis()) {
    var showHome by remember(bootStartMillis) { mutableStateOf(false) }
    var signedInProfile by remember(bootStartMillis) { mutableStateOf(CinerificProfile.Guest) }
    var selectedLanguage by rememberSaveable { mutableStateOf(CinerificLanguage.English) }

    if (showHome) {
        CinerificLocalizedResources(selectedLanguage) {
            CinerificMainExperience(
                signedInProfile = signedInProfile,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it },
                onSignOut = {
                    signedInProfile = CinerificProfile.Guest
                    showHome = false
                }
            )
        }
    } else {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                CinerificIntroView(context).apply {
                    this.bootStartMillis = bootStartMillis
                    onAvatarSelected = { profile ->
                        signedInProfile = profile
                        showHome = true
                    }
                }
            },
            update = { view ->
                view.bootStartMillis = bootStartMillis
                view.onAvatarSelected = { profile ->
                    signedInProfile = profile
                    showHome = true
                }
            }
        )
    }
}

internal enum class CinerificDestination {
    Home,
    Movies,
    Shows,
    Favorites,
    Settings
}

internal enum class CinerificProfile(
    @DrawableRes val avatarResId: Int,
    @DrawableRes val nameResId: Int
) {
    Steve(R.drawable.steve_avatar, R.drawable.steve_name),
    Martin(R.drawable.martin_avatar, R.drawable.martin_name),
    Janny(R.drawable.janny_avatar, R.drawable.janny_name),
    Guest(R.drawable.guest_avatar, R.drawable.guest_name)
}

@Composable
private fun CinerificMainExperience(
    signedInProfile: CinerificProfile,
    selectedLanguage: CinerificLanguage,
    onLanguageSelected: (CinerificLanguage) -> Unit,
    onSignOut: () -> Unit
) {
    var destination by remember { mutableStateOf(CinerificDestination.Home) }
    var autoLogoutEnabled by rememberSaveable { mutableStateOf(false) }
    var userInitiatedPlaybackActive by remember { mutableStateOf(false) }
    var lastInteractionMillis by remember { mutableStateOf(SystemClock.uptimeMillis()) }
    val playbackSessionController = remember(userInitiatedPlaybackActive) {
        CinerificPlaybackSessionController(
            isUserInitiatedPlaybackActive = userInitiatedPlaybackActive,
            onUserInitiatedPlaybackStarted = {
                userInitiatedPlaybackActive = true
            },
            finishUserInitiatedPlayback = {
                userInitiatedPlaybackActive = false
                lastInteractionMillis = SystemClock.uptimeMillis()
            }
        )
    }

    LaunchedEffect(autoLogoutEnabled, userInitiatedPlaybackActive, lastInteractionMillis) {
        if (!autoLogoutEnabled || userInitiatedPlaybackActive) return@LaunchedEffect
        delay(AUTO_LOGOUT_TIMEOUT_MS)
        val inactiveForMillis = SystemClock.uptimeMillis() - lastInteractionMillis
        if (
            autoLogoutEnabled &&
            !userInitiatedPlaybackActive &&
            inactiveForMillis >= AUTO_LOGOUT_TIMEOUT_MS
        ) {
            onSignOut()
        }
    }

    CompositionLocalProvider(LocalCinerificPlaybackSessionController provides playbackSessionController) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent(PointerEventPass.Initial)
                            lastInteractionMillis = SystemClock.uptimeMillis()
                        }
                    }
                }
        ) {
            when (destination) {
                CinerificDestination.Home -> CinerificHomeScreen(modifier = Modifier.fillMaxSize())
                CinerificDestination.Movies,
                CinerificDestination.Shows,
                CinerificDestination.Favorites,
                CinerificDestination.Settings -> CinerificDestinationScreen(
                    destination = destination,
                    signedInProfile = signedInProfile,
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = onLanguageSelected,
                    autoLogoutEnabled = autoLogoutEnabled,
                    onAutoLogoutEnabledChange = { enabled ->
                        autoLogoutEnabled = enabled
                        lastInteractionMillis = SystemClock.uptimeMillis()
                    },
                    onSignOut = onSignOut,
                    modifier = Modifier.fillMaxSize()
                )
            }

            CinerificRightSideNavBar(
                currentDestination = destination,
                onDestinationSelected = { destination = it },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun BootIntroFromFigma(progress: Float) {
    val p = progress.coerceIn(0f, 1f)
    val images = rememberIntroImages()
    val blackToBurgundy = linearSegmentMs(p, 0, SCREEN_RED_MS)
    val burgundyToSettle = easedSegmentMs(p, 2500, 3900)
    val background = introBackgroundBrush(
        solidProgress = blackToBurgundy,
        gradientProgress = burgundyToSettle
    )
    val logoAlpha = linearSegmentMs(p, 90, LOGO_ENTRY_START_MS)
    val logoEntryProgress = bouncySegmentMs(p, LOGO_ENTRY_START_MS, LOGO_ENTRY_END_MS)
    val logoFinalProgress = easedSegmentMs(p, FINAL_SETTLE_START_MS, FINAL_SETTLE_END_MS)
    val avatarSettleAlpha = easedSegmentMs(p, 2300, FINAL_SETTLE_END_MS)
    val avatarYOffset = lerpFloat(56f, 0f, avatarSettleAlpha)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val stageScale = min(size.width / FIGMA_FRAME_WIDTH, size.height / FIGMA_FRAME_HEIGHT)
        val stageLeft = (size.width - FIGMA_FRAME_WIDTH * stageScale) / 2f
        val stageTop = (size.height - FIGMA_FRAME_HEIGHT * stageScale) / 2f

        drawRect(brush = background, size = size)

        if (logoAlpha > 0.01f) {
            val baseBounds = FigmaBounds(x = 222f, y = 150f, w = 750f, h = 535f)
            val simpleEntry = lerpBounds(
                start = FigmaBounds(x = 447f, y = -417f, w = 300f, h = 214f),
                end = baseBounds,
                amount = logoEntryProgress
            )
            val eyesEntry = lerpBounds(
                start = FigmaBounds(x = 453f, y = 1037f, w = 300f, h = 214f),
                end = baseBounds,
                amount = logoEntryProgress
            )
            val finalBounds = FigmaBounds(x = 297f, y = 11f, w = 600f, h = 428f)

            drawFigmaImage(
                image = images.logoSimple,
                bounds = lerpBounds(simpleEntry, finalBounds, logoFinalProgress),
                stageLeft = stageLeft,
                stageTop = stageTop,
                stageScale = stageScale,
                alpha = logoAlpha
            )
            drawFigmaImage(
                image = images.logoEyes,
                bounds = lerpBounds(eyesEntry, finalBounds, logoFinalProgress),
                stageLeft = stageLeft,
                stageTop = stageTop,
                stageScale = stageScale,
                alpha = logoAlpha
            )
        }

        if (avatarSettleAlpha > 0.01f) {
            val y = avatarYOffset
            drawFigmaImage(images.steveAvatar, FigmaBounds(130f, 428f + y, 220f, 220f), stageLeft, stageTop, stageScale, avatarSettleAlpha, clipCircle = true)
            drawFigmaImage(images.martinAvatar, FigmaBounds(368f, 428f + y, 220f, 220f), stageLeft, stageTop, stageScale, avatarSettleAlpha, clipCircle = true)
            drawFigmaImage(images.jannyAvatar, FigmaBounds(606f, 432f + y, 220f, 220f), stageLeft, stageTop, stageScale, avatarSettleAlpha, clipCircle = true)
            drawFigmaImage(images.guestAvatar, FigmaBounds(844f, 428f + y, 220f, 220f), stageLeft, stageTop, stageScale, avatarSettleAlpha, clipCircle = true)

            drawFigmaImage(images.steveName, FigmaBounds(130f, 683f + y, 220f, 72f), stageLeft, stageTop, stageScale, avatarSettleAlpha)
            drawFigmaImage(images.martinName, FigmaBounds(368f, 683f + y, 220f, 72f), stageLeft, stageTop, stageScale, avatarSettleAlpha)
            drawFigmaImage(images.jannyName, FigmaBounds(606f, 683f + y, 220f, 72f), stageLeft, stageTop, stageScale, avatarSettleAlpha)
            drawFigmaImage(images.guestName, FigmaBounds(854f, 683f + y, 200f, 72f), stageLeft, stageTop, stageScale, avatarSettleAlpha)
        }
    }
}

@Composable
private fun IntroFrame1FromFigma() {
    // Node 474:7309 has only a pure black fill.
    FigmaStage(background = solidBrush(ColorFrame1Background)) { }
}

@Composable
private fun IntroFrame2FromFigma() {
    // Node 474:7311 with Logo Slide 1 instance 478:6917.
    val slideYOffset = remember { Animatable(-417f) }

    LaunchedEffect(Unit) {
        slideYOffset.snapTo(-417f)
        slideYOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
        )
    }

    FigmaStage(background = solidBrush(ColorFrame2Background)) { scale ->
        LogoSlide1Layer(scale = scale, yOffset = slideYOffset.value)
    }
}

@Composable
private fun IntroFrame3FromFigma() {
    // Node 476:6937 with PROMO background1 2 (793:7049) and Logo Slide 2 (478:6970).
    FigmaStage(background = solidBrush(ColorFrame3Background)) { scale ->
        PromoBackgroundLayer(
            scale = scale,
            darkOverlay = 0f,
            backgroundResId = R.drawable.promo_background
        )
        LogoSlide2Layer(
            scale = scale,
            logoResId = R.drawable.logo_simple_large,
            eyesResId = R.drawable.logo_eyes_large
        )
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

    FigmaStage(background = solidBrush(ColorFrame4Background)) { scale ->
        PromoBackgroundLayer(
            scale = scale,
            darkOverlay = 0.5f,
            backgroundResId = R.drawable.promo_background_signin
        )
        LogoSlide2Layer(
            scale = scale,
            logoResId = R.drawable.logo_simple_signin,
            eyesResId = R.drawable.logo_eyes_signin
        )

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
private fun LogoSlide1Layer(scale: Float, yOffset: Float, alpha: Float = 1f) {
    Box(
        modifier = Modifier
            .absoluteOffset(y = figma(yOffset, scale))
            .requiredSize(figma(1194f, scale), figma(1668f, scale))
            .graphicsLayer(alpha = alpha)
    ) {
        // Node I478:6917;478:6923 logo simple 1 at x:447 y:0 size:300x214.
        FigmaAssetImage(
            x = 447f,
            y = 0f,
            w = 300f,
            h = 214f,
            scale = scale,
            resId = R.drawable.logo_simple_intro2
        )

        // Node I478:6917;478:6925 logo eyes only 2 at x:453 y:1454 size:300x214.
        FigmaAssetImage(
            x = 453f,
            y = 1454f,
            w = 300f,
            h = 214f,
            scale = scale,
            resId = R.drawable.logo_eyes_intro2
        )
    }
}

@Composable
private fun LogoSlide2Layer(
    scale: Float,
    @DrawableRes logoResId: Int,
    @DrawableRes eyesResId: Int,
    alpha: Float = 1f
) {
    Box(
        modifier = Modifier
            .requiredSize(figma(1194f, scale), figma(834f, scale))
            .graphicsLayer(alpha = alpha)
    ) {
        LogoDot(x = 427f, y = 516f, scale = scale, active = false)
        LogoDot(x = 507f, y = 516f, scale = scale, active = false)
        LogoDot(x = 587f, y = 516f, scale = scale, active = false)
        LogoDot(x = 667f, y = 516f, scale = scale, active = true)

        // Visible logo bounds from the Figma instance in the 1194x834 frame.
        FigmaAssetImage(
            x = 222f,
            y = 150f,
            w = 750f,
            h = 535f,
            scale = scale,
            resId = logoResId
        )

        // Eye layer sits on the same logo bounds.
        FigmaAssetImage(
            x = 222f,
            y = 150f,
            w = 750f,
            h = 535f,
            scale = scale,
            resId = eyesResId
        )
    }
}

private data class FigmaBounds(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float
)

private data class IntroImages(
    val logoSimple: ImageBitmap,
    val logoEyes: ImageBitmap,
    val steveAvatar: ImageBitmap,
    val martinAvatar: ImageBitmap,
    val jannyAvatar: ImageBitmap,
    val guestAvatar: ImageBitmap,
    val steveName: ImageBitmap,
    val martinName: ImageBitmap,
    val jannyName: ImageBitmap,
    val guestName: ImageBitmap
)

@Composable
private fun rememberIntroImages(): IntroImages {
    val resources = LocalContext.current.resources
    return remember(resources) {
        IntroImages(
            logoSimple = BitmapFactory.decodeResource(resources, R.drawable.logo_simple_large).asImageBitmap(),
            logoEyes = BitmapFactory.decodeResource(resources, R.drawable.logo_eyes_large).asImageBitmap(),
            steveAvatar = BitmapFactory.decodeResource(resources, R.drawable.steve_avatar).asImageBitmap(),
            martinAvatar = BitmapFactory.decodeResource(resources, R.drawable.martin_avatar).asImageBitmap(),
            jannyAvatar = BitmapFactory.decodeResource(resources, R.drawable.janny_avatar).asImageBitmap(),
            guestAvatar = BitmapFactory.decodeResource(resources, R.drawable.guest_avatar).asImageBitmap(),
            steveName = BitmapFactory.decodeResource(resources, R.drawable.steve_name).asImageBitmap(),
            martinName = BitmapFactory.decodeResource(resources, R.drawable.martin_name).asImageBitmap(),
            jannyName = BitmapFactory.decodeResource(resources, R.drawable.janny_name).asImageBitmap(),
            guestName = BitmapFactory.decodeResource(resources, R.drawable.guest_name).asImageBitmap()
        )
    }
}

private fun DrawScope.drawFigmaImage(
    image: ImageBitmap,
    bounds: FigmaBounds,
    stageLeft: Float,
    stageTop: Float,
    stageScale: Float,
    alpha: Float = 1f,
    clipCircle: Boolean = false
) {
    val left = stageLeft + bounds.x * stageScale
    val top = stageTop + bounds.y * stageScale
    val width = bounds.w * stageScale
    val height = bounds.h * stageScale

    if (clipCircle) {
        val clip = Path().apply {
            addOval(Rect(left, top, left + width, top + height))
        }
        clipPath(clip) {
            drawImageBitmap(image, left, top, width, height, alpha)
        }
    } else {
        drawImageBitmap(image, left, top, width, height, alpha)
    }
}

private fun DrawScope.drawImageBitmap(
    image: ImageBitmap,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    alpha: Float
) {
    drawImage(
        image = image,
        dstOffset = IntOffset(left.roundToInt(), top.roundToInt()),
        dstSize = IntSize(width.roundToInt(), height.roundToInt()),
        alpha = alpha.coerceIn(0f, 1f)
    )
}

@Composable
private fun SettlingLogoLayer(
    scale: Float,
    entryProgress: Float,
    finalProgress: Float,
    alpha: Float
) {
    val baseBounds = FigmaBounds(x = 222f, y = 150f, w = 750f, h = 535f)
    val simpleEntry = lerpBounds(
        start = FigmaBounds(x = 447f, y = 1037f, w = 300f, h = 214f),
        end = baseBounds,
        amount = entryProgress
    )
    val eyesEntry = lerpBounds(
        start = FigmaBounds(x = 453f, y = -417f, w = 300f, h = 214f),
        end = baseBounds,
        amount = entryProgress
    )
    val finalBounds = FigmaBounds(x = 297f, y = 11f, w = 600f, h = 428f)
    val simpleBounds = lerpBounds(simpleEntry, finalBounds, finalProgress)
    val eyesBounds = lerpBounds(eyesEntry, finalBounds, finalProgress)

    TransformedFigmaAssetImage(
        base = baseBounds,
        target = simpleBounds,
        scale = scale,
        resId = R.drawable.logo_simple_large,
        alpha = alpha
    )
    TransformedFigmaAssetImage(
        base = baseBounds,
        target = eyesBounds,
        scale = scale,
        resId = R.drawable.logo_eyes_large,
        alpha = alpha
    )
}

@Composable
private fun AvatarSelectionLayer(scale: Float, alpha: Float, yOffset: Float) {
    val density = LocalDensity.current.density
    Box(
        modifier = Modifier
            .requiredSize(figma(1194f, scale), figma(834f, scale))
            .graphicsLayer {
                this.alpha = alpha
                translationY = yOffset * scale * density
            }
    ) {
        // Figma Logo Slide 2, Property 1=Variant2, constrained inside Sign-in Frame.
        FigmaAvatarImage(x = 130f, y = 428f, w = 220f, h = 220f, scale = scale, resId = R.drawable.steve_avatar)
        FigmaAvatarImage(x = 368f, y = 428f, w = 220f, h = 220f, scale = scale, resId = R.drawable.martin_avatar)
        FigmaAvatarImage(x = 606f, y = 432f, w = 220f, h = 220f, scale = scale, resId = R.drawable.janny_avatar)
        FigmaAvatarImage(x = 844f, y = 428f, w = 220f, h = 220f, scale = scale, resId = R.drawable.guest_avatar)

        FigmaAssetImage(x = 130f, y = 683f, w = 220f, h = 72f, scale = scale, resId = R.drawable.steve_name)
        FigmaAssetImage(x = 368f, y = 683f, w = 220f, h = 72f, scale = scale, resId = R.drawable.martin_name)
        FigmaAssetImage(x = 606f, y = 683f, w = 220f, h = 72f, scale = scale, resId = R.drawable.janny_name)
        FigmaAssetImage(x = 854f, y = 683f, w = 200f, h = 72f, scale = scale, resId = R.drawable.guest_name)
    }
}

@Composable
private fun FigmaAvatarImage(
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    scale: Float,
    @DrawableRes resId: Int
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .absoluteOffset(x = figma(x, scale), y = figma(y, scale))
            .requiredSize(figma(w, scale), figma(h, scale))
            .clip(CircleShape)
    )
}

@Composable
private fun LogoDot(x: Float, y: Float, scale: Float, active: Boolean) {
    Box(
        modifier = Modifier
            .absoluteOffset(x = figma(x, scale), y = figma(y, scale))
            .requiredSize(figma(50.4f, scale), figma(50.4f, scale))
            .clip(CircleShape)
            .background(if (active) Color(0xFFE7E7E7) else Color(0xFF050505))
    )
}

@Composable
private fun PromoBackgroundLayer(
    scale: Float,
    darkOverlay: Float,
    @DrawableRes backgroundResId: Int,
    alpha: Float = 1f
) {
    // Nodes 793:7049 / 793:7050 at x:0 y:0 size:1584x1584.
    FigmaAssetImage(
        x = 0f,
        y = 0f,
        w = 1584f,
        h = 1584f,
        scale = scale,
        resId = backgroundResId,
        alpha = alpha
    )

    if (darkOverlay > 0f) {
        Box(
            modifier = Modifier
                .absoluteOffset(x = figma(0f, scale), y = figma(0f, scale))
                .requiredSize(figma(1584f, scale), figma(1584f, scale))
                .graphicsLayer(alpha = alpha)
                .background(Color.Black.copy(alpha = darkOverlay))
        )
    }
}

@Composable
private fun TransformedFigmaAssetImage(
    base: FigmaBounds,
    target: FigmaBounds,
    scale: Float,
    @DrawableRes resId: Int,
    alpha: Float = 1f
) {
    val density = LocalDensity.current.density
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .absoluteOffset(x = figma(base.x, scale), y = figma(base.y, scale))
            .requiredSize(figma(base.w, scale), figma(base.h, scale))
            .graphicsLayer {
                this.alpha = alpha
                transformOrigin = TransformOrigin(0f, 0f)
                translationX = (target.x - base.x) * scale * density
                translationY = (target.y - base.y) * scale * density
                scaleX = target.w / base.w
                scaleY = target.h / base.h
            }
    )
}

@Composable
private fun FigmaAssetImage(
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    scale: Float,
    @DrawableRes resId: Int,
    alpha: Float = 1f
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .absoluteOffset(x = figma(x, scale), y = figma(y, scale))
            .requiredSize(figma(w, scale), figma(h, scale))
            .graphicsLayer(alpha = alpha)
    )
}

@Composable
private fun FigmaStage(background: Brush, content: @Composable BoxWithConstraintsScope.(Float) -> Unit) {
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

private fun introBackgroundBrush(solidProgress: Float, gradientProgress: Float): Brush {
    val solid = lerpColor(ColorFrame1Background, ColorFrame2Background, solidProgress)
    return Brush.verticalGradient(
        colors = listOf(
            lerpColor(solid, ColorIntroGradientTop, gradientProgress),
            lerpColor(solid, ColorIntroGradientCenter, gradientProgress),
            lerpColor(solid, ColorIntroGradientBottom, gradientProgress)
        )
    )
}

private fun solidBrush(color: Color): Brush = Brush.verticalGradient(
    colors = listOf(color, color)
)

private fun bootProgressAt(bootStartMillis: Long): Float {
    val elapsed = SystemClock.uptimeMillis() - bootStartMillis
    return (elapsed / BOOT_ANIMATION_MS.toFloat()).coerceIn(0f, 1f)
}

private fun easedSegment(value: Float, start: Float, end: Float): Float {
    val progress = ((value - start) / (end - start)).coerceIn(0f, 1f)
    return FastOutSlowInEasing.transform(progress)
}

private fun linearSegmentMs(progress: Float, startMs: Int, endMs: Int): Float {
    val ms = progress.coerceIn(0f, 1f) * BOOT_ANIMATION_MS
    return ((ms - startMs) / (endMs - startMs).toFloat()).coerceIn(0f, 1f)
}

private fun easedSegmentMs(progress: Float, startMs: Int, endMs: Int): Float {
    return FastOutSlowInEasing.transform(linearSegmentMs(progress, startMs, endMs))
}

private fun bouncySegmentMs(progress: Float, startMs: Int, endMs: Int): Float {
    return bouncyEasing(linearSegmentMs(progress, startMs, endMs))
}

private fun bouncyEasing(value: Float): Float {
    val t = value.coerceIn(0f, 1f)
    val base = FastOutSlowInEasing.transform(t)
    val bounce = sin(t * PI.toFloat() * 4.5f) * (1f - t) * 0.13f
    return base + bounce
}

private fun lerpFloat(start: Float, end: Float, amount: Float) = start + (end - start) * amount

private fun lerpBounds(start: FigmaBounds, end: FigmaBounds, amount: Float) = FigmaBounds(
    x = lerpFloat(start.x, end.x, amount),
    y = lerpFloat(start.y, end.y, amount),
    w = lerpFloat(start.w, end.w, amount),
    h = lerpFloat(start.h, end.h, amount)
)
