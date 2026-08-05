package com.prahlin.cinerific.ui

import android.graphics.BitmapFactory
import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.prahlin.cinerific.R
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private const val FIGMA_FRAME_WIDTH = 1194f
private const val FIGMA_FRAME_HEIGHT = 834f
private const val SCREEN_BLACK_HOLD_MS = 1000
private const val SCREEN_PINK_MS = 1000
private const val LOGO_ENTRY_START_MS = 250
private const val LOGO_ENTRY_END_MS = 1687
private const val FINAL_SETTLE_START_MS = LOGO_ENTRY_END_MS
private const val FINAL_SETTLE_END_MS = 4812
private const val BOOT_ANIMATION_MS = FINAL_SETTLE_END_MS
private const val AUTO_LOGOUT_TIMEOUT_MS = 10_000L
private const val FAVORITES_FULL_PROMPT_X = 977f
private const val FAVORITES_FULL_PROMPT_Y = 92f
private const val FAVORITES_FULL_PROMPT_WIDTH = 95f
private const val FAVORITES_FULL_PROMPT_HEIGHT = 139.25f
private const val FAVORITES_FULL_PROMPT_BOX_TOP = 20f
private const val FAVORITES_FULL_PROMPT_BOX_HEIGHT = 119f
private const val FAVORITES_FULL_PROMPT_RADIUS = 20f
private const val FAVORITES_FULL_PROMPT_STROKE = 3f
private const val FAVORITES_FULL_PROMPT_TEXT_X = 13.5f
private const val FAVORITES_FULL_PROMPT_TEXT_Y = 37f
private const val FAVORITES_FULL_PROMPT_TEXT_WIDTH = 68f
private const val FAVORITES_FULL_PROMPT_TEXT_HEIGHT = 84f
private const val FAVORITES_FULL_PROMPT_VISIBLE_MS = 2200L
private const val SIGN_IN_AVATAR_SIZE = 176f
private const val SIGN_IN_PORTRAIT_STACK_SHIFT_Y = -51f
private const val SIGN_IN_LANDSCAPE_STACK_SHIFT_Y = -61f
private const val SIGN_IN_NAME_TOP = 655f
private const val SIGN_IN_LANDSCAPE_ACCOUNT_PROMPT_EXTRA_SHIFT_Y = -36f
private const val ACCOUNT_PROMPT_CREATE_TEXT = "Create Account"
private const val ACCOUNT_PROMPT_SIGN_IN_TEXT = "Sign In"
private const val ACCOUNT_PROMPT_FORGOT_TEXT = "Forgot Password"
private const val ACCOUNT_PROMPT_LEFT_ANCHOR_X = 152f
private const val ACCOUNT_PROMPT_SIGN_IN_CENTER_X = 597f
private const val ACCOUNT_PROMPT_RIGHT_ANCHOR_X = 1042f
private const val ACCOUNT_PROMPT_WIDTH = 300f
private const val ACCOUNT_PROMPT_TOP = 770f
private const val ACCOUNT_PROMPT_HEIGHT = 45f
private const val ACCOUNT_PROMPT_TEXT_SIZE = 30f
private const val ACCOUNT_PROMPT_LANDSCAPE_TEXT_SIZE = 24.3f
private const val ACCOUNT_PROMPT_SECONDARY_TEXT_SCALE = 1f
private const val ACCOUNT_PROMPT_LINE_HEIGHT = 38f

private val ColorFrame1Background = Color(0xFF000000)
private val ColorFrame2Background = Color(0xFF600878)
private val ColorFrame3Background = Color(0xFF1F1F1F)
private val ColorFrame4Background = Color(0xFF1F1F1F)
private val ColorIntroGradientTop = Color(0xFF050006)
private val ColorIntroGradientCenter = Color(0xFF600878)
private val ColorIntroGradientBottom = Color(0xFF100114)
private val ColorFavoritesFullPromptFill = Color(0xFF303030)
private val ColorFavoritesFullPromptAccent = Color(0xFF858585)

@Composable
fun CinerificApp(bootStartMillis: Long = SystemClock.uptimeMillis()) {
    val introBootStartMillis by rememberSaveable { mutableStateOf(bootStartMillis) }
    var showHome by rememberSaveable { mutableStateOf(false) }
    var signedInProfile by rememberSaveable { mutableStateOf(CinerificProfile.Guest) }
    var selectedLanguage by rememberSaveable { mutableStateOf(CinerificLanguage.English) }
    var introSnapshot by rememberSaveable(stateSaver = CinerificIntroSnapshotSaver) {
        mutableStateOf(CinerificIntroSnapshot())
    }

    if (showHome) {
        CinerificLocalizedResources(selectedLanguage) {
            CinerificMainExperience(
                signedInProfile = signedInProfile,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it },
                onSignOut = {
                    signedInProfile = CinerificProfile.Guest
                    introSnapshot = CinerificIntroSnapshot()
                    showHome = false
                }
            )
        }
    } else {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                CinerificIntroView(context).apply {
                    this.bootStartMillis = introBootStartMillis
                    onIntroSnapshotChanged = { snapshot ->
                        if (introSnapshot != snapshot) {
                            introSnapshot = snapshot
                        }
                    }
                    restoreIntroSnapshot(introSnapshot)
                    onAvatarSelected = { profile ->
                        introSnapshot = CinerificIntroSnapshot()
                        signedInProfile = profile
                        showHome = true
                    }
                }
            },
            update = { view ->
                view.bootStartMillis = introBootStartMillis
                view.onIntroSnapshotChanged = { snapshot ->
                    if (introSnapshot != snapshot) {
                        introSnapshot = snapshot
                    }
                }
                view.restoreIntroSnapshot(introSnapshot)
                view.onAvatarSelected = { profile ->
                    introSnapshot = CinerificIntroSnapshot()
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
    Settings,
    ProgramDetails
}

internal enum class CinerificProfile(
    @DrawableRes val avatarResId: Int,
    @DrawableRes val nameResId: Int
) {
    Steve(R.drawable.steve_avatar_bubble_edge50_body0_test, R.drawable.steve_name),
    Martin(R.drawable.martin_avatar_bubble_edge50_body0_test, R.drawable.martin_name),
    Janny(R.drawable.janny_avatar_bubble_edge50_body0_test, R.drawable.janny_name),
    Guest(R.drawable.guest_avatar_bubble_edge50_body0_test, R.drawable.guest_name)
}

@Composable
private fun CinerificMainExperience(
    signedInProfile: CinerificProfile,
    selectedLanguage: CinerificLanguage,
    onLanguageSelected: (CinerificLanguage) -> Unit,
    onSignOut: () -> Unit
) {
    var destination by rememberSaveable { mutableStateOf(CinerificDestination.Home) }
    var selectedProgramTitle by rememberSaveable { mutableStateOf("Sink or Swim") }
    var favoriteProgramTitles by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var userProgramRatings by rememberSaveable { mutableStateOf(emptyMap<String, Int>()) }
    var catalogRouteDestinationName by rememberSaveable { mutableStateOf("") }
    var catalogRouteGenreName by rememberSaveable { mutableStateOf("") }
    var catalogRouteModeName by rememberSaveable { mutableStateOf("") }
    var favoritesFullPromptRequestId by remember { mutableStateOf(0) }
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
    val catalogRoute = remember(
        catalogRouteDestinationName,
        catalogRouteGenreName,
        catalogRouteModeName
    ) {
        cinerificSavedCatalogRoute(
            destinationName = catalogRouteDestinationName,
            genreName = catalogRouteGenreName,
            modeName = catalogRouteModeName
        )
    }

    fun clearCatalogRoute() {
        catalogRouteDestinationName = ""
        catalogRouteGenreName = ""
        catalogRouteModeName = ""
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

    fun showProgramDetails(title: String) {
        clearCatalogRoute()
        selectedProgramTitle = title
        destination = CinerificDestination.ProgramDetails
    }

    fun showCatalog(route: CinerificCatalogRoute) {
        catalogRouteDestinationName = route.destination.name
        catalogRouteGenreName = route.genre.name
        catalogRouteModeName = route.mode.name
        destination = route.destination
    }

    fun rateProgram(title: String, rating: Int) {
        userProgramRatings = userProgramRatings + (title to rating.coerceIn(1, 5))
    }

    fun toggleFavoriteProgram(title: String) {
        if (title in favoriteProgramTitles) {
            favoriteProgramTitles = favoriteProgramTitles - title
            return
        }

        val isShow = cinerificProgramIsShow(title)
        val filledSlots = cinerificFilledFavoritePlaceholderCount(
            titles = favoriteProgramTitles,
            isShow = isShow
        )
        if (filledSlots >= CINERIFIC_FAVORITES_CAPACITY) {
            favoritesFullPromptRequestId += 1
            return
        }

        favoriteProgramTitles = favoriteProgramTitles + title
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
                CinerificDestination.Home -> CinerificHomeScreen(
                    onProgramSelected = ::showProgramDetails,
                    onCatalogSelected = ::showCatalog,
                    modifier = Modifier.fillMaxSize()
                )
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
                    favoriteProgramTitles = favoriteProgramTitles,
                    onFavoriteToggled = ::toggleFavoriteProgram,
                    userProgramRatings = userProgramRatings,
                    onProgramRated = ::rateProgram,
                    onProgramSelected = ::showProgramDetails,
                    catalogRoute = catalogRoute?.takeIf { it.destination == destination },
                    modifier = Modifier.fillMaxSize()
                )
                CinerificDestination.ProgramDetails -> CinerificProgramDetailsScreen(
                    programTitle = selectedProgramTitle,
                    favoriteProgramTitles = favoriteProgramTitles,
                    onFavoriteToggled = ::toggleFavoriteProgram,
                    userProgramRatings = userProgramRatings,
                    onProgramRated = ::rateProgram,
                    onProgramSelected = ::showProgramDetails,
                    modifier = Modifier.fillMaxSize()
                )
            }

            CinerificRightSideNavBar(
                currentDestination = destination,
                onDestinationSelected = {
                    clearCatalogRoute()
                    destination = it
                },
                modifier = Modifier.fillMaxSize()
            )

            FavoritesFullPromptOverlay(
                requestId = favoritesFullPromptRequestId,
                currentDestination = destination,
                suppressed = userInitiatedPlaybackActive,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun cinerificSavedCatalogRoute(
    destinationName: String,
    genreName: String,
    modeName: String
): CinerificCatalogRoute? {
    if (destinationName.isBlank() || genreName.isBlank() || modeName.isBlank()) return null

    val destination = CinerificDestination.values().firstOrNull { it.name == destinationName } ?: return null
    val genre = ViewportGenre.values().firstOrNull { it.name == genreName } ?: return null
    val mode = ViewportMode.values().firstOrNull { it.name == modeName } ?: return null

    return CinerificCatalogRoute(
        destination = destination,
        genre = genre,
        mode = mode
    )
}

@Composable
private fun FavoritesFullPromptOverlay(
    requestId: Int,
    currentDestination: CinerificDestination,
    suppressed: Boolean,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(requestId, suppressed) {
        if (suppressed) {
            visible = false
            return@LaunchedEffect
        }
        if (requestId == 0) {
            visible = false
            return@LaunchedEffect
        }
        visible = true
        delay(FAVORITES_FULL_PROMPT_VISIBLE_MS)
        visible = false
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = if (visible) 140 else 190),
        label = "favorites-full-prompt-alpha"
    )
    val promptScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.94f,
        animationSpec = tween(durationMillis = if (visible) 140 else 190),
        label = "favorites-full-prompt-scale"
    )

    if (suppressed || requestId == 0 || (!visible && alpha <= 0.01f)) return

    BoxWithConstraints(modifier = modifier) {
        val stageScale = maxWidth.value / FIGMA_FRAME_WIDTH
        val isPortraitDetail = currentDestination == CinerificDestination.ProgramDetails &&
            maxHeight > maxWidth
        val promptY = if (isPortraitDetail) {
            DETAIL_HERO_LOGO_CENTER_Y - FAVORITES_FULL_PROMPT_HEIGHT / 2f
        } else {
            FAVORITES_FULL_PROMPT_Y
        }
        FavoritesFullPromptBubble(
            stageScale = stageScale,
            modifier = Modifier
                .absoluteOffset(
                    x = (FAVORITES_FULL_PROMPT_X * stageScale).dp,
                    y = (promptY * stageScale).dp
                )
                .graphicsLayer {
                    this.alpha = alpha
                    scaleX = promptScale
                    scaleY = promptScale
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
        )
    }
}

@Composable
private fun FavoritesFullPromptBubble(
    stageScale: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.requiredSize(
            width = (FAVORITES_FULL_PROMPT_WIDTH * stageScale).dp,
            height = (FAVORITES_FULL_PROMPT_HEIGHT * stageScale).dp
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val xScale = size.width / FAVORITES_FULL_PROMPT_WIDTH
            val yScale = size.height / FAVORITES_FULL_PROMPT_HEIGHT
            val pointer = Path().apply {
                moveTo(47.5f * xScale, 0f)
                lineTo(63f * xScale, FAVORITES_FULL_PROMPT_BOX_TOP * yScale)
                lineTo(32f * xScale, FAVORITES_FULL_PROMPT_BOX_TOP * yScale)
                close()
            }
            drawPath(pointer, ColorFavoritesFullPromptAccent)
            drawRoundRect(
                color = ColorFavoritesFullPromptFill,
                topLeft = Offset(0f, FAVORITES_FULL_PROMPT_BOX_TOP * yScale),
                size = Size(
                    width = FAVORITES_FULL_PROMPT_WIDTH * xScale,
                    height = FAVORITES_FULL_PROMPT_BOX_HEIGHT * yScale
                ),
                cornerRadius = CornerRadius(
                    x = FAVORITES_FULL_PROMPT_RADIUS * xScale,
                    y = FAVORITES_FULL_PROMPT_RADIUS * yScale
                )
            )
            drawRoundRect(
                color = ColorFavoritesFullPromptAccent,
                topLeft = Offset(
                    x = FAVORITES_FULL_PROMPT_STROKE * xScale / 2f,
                    y = FAVORITES_FULL_PROMPT_BOX_TOP * yScale +
                        FAVORITES_FULL_PROMPT_STROKE * yScale / 2f
                ),
                size = Size(
                    width = (FAVORITES_FULL_PROMPT_WIDTH - FAVORITES_FULL_PROMPT_STROKE) * xScale,
                    height = (FAVORITES_FULL_PROMPT_BOX_HEIGHT - FAVORITES_FULL_PROMPT_STROKE) * yScale
                ),
                cornerRadius = CornerRadius(
                    x = FAVORITES_FULL_PROMPT_RADIUS * xScale,
                    y = FAVORITES_FULL_PROMPT_RADIUS * yScale
                ),
                style = Stroke(width = FAVORITES_FULL_PROMPT_STROKE * min(xScale, yScale))
            )
        }
        Text(
            text = "You've\nfilled\nup on\nfavorites!",
            color = ColorFavoritesFullPromptAccent,
            fontFamily = CinerificAppTextFontFamily,
            fontSize = (14f * stageScale).sp,
            fontWeight = FontWeight.Black,
            lineHeight = (21.252f * stageScale).sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .absoluteOffset(
                    x = (FAVORITES_FULL_PROMPT_TEXT_X * stageScale).dp,
                    y = (FAVORITES_FULL_PROMPT_TEXT_Y * stageScale).dp
                )
                .requiredSize(
                    width = (FAVORITES_FULL_PROMPT_TEXT_WIDTH * stageScale).dp,
                    height = (FAVORITES_FULL_PROMPT_TEXT_HEIGHT * stageScale).dp
                )
        )
    }
}

@Composable
private fun BootIntroFromFigma(progress: Float) {
    val p = progress.coerceIn(0f, 1f)
    val images = rememberIntroImages()
    val blackToPurple = linearSegmentMs(
        p,
        SCREEN_BLACK_HOLD_MS,
        SCREEN_BLACK_HOLD_MS + SCREEN_PINK_MS
    )
    val purpleToSettle = easedSegmentMs(p, 2500, 3900)
    val background = introBackgroundBrush(
        solidProgress = blackToPurple,
        gradientProgress = purpleToSettle
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
        val stackShiftY = signInStackShiftY(size.width > size.height)

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
            drawFigmaImage(images.steveAvatar, FigmaBounds(152f, 450f + stackShiftY + y, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE), stageLeft, stageTop, stageScale, avatarSettleAlpha, clipCircle = true)
            drawFigmaImage(images.martinAvatar, FigmaBounds(390f, 450f + stackShiftY + y, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE), stageLeft, stageTop, stageScale, avatarSettleAlpha, clipCircle = true)
            drawFigmaImage(images.jannyAvatar, FigmaBounds(628f, 454f + stackShiftY + y, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE), stageLeft, stageTop, stageScale, avatarSettleAlpha, clipCircle = true)
            drawFigmaImage(images.guestAvatar, FigmaBounds(866f, 450f + stackShiftY + y, SIGN_IN_AVATAR_SIZE, SIGN_IN_AVATAR_SIZE), stageLeft, stageTop, stageScale, avatarSettleAlpha, clipCircle = true)

            drawFigmaImage(images.steveName, FigmaBounds(130f, SIGN_IN_NAME_TOP + stackShiftY + y, 220f, 72f), stageLeft, stageTop, stageScale, avatarSettleAlpha)
            drawFigmaImage(images.martinName, FigmaBounds(368f, SIGN_IN_NAME_TOP + stackShiftY + y, 220f, 72f), stageLeft, stageTop, stageScale, avatarSettleAlpha)
            drawFigmaImage(images.jannyName, FigmaBounds(606f, SIGN_IN_NAME_TOP + stackShiftY + y, 220f, 72f), stageLeft, stageTop, stageScale, avatarSettleAlpha)
            drawFigmaImage(images.guestName, FigmaBounds(854f, SIGN_IN_NAME_TOP + stackShiftY + y, 200f, 72f), stageLeft, stageTop, stageScale, avatarSettleAlpha)
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

        // Node 485:7113 loading spinner instance, rebuilt from centered standalone layers.
        val spinnerHeight = 300f *
            CINERIFIC_LOADING_SPINNER_CANVAS_HEIGHT /
            CINERIFIC_LOADING_SPINNER_CANVAS_WIDTH
        CinerificLoadingSpinner(
            modifier = Modifier
                .absoluteOffset(
                    x = figma(447f, scale),
                    y = figma(267f - (spinnerHeight - 300f) / 2f, scale)
                )
                .requiredSize(figma(300f, scale), figma(spinnerHeight, scale))
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
            steveAvatar = BitmapFactory.decodeResource(resources, R.drawable.steve_avatar_bubble_edge50_body0_test).asImageBitmap(),
            martinAvatar = BitmapFactory.decodeResource(resources, R.drawable.martin_avatar_bubble_edge50_body0_test).asImageBitmap(),
            jannyAvatar = BitmapFactory.decodeResource(resources, R.drawable.janny_avatar_bubble_edge50_body0_test).asImageBitmap(),
            guestAvatar = BitmapFactory.decodeResource(resources, R.drawable.guest_avatar_bubble_edge50_body0_test).asImageBitmap(),
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val stackShiftY = signInStackShiftY(isLandscape)
    val accountPromptExtraShiftY = signInAccountPromptExtraShiftY(isLandscape)
    val accountPromptTextSize = signInAccountPromptTextSize(isLandscape)
    val accountPromptSecondaryTextSize = accountPromptTextSize * ACCOUNT_PROMPT_SECONDARY_TEXT_SCALE
    Box(
        modifier = Modifier
            .requiredSize(figma(1194f, scale), figma(834f, scale))
            .graphicsLayer {
                this.alpha = alpha
                translationY = yOffset * scale * density
            }
    ) {
        // Figma Logo Slide 2, Property 1=Variant2, constrained inside Sign-in Frame.
        FigmaAvatarImage(x = 152f, y = 450f + stackShiftY, w = SIGN_IN_AVATAR_SIZE, h = SIGN_IN_AVATAR_SIZE, scale = scale, resId = R.drawable.steve_avatar_bubble_edge50_body0_test)
        FigmaAvatarImage(x = 390f, y = 450f + stackShiftY, w = SIGN_IN_AVATAR_SIZE, h = SIGN_IN_AVATAR_SIZE, scale = scale, resId = R.drawable.martin_avatar_bubble_edge50_body0_test)
        FigmaAvatarImage(x = 628f, y = 454f + stackShiftY, w = SIGN_IN_AVATAR_SIZE, h = SIGN_IN_AVATAR_SIZE, scale = scale, resId = R.drawable.janny_avatar_bubble_edge50_body0_test)
        FigmaAvatarImage(x = 866f, y = 450f + stackShiftY, w = SIGN_IN_AVATAR_SIZE, h = SIGN_IN_AVATAR_SIZE, scale = scale, resId = R.drawable.guest_avatar_bubble_edge50_body0_test)

        FigmaAssetImage(x = 130f, y = SIGN_IN_NAME_TOP + stackShiftY, w = 220f, h = 72f, scale = scale, resId = R.drawable.steve_name)
        FigmaAssetImage(x = 368f, y = SIGN_IN_NAME_TOP + stackShiftY, w = 220f, h = 72f, scale = scale, resId = R.drawable.martin_name)
        FigmaAssetImage(x = 606f, y = SIGN_IN_NAME_TOP + stackShiftY, w = 220f, h = 72f, scale = scale, resId = R.drawable.janny_name)
        FigmaAssetImage(x = 854f, y = SIGN_IN_NAME_TOP + stackShiftY, w = 200f, h = 72f, scale = scale, resId = R.drawable.guest_name)
        SignInPromptText(
            text = ACCOUNT_PROMPT_CREATE_TEXT,
            x = ACCOUNT_PROMPT_LEFT_ANCHOR_X,
            y = ACCOUNT_PROMPT_TOP + stackShiftY + accountPromptExtraShiftY,
            textSize = accountPromptSecondaryTextSize,
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.Black,
            scale = scale
        )
        SignInPromptText(
            text = ACCOUNT_PROMPT_SIGN_IN_TEXT,
            x = ACCOUNT_PROMPT_SIGN_IN_CENTER_X - ACCOUNT_PROMPT_WIDTH / 2f,
            y = ACCOUNT_PROMPT_TOP + stackShiftY + accountPromptExtraShiftY,
            textSize = accountPromptTextSize,
            fontWeight = FontWeight.Black,
            scale = scale
        )
        SignInPromptText(
            text = ACCOUNT_PROMPT_FORGOT_TEXT,
            x = ACCOUNT_PROMPT_RIGHT_ANCHOR_X - ACCOUNT_PROMPT_WIDTH,
            y = ACCOUNT_PROMPT_TOP + stackShiftY + accountPromptExtraShiftY,
            textSize = accountPromptSecondaryTextSize,
            textAlign = TextAlign.Right,
            fontWeight = FontWeight.Black,
            scale = scale
        )
    }
}

@Composable
private fun SignInPromptText(
    text: String,
    x: Float,
    y: Float,
    textSize: Float,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textAlign: TextAlign = TextAlign.Center,
    scale: Float
) {
    val contentAlignment = when (textAlign) {
        TextAlign.Left -> Alignment.CenterStart
        TextAlign.Right -> Alignment.CenterEnd
        else -> Alignment.Center
    }
    Box(
        modifier = Modifier
            .absoluteOffset(
                x = figma(x, scale),
                y = figma(y, scale)
            )
            .requiredSize(
                width = figma(ACCOUNT_PROMPT_WIDTH, scale),
                height = figma(ACCOUNT_PROMPT_HEIGHT, scale)
            ),
        contentAlignment = contentAlignment
    ) {
        Text(
            text = text,
            color = Color.White,
            fontFamily = CinerificAppTextFontFamily,
            fontSize = (textSize * scale).sp,
            fontWeight = fontWeight,
            lineHeight = (ACCOUNT_PROMPT_LINE_HEIGHT * textSize / ACCOUNT_PROMPT_TEXT_SIZE * scale).sp,
            maxLines = 1,
            softWrap = false,
            textAlign = textAlign
        )
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

private fun signInStackShiftY(isLandscape: Boolean): Float {
    return if (isLandscape) {
        SIGN_IN_LANDSCAPE_STACK_SHIFT_Y
    } else {
        SIGN_IN_PORTRAIT_STACK_SHIFT_Y
    }
}

private fun signInAccountPromptExtraShiftY(isLandscape: Boolean): Float {
    return if (isLandscape) SIGN_IN_LANDSCAPE_ACCOUNT_PROMPT_EXTRA_SHIFT_Y else 0f
}

private fun signInAccountPromptTextSize(isLandscape: Boolean): Float {
    return if (isLandscape) ACCOUNT_PROMPT_LANDSCAPE_TEXT_SIZE else ACCOUNT_PROMPT_TEXT_SIZE
}

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

private val CinerificIntroSnapshotSaver = listSaver<CinerificIntroSnapshot, Any>(
    save = { snapshot ->
        listOf(
            snapshot.activeFlowName,
            snapshot.usernameText,
            snapshot.passwordText,
            snapshot.confirmPasswordText,
            snapshot.emailText,
            snapshot.monthText,
            snapshot.dayText,
            snapshot.yearText,
            snapshot.subscriptionTierText,
            snapshot.forgotRecoveryTargetName,
            snapshot.forgotPasswordSubmissionStateName,
            snapshot.rememberMeChecked,
            snapshot.createAvatarIndex
        )
    },
    restore = { values ->
        CinerificIntroSnapshot(
            activeFlowName = values.getOrNull(0) as? String ?: "",
            usernameText = values.getOrNull(1) as? String ?: "",
            passwordText = values.getOrNull(2) as? String ?: "",
            confirmPasswordText = values.getOrNull(3) as? String ?: "",
            emailText = values.getOrNull(4) as? String ?: "",
            monthText = values.getOrNull(5) as? String ?: "Month",
            dayText = values.getOrNull(6) as? String ?: "Day",
            yearText = values.getOrNull(7) as? String ?: "Year",
            subscriptionTierText = values.getOrNull(8) as? String ?: "Subscription Tier",
            forgotRecoveryTargetName = values.getOrNull(9) as? String ?: "Password",
            forgotPasswordSubmissionStateName = values.getOrNull(10) as? String ?: "Idle",
            rememberMeChecked = values.getOrNull(11) as? Boolean ?: false,
            createAvatarIndex = values.getOrNull(12) as? Int ?: 0
        )
    }
)
