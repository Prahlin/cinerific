package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prahlin.cinerific.R
import kotlinx.coroutines.delay
import kotlin.math.abs

private const val DESTINATION_FRAME_WIDTH = 1194f
private const val DESTINATION_TOP_BAR_TITLE_BOTTOM = 18f
private const val DESTINATION_CARD_ASPECT = 350f / 263f
private const val DESTINATION_CARD_SCALE = 0.8f
private const val SETTINGS_CONTROL_WIDTH = 150f
private const val SETTINGS_CONTROL_HEIGHT = 75f
private const val SETTINGS_CONTROL_RADIUS = 50f
private const val SETTINGS_CONTROL_BORDER_WIDTH = 2f
private const val SETTINGS_CONTROL_SHADOW = 2f
private const val SETTINGS_SCREEN_HORIZONTAL_PADDING = 64f
private const val SETTINGS_ROW_TEXT_WIDTH = 350f
private const val SETTINGS_CONTROL_COLUMN_GAP = 64.5f
private const val SETTINGS_SECTION_BACKGROUND_RADIUS = 22f
private const val SETTINGS_SECTION_BOTTOM_PADDING = 42f
private const val SETTINGS_SECTION_HEADER_HEIGHT = 58f
private const val SETTINGS_SECTION_HEADER_RADIUS = 36f
private const val SETTINGS_SECTION_HEADER_HORIZONTAL_PADDING = 54f
private const val SETTINGS_SECTION_HEADER_LEFT_BLEED = 24f
private const val SETTINGS_SECTION_BODY_START_PADDING = 0f
private const val SETTINGS_LANGUAGE_MENU_HEIGHT = 116f
private const val SETTINGS_LANGUAGE_MENU_ROW_HEIGHT = 34f
private const val SETTINGS_LANGUAGE_TEXT_SIZE = 21.6f
private const val SETTINGS_LANGUAGE_LINE_HEIGHT = 32.784f
private const val SETTINGS_LANGUAGE_BUTTON_PADDING = 18f
private const val SETTINGS_LANGUAGE_ARROW_WIDTH = 23.4f
private const val SETTINGS_LANGUAGE_ARROW_HEIGHT = 27.020f
private const val SETTINGS_LANGUAGE_ANIMATION_MS = 120
private const val SETTINGS_SIGNED_IN_LEFT = 754f
private const val SETTINGS_SIGNED_IN_TOP_AFTER_BAR = 48f
private const val SETTINGS_SIGNED_IN_WIDTH = 200f
private const val SETTINGS_SIGNED_IN_HEIGHT = 481f
private const val SETTINGS_SIGNED_IN_PROFILE_SCALE = 0.9f
private const val SETTINGS_SIGNED_IN_TITLE_TOP = -3f
private const val SETTINGS_SIGNED_IN_TITLE_HEIGHT = 45f
private const val SETTINGS_SIGNED_IN_NAME_GAP = 21.8f
private const val SETTINGS_SIGNED_IN_AVATAR_TOP = SETTINGS_SIGNED_IN_TITLE_TOP +
    SETTINGS_SIGNED_IN_TITLE_HEIGHT +
    SETTINGS_SIGNED_IN_NAME_GAP * SETTINGS_SIGNED_IN_PROFILE_SCALE
private const val SETTINGS_SIGNED_IN_AVATAR_SIZE = 200.2f
private const val SETTINGS_SIGNED_IN_NAME_WIDTH = 200f
private const val SETTINGS_SIGNED_IN_NAME_HEIGHT = 40f
private const val SETTINGS_SIGNED_IN_SIGN_OUT_GAP = 50f

private val DestinationTop = Color(0xFF080007)
private val DestinationMid = Color(0xFF23001F)
private val DestinationBottom = Color(0xFF060004)
private val DestinationText = Color(0xFFE7E7E7)
private val DestinationSubtle = Color(0xFFBDBDBD)
private val SettingsLanguageOptions = listOf("English", "Spanish", "Mandarin")

@Composable
internal fun CinerificDestinationScreen(
    destination: CinerificDestination,
    signedInProfile: CinerificProfile = CinerificProfile.Guest,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (destination) {
        CinerificDestination.Movies -> CinerificCatalogScreen(
            title = "Movies",
            description = "Explore a curated selection of exhilarating films",
            rows = MovieRows,
            showViewportNav = true,
            modifier = modifier
        )
        CinerificDestination.Shows -> CinerificCatalogScreen(
            title = "Shows",
            description = "Browse episodic picks, genre collections, and returning favorites",
            rows = ShowRows,
            showViewportNav = true,
            modifier = modifier
        )
        CinerificDestination.Favorites -> CinerificFavoritesScreen(modifier = modifier)
        CinerificDestination.Settings -> CinerificSettingsScreen(
            signedInProfile = signedInProfile,
            onSignOut = onSignOut,
            modifier = modifier
        )
        CinerificDestination.Home -> CinerificHomeScreen(modifier = modifier)
    }
}

@Composable
private fun CinerificCatalogScreen(
    title: String,
    rows: List<DestinationRowSpec>,
    description: String = "",
    showViewportNav: Boolean,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DestinationBottom)
    ) {
        val scale = maxWidth.value / DESTINATION_FRAME_WIDTH
        val density = LocalDensity.current
        val horizontalPadding = destinationDp(50f, scale)
        val rightPadding = destinationDp(150f, scale)
        val navScale = cinerificNavScale(maxWidth, maxHeight)
        val titleBottomPadding = destinationDp(DESTINATION_TOP_BAR_TITLE_BOTTOM, navScale)
        val statusBarTop = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
        val topBarHeight = cinerificTopRailHeight(maxWidth, maxHeight, statusBarTop)
        val cardWidth = destinationDp(350f, scale) * DESTINATION_CARD_SCALE
        val cardHeight = cardWidth / DESTINATION_CARD_ASPECT
        val cardGap = destinationDp(50f, scale)
        val contentColumnCount = (
            (maxWidth.value - horizontalPadding.value - rightPadding.value + cardGap.value) /
                (cardWidth.value + cardGap.value)
            ).toInt().coerceAtLeast(1)
        val bottomSystemPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        var selectedGenre by remember(title) { mutableStateOf(ViewportGenre.All) }
        var selectedMode by remember(title) { mutableStateOf(ViewportMode.CollageLarge) }
        val scrollState = rememberScrollState()
        val visibleRows = if (!showViewportNav || selectedGenre == ViewportGenre.All) {
            rows
        } else {
            rows.filter { it.genre == selectedGenre }
        }
        LaunchedEffect(title, selectedGenre, selectedMode) {
            scrollState.scrollTo(0)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DestinationTop, DestinationMid, DestinationBottom)
                    )
                )
                .padding(top = topBarHeight)
        ) {
            if (showViewportNav) {
                DestinationViewportHeader(
                    description = description,
                    selectedGenre = selectedGenre,
                    selectedMode = selectedMode,
                    onGenreSelected = { selectedGenre = it },
                    onModeSelected = { selectedMode = it },
                    scale = navScale,
                    horizontalPadding = horizontalPadding,
                    rightPadding = rightPadding
                )
            }

            when (selectedMode) {
                ViewportMode.CollageLarge -> {
                    visibleRows.forEachIndexed { index, row ->
                        DestinationProgramRow(
                            title = row.title,
                            programs = row.programs,
                            horizontalPadding = horizontalPadding,
                            rightPadding = rightPadding,
                            cardWidth = cardWidth,
                            cardHeight = cardHeight,
                            cardGap = cardGap,
                            columnCount = contentColumnCount,
                            topPadding = destinationSectionTopPadding(index = index, showViewportNav = showViewportNav)
                        )
                    }
                }
                ViewportMode.CollageSmall -> {
                    visibleRows.forEachIndexed { index, row ->
                        DestinationProgramSmallCollage(
                            title = row.title,
                            programs = smallCollagePrograms(destinationTitle = title, programs = row.programs),
                            horizontalPadding = horizontalPadding,
                            rightPadding = rightPadding,
                            scale = scale,
                            topPadding = destinationSectionTopPadding(index = index, showViewportNav = showViewportNav)
                        )
                    }
                }
                ViewportMode.List -> {
                    visibleRows.forEachIndexed { index, row ->
                        DestinationProgramList(
                            title = row.title,
                            programs = listPrograms(row.programs),
                            horizontalPadding = horizontalPadding,
                            rightPadding = rightPadding,
                            scale = scale,
                            topPadding = destinationSectionTopPadding(index = index, showViewportNav = showViewportNav)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp + bottomSystemPadding))
        }

        DestinationTopBar(
            title = title,
            height = topBarHeight,
            horizontalPadding = horizontalPadding,
            rightPadding = rightPadding,
            titleBottomPadding = titleBottomPadding
        )
    }
}

@Composable
private fun DestinationViewportHeader(
    description: String,
    selectedGenre: ViewportGenre,
    selectedMode: ViewportMode,
    onGenreSelected: (ViewportGenre) -> Unit,
    onModeSelected: (ViewportMode) -> Unit,
    scale: Float,
    horizontalPadding: Dp,
    rightPadding: Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = rightPadding)
            .padding(top = destinationDp(34f, scale)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = description,
            color = DestinationText.copy(alpha = 0.86f),
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 30.sp,
            letterSpacing = 0.sp,
            modifier = Modifier
                .weight(1f)
                .padding(end = destinationDp(52f, scale))
        )
        CinerificViewportNavBar(
            selectedGenre = selectedGenre,
            selectedMode = selectedMode,
            onGenreSelected = onGenreSelected,
            onModeSelected = onModeSelected,
            scale = scale,
            modifier = Modifier.width(destinationDp(401f, scale))
        )
    }
}

@Composable
private fun DestinationProgramRow(
    title: String,
    programs: List<DestinationProgramSpec>,
    horizontalPadding: Dp,
    rightPadding: Dp,
    cardWidth: Dp,
    cardHeight: Dp,
    cardGap: Dp,
    columnCount: Int,
    topPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
    ) {
        DestinationSectionHeader(
            title = title,
            horizontalPadding = horizontalPadding,
            rightPadding = rightPadding
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = horizontalPadding, end = rightPadding)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(cardGap)
        ) {
            programs.chunked(columnCount).forEach { rowPrograms ->
                Row(horizontalArrangement = Arrangement.spacedBy(cardGap)) {
                    rowPrograms.forEach { program ->
                        DestinationProgramCard(
                            drawableId = program.drawableId,
                            width = cardWidth,
                            height = cardHeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationSectionHeader(
    title: String,
    horizontalPadding: Dp,
    rightPadding: Dp
) {
    Text(
        text = title,
        color = DestinationText,
        fontSize = 36.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.sp,
        modifier = Modifier.padding(start = horizontalPadding, end = rightPadding)
    )
}

@Composable
private fun DestinationProgramSmallCollage(
    title: String,
    programs: List<DestinationProgramSpec>,
    horizontalPadding: Dp,
    rightPadding: Dp,
    scale: Float,
    topPadding: Dp
) {
    val itemWidth = destinationDp(200f, scale)
    val cardHeight = destinationDp(150.4f, scale)
    val horizontalCardGap = destinationDp(59f, scale)
    val verticalCardGap = destinationDp(50f, scale)
    val columnCount = 4

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
    ) {
        DestinationSectionHeader(
            title = title,
            horizontalPadding = horizontalPadding,
            rightPadding = rightPadding
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = horizontalPadding, end = rightPadding)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(verticalCardGap)
        ) {
            programs.chunked(columnCount).forEach { rowPrograms ->
                Row(horizontalArrangement = Arrangement.spacedBy(horizontalCardGap)) {
                    rowPrograms.forEach { program ->
                        DestinationSmallCollageCard(
                            program = program,
                            width = itemWidth,
                            cardHeight = cardHeight,
                            scale = scale
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationSmallCollageCard(
    program: DestinationProgramSpec,
    width: Dp,
    cardHeight: Dp,
    scale: Float
) {
    val shape = RoundedCornerShape(destinationDp(30f, scale))

    Column(
        modifier = Modifier.width(width),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(destinationDp(20f, scale))
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(cardHeight)
                .shadow(destinationDp(14f, scale), shape, clip = false)
                .clip(shape)
                .background(Color.Black)
                .border(destinationDp(0.63f, scale), Color.Black, shape)
        ) {
            Image(
                painter = painterResource(program.drawableId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = program.title.uppercase(),
            color = DestinationText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DestinationProgramList(
    title: String,
    programs: List<DestinationProgramSpec>,
    horizontalPadding: Dp,
    rightPadding: Dp,
    scale: Float,
    topPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
    ) {
        DestinationSectionHeader(
            title = title,
            horizontalPadding = horizontalPadding,
            rightPadding = rightPadding
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = horizontalPadding, end = rightPadding)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(destinationDp(100f, scale))
        ) {
            programs.forEach { program ->
                DestinationProgramListItem(
                    program = program,
                    scale = scale
                )
            }
        }
    }
}

@Composable
private fun DestinationProgramListItem(
    program: DestinationProgramSpec,
    scale: Float
) {
    val imageShape = RoundedCornerShape(destinationDp(15f, scale))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(destinationDp(237f, scale)),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(destinationDp(300f, scale))
                .height(destinationDp(225f, scale))
                .shadow(destinationDp(14f, scale), imageShape, clip = false)
                .clip(imageShape)
                .background(Color.Black)
        ) {
            Image(
                painter = painterResource(program.listDrawableId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .height(destinationDp(222f, scale))
                .padding(start = destinationDp(50f, scale)),
            verticalArrangement = Arrangement.spacedBy(destinationDp(20f, scale))
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                        append(program.title.uppercase())
                    }
                    append(" ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                        append("(${program.year})")
                    }
                },
                color = DestinationText,
                fontSize = 32.sp,
                lineHeight = 49.sp,
                letterSpacing = 0.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.height(destinationDp(76f, scale)),
                horizontalArrangement = Arrangement.spacedBy(destinationDp(37f, scale)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = program.synopsis,
                    color = DestinationText.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 24.sp,
                    letterSpacing = 0.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(destinationDp(298f, scale))
                )
                DestinationListMetaColumn(
                    top = program.runtime,
                    bottom = program.metadataGenre,
                    width = destinationDp(71f, scale),
                    scale = scale
                )
                DestinationListMetaColumn(
                    top = "Dir: ${program.director}",
                    bottom = "Prod: ${program.producer}",
                    width = destinationDp(156f, scale),
                    scale = scale
                )
            }
            DestinationRatingStars(
                rating = program.rating,
                scale = scale
            )
        }
    }
}

@Composable
private fun DestinationListMetaColumn(
    top: String,
    bottom: String,
    width: Dp,
    scale: Float
) {
    Column(
        modifier = Modifier
            .width(width)
            .height(destinationDp(76f, scale)),
        verticalArrangement = Arrangement.spacedBy(destinationDp(25f, scale))
    ) {
        DestinationListMetaText(text = top)
        DestinationListMetaText(text = bottom)
    }
}

@Composable
private fun DestinationListMetaText(text: String) {
    Text(
        text = text,
        color = DestinationText.copy(alpha = 0.9f),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun DestinationRatingStars(
    rating: Int,
    scale: Float
) {
    Row(
        modifier = Modifier
            .width(destinationDp(192f, scale))
            .height(destinationDp(40f, scale)),
        horizontalArrangement = Arrangement.spacedBy(destinationDp(8f, scale)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFC91B) else Color(0xFFC9C4CC),
                modifier = Modifier.size(destinationDp(26f, scale))
            )
        }
    }
}

@Composable
private fun DestinationProgramCard(
    @DrawableRes drawableId: Int,
    width: Dp,
    height: Dp
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(12.dp, shape, clip = false)
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

@Composable
private fun CinerificSettingsScreen(
    signedInProfile: CinerificProfile,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DestinationBottom)
    ) {
        val scale = maxWidth.value / DESTINATION_FRAME_WIDTH
        val density = LocalDensity.current
        val horizontalPadding = destinationDp(SETTINGS_SCREEN_HORIZONTAL_PADDING, scale)
        val rightPadding = destinationDp(160f, scale)
        val navScale = cinerificNavScale(maxWidth, maxHeight)
        val titleBottomPadding = destinationDp(DESTINATION_TOP_BAR_TITLE_BOTTOM, navScale)
        val statusBarTop = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
        val topBarHeight = cinerificTopRailHeight(maxWidth, maxHeight, statusBarTop)
        val bottomSystemPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        val settingsScrollState = rememberScrollState()
        var bottomToggleCenterY by remember { mutableStateOf<Float?>(null) }
        var signOutCenterY by remember { mutableStateOf<Float?>(null) }
        var bottomAlignmentSpacerPx by remember { mutableStateOf(0f) }
        var keepSettingsBottomPinned by remember { mutableStateOf(false) }
        val bottomAlignmentSpacer = with(density) { bottomAlignmentSpacerPx.toDp() }

        LaunchedEffect(
            bottomToggleCenterY,
            signOutCenterY,
            settingsScrollState.value,
            settingsScrollState.maxValue,
            bottomAlignmentSpacerPx
        ) {
            val toggleCenterY = bottomToggleCenterY ?: return@LaunchedEffect
            val buttonCenterY = signOutCenterY ?: return@LaunchedEffect
            val toggleContentCenterY = toggleCenterY + settingsScrollState.value
            val targetMaxScroll = toggleContentCenterY - buttonCenterY
            val spacerAdjustment = targetMaxScroll - settingsScrollState.maxValue
            val nextSpacer = (bottomAlignmentSpacerPx + spacerAdjustment).coerceAtLeast(0f)

            if (abs(nextSpacer - bottomAlignmentSpacerPx) > 1f) {
                keepSettingsBottomPinned = settingsScrollState.value >= settingsScrollState.maxValue - 2
                bottomAlignmentSpacerPx = nextSpacer
            }
        }

        LaunchedEffect(bottomAlignmentSpacerPx) {
            if (!keepSettingsBottomPinned) return@LaunchedEffect
            withFrameNanos { }
            settingsScrollState.scrollTo(settingsScrollState.maxValue)
            keepSettingsBottomPinned = false
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(settingsScrollState)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DestinationTop, DestinationMid, DestinationBottom)
                    )
                )
                .padding(
                    start = horizontalPadding,
                    top = topBarHeight,
                    end = rightPadding,
                    bottom = bottomSystemPadding
                )
        ) {
            SettingsSection(
                title = "Accessibility",
                rows = listOf(
                    SettingsRowSpec(
                        label = "Language",
                        detail = "Select your language of choice",
                        control = SettingsControl.LanguageDropdown
                    ),
                    SettingsRowSpec(label = "Simplify UI", detail = "Recommended for children"),
                    SettingsRowSpec(label = "Dark mode", detail = "App darkens during night time")
                ),
                scale = scale
            )
            SettingsSection(
                title = "Playback",
                rows = listOf(
                    SettingsRowSpec(label = "Autoplay", detail = "Automatically play the next video"),
                    SettingsRowSpec(label = "Background play", detail = "Play Cinerific behind other apps"),
                    SettingsRowSpec(label = "Wifi downloads only", detail = "Some carriers will charge for data")
                ),
                scale = scale
            )
            SettingsSection(
                title = "Billing & Payment",
                rows = listOf(
                    SettingsRowSpec(
                        label = "Bill automatically",
                        detail = "Only applicable if billing information provided"
                    ),
                    SettingsRowSpec(
                        label = "Notifications",
                        detail = "Receive email notifications 7 days prior to your billing date"
                    )
                ),
                scale = scale
            )
            SettingsSection(
                title = "Security",
                rows = listOf(
                    SettingsRowSpec(
                        label = "Auto log out",
                        detail = "Log out automatically following 10 minutes of inactivity"
                    ),
                    SettingsRowSpec(
                        label = "Two-Factor Authentication",
                        detail = "Provides an extra level of security"
                    )
                ),
                scale = scale,
                onLastToggleCenterMeasured = { bottomToggleCenterY = it }
            )
            Spacer(modifier = Modifier.height(bottomAlignmentSpacer))
        }

        SettingsSignedInColumn(
            profile = signedInProfile,
            scale = scale,
            onSignOut = onSignOut,
            onSignOutCenterMeasured = { signOutCenterY = it },
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = destinationDp(SETTINGS_SIGNED_IN_LEFT, scale),
                    y = topBarHeight + destinationDp(SETTINGS_SIGNED_IN_TOP_AFTER_BAR, scale)
                )
        )

        DestinationTopBar(
            title = "Settings",
            height = topBarHeight,
            horizontalPadding = horizontalPadding,
            rightPadding = rightPadding,
            titleBottomPadding = titleBottomPadding
        )
    }
}

@Composable
private fun CinerificFavoritesScreen(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DestinationBottom)
    ) {
        val scale = maxWidth.value / DESTINATION_FRAME_WIDTH
        val density = LocalDensity.current
        val horizontalPadding = destinationDp(50f, scale)
        val rightPadding = destinationDp(150f, scale)
        val navScale = cinerificNavScale(maxWidth, maxHeight)
        val titleBottomPadding = destinationDp(DESTINATION_TOP_BAR_TITLE_BOTTOM, navScale)
        val statusBarTop = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
        val topBarHeight = cinerificTopRailHeight(maxWidth, maxHeight, statusBarTop)
        val bottomSystemPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DestinationTop, DestinationMid, DestinationBottom)
                    )
                )
                .padding(top = topBarHeight, bottom = 80.dp + bottomSystemPadding)
        ) {
            Spacer(modifier = Modifier.height(destinationDp(61f, scale)))
            FavoritesPlaceholderSection(
                title = "Movies",
                horizontalPadding = horizontalPadding,
                scale = scale
            )
            Spacer(modifier = Modifier.height(destinationDp(82f, scale)))
            FavoritesPlaceholderSection(
                title = "Shows",
                horizontalPadding = horizontalPadding,
                scale = scale
            )
            FavoritesSummaryHeaders(
                horizontalPadding = horizontalPadding,
                scale = scale
            )
        }

        DestinationTopBar(
            title = "Favorites",
            height = topBarHeight,
            horizontalPadding = horizontalPadding,
            rightPadding = rightPadding,
            titleBottomPadding = titleBottomPadding
        )
    }
}

@Composable
private fun FavoritesPlaceholderSection(
    title: String,
    horizontalPadding: Dp,
    scale: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding)
    ) {
        Text(
            text = title,
            color = DestinationText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 49.sp,
            letterSpacing = 0.sp
        )
        Spacer(modifier = Modifier.height(destinationDp(24f, scale)))
        Row(
            horizontalArrangement = Arrangement.spacedBy(destinationDp(50f, scale))
        ) {
            repeat(5) {
                FavoritePlaceholderCard(scale = scale)
            }
        }
    }
}

@Composable
private fun FavoritePlaceholderCard(scale: Float) {
    val shape = RoundedCornerShape(destinationDp(6.619f, scale))

    Box(
        modifier = Modifier
            .width(destinationDp(198.786f, scale))
            .height(destinationDp(138.85f, scale))
            .shadow(destinationDp(10.59f, scale), shape, clip = false)
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.08f))
            .border(destinationDp(3.971f, scale), Color.Black, shape)
    )
}

@Composable
private fun FavoritesSummaryHeaders(
    horizontalPadding: Dp,
    scale: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding)
            .padding(top = destinationDp(105f, scale))
    ) {
        FavoritesSummaryHeader(text = "Most Frequently Viewed (7 times)")
        Spacer(modifier = Modifier.height(destinationDp(96f, scale)))
        FavoritesSummaryHeader(text = "Longest Playtime (32 hrs)")
        Spacer(modifier = Modifier.height(destinationDp(134f, scale)))
        FavoritesSummaryHeader(text = "Most Used Device")
        Spacer(modifier = Modifier.height(destinationDp(51f, scale)))
        FavoritesSummaryHeader(text = "Favorite Genre")
    }
}

@Composable
private fun FavoritesSummaryHeader(text: String) {
    Text(
        text = text,
        color = DestinationText,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
}

@Composable
private fun DestinationTopBar(
    title: String,
    height: Dp,
    horizontalPadding: Dp,
    rightPadding: Dp,
    titleBottomPadding: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        CinerificChromeBackground(
            modifier = Modifier.fillMaxSize(),
            alpha = 1f,
            backgroundResId = R.drawable.chrome_top_bgfill
        )
        Text(
            text = title,
            color = DestinationText,
            fontSize = 58.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = horizontalPadding,
                    end = rightPadding,
                    bottom = titleBottomPadding
                )
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    rows: List<SettingsRowSpec>,
    scale: Float,
    onLastToggleCenterMeasured: ((Float) -> Unit)? = null
) {
    val sectionShape = RoundedCornerShape(destinationDp(SETTINGS_SECTION_BACKGROUND_RADIUS, scale))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp)
            .background(Color.Black.copy(alpha = 0.14f), sectionShape)
            .border(destinationDp(1f, scale), Color.White.copy(alpha = 0.035f), sectionShape)
            .padding(bottom = destinationDp(SETTINGS_SECTION_BOTTOM_PADDING, scale))
    ) {
        SettingsSectionHeader(title = title, scale = scale)

        rows.forEachIndexed { index, row ->
            val rowTopPadding = 34.dp +
                if (rows.getOrNull(index - 1)?.control == SettingsControl.LanguageDropdown) {
                    destinationDp(SETTINGS_LANGUAGE_MENU_HEIGHT - SETTINGS_CONTROL_HEIGHT, scale)
                } else {
                    0.dp
                }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = destinationDp(SETTINGS_SECTION_BODY_START_PADDING, scale),
                        top = rowTopPadding
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.width(destinationDp(SETTINGS_ROW_TEXT_WIDTH, scale))) {
                    Text(
                        text = row.label,
                        color = DestinationText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp
                    )
                    Text(
                        text = row.detail,
                        color = DestinationSubtle,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        letterSpacing = 0.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(destinationDp(SETTINGS_CONTROL_COLUMN_GAP, scale)))
                Box(
                    modifier = Modifier.width(destinationDp(SETTINGS_CONTROL_WIDTH, scale)),
                    contentAlignment = Alignment.Center
                ) {
                    val toggleModifier = if (
                        onLastToggleCenterMeasured != null &&
                        index == rows.lastIndex &&
                        row.control == SettingsControl.Toggle
                    ) {
                        Modifier.onGloballyPositioned { coordinates ->
                            onLastToggleCenterMeasured(
                                coordinates.positionInRoot().y + coordinates.size.height / 2f
                            )
                        }
                    } else {
                        Modifier
                    }

                    when (row.control) {
                        SettingsControl.Toggle -> SettingsAnimatedToggle(
                            label = row.label,
                            scale = scale,
                            modifier = toggleModifier
                        )
                        SettingsControl.LanguageDropdown -> SettingsLanguageDropdown(scale = scale)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SettingsSignedInColumn(
    profile: CinerificProfile,
    scale: Float,
    onSignOut: () -> Unit,
    onSignOutCenterMeasured: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarSize = SETTINGS_SIGNED_IN_AVATAR_SIZE * SETTINGS_SIGNED_IN_PROFILE_SCALE
    val avatarLeft = (SETTINGS_SIGNED_IN_WIDTH - avatarSize) / 2f
    val nameWidth = SETTINGS_SIGNED_IN_NAME_WIDTH * SETTINGS_SIGNED_IN_PROFILE_SCALE
    val nameHeight = SETTINGS_SIGNED_IN_NAME_HEIGHT * SETTINGS_SIGNED_IN_PROFILE_SCALE
    val nameLeft = (SETTINGS_SIGNED_IN_WIDTH - nameWidth) / 2f
    val nameTop = SETTINGS_SIGNED_IN_AVATAR_TOP +
        avatarSize +
        SETTINGS_SIGNED_IN_NAME_GAP * SETTINGS_SIGNED_IN_PROFILE_SCALE
    val signOutTop = nameTop + nameHeight + SETTINGS_SIGNED_IN_SIGN_OUT_GAP * SETTINGS_SIGNED_IN_PROFILE_SCALE

    Box(
        modifier = modifier
            .width(destinationDp(SETTINGS_SIGNED_IN_WIDTH, scale))
            .height(destinationDp(SETTINGS_SIGNED_IN_HEIGHT, scale))
    ) {
        Text(
            text = "Signed in as",
            color = DestinationText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(x = destinationDp(29f, scale), y = destinationDp(SETTINGS_SIGNED_IN_TITLE_TOP, scale))
                .width(destinationDp(142f, scale))
                .height(destinationDp(SETTINGS_SIGNED_IN_TITLE_HEIGHT, scale))
        )
        Image(
            painter = painterResource(profile.avatarResId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(x = destinationDp(avatarLeft, scale), y = destinationDp(SETTINGS_SIGNED_IN_AVATAR_TOP, scale))
                .size(destinationDp(avatarSize, scale))
                .clip(CircleShape)
        )
        Image(
            painter = painterResource(profile.nameResId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(x = destinationDp(nameLeft, scale), y = destinationDp(nameTop, scale))
                .width(destinationDp(nameWidth, scale))
                .height(destinationDp(nameHeight, scale))
        )
        SettingsSignOutButton(
            scale = scale,
            onSignOut = onSignOut,
            onCenterMeasured = onSignOutCenterMeasured,
            modifier = Modifier.offset(x = destinationDp(25f, scale), y = destinationDp(signOutTop, scale))
        )
    }
}

@Composable
private fun SettingsSignOutButton(
    scale: Float,
    onSignOut: () -> Unit,
    onCenterMeasured: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(destinationDp(150f, scale))
            .height(destinationDp(75f, scale))
            .onGloballyPositioned { coordinates ->
                onCenterMeasured(coordinates.positionInRoot().y + coordinates.size.height / 2f)
            }
            .clickable { onSignOut() },
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            painter = painterResource(R.drawable.settings_sign_out_base),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "Sign out",
            color = DestinationText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    scale: Float
) {
    val shape = RoundedCornerShape(destinationDp(SETTINGS_SECTION_HEADER_RADIUS, scale))

    Box(
        modifier = Modifier
            .offset(x = destinationDp(-(SETTINGS_SCREEN_HORIZONTAL_PADDING + SETTINGS_SECTION_HEADER_LEFT_BLEED), scale))
            .height(destinationDp(SETTINGS_SECTION_HEADER_HEIGHT, scale))
            .clip(shape)
            .background(Color(0xFF303030).copy(alpha = 0.96f), shape)
            .padding(
                start = destinationDp(SETTINGS_SCREEN_HORIZONTAL_PADDING + SETTINGS_SECTION_HEADER_LEFT_BLEED, scale),
                end = destinationDp(SETTINGS_SECTION_HEADER_HORIZONTAL_PADDING, scale)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            color = DestinationText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.sp
        )
    }
}

@Composable
private fun SettingsAnimatedToggle(
    label: String,
    scale: Float,
    modifier: Modifier = Modifier
) {
    var checked by rememberSaveable(label) { mutableStateOf(false) }
    val shape = RoundedCornerShape(destinationDp(SETTINGS_CONTROL_RADIUS, scale))
    val interactionSource = remember { MutableInteractionSource() }
    val trackFill by animateColorAsState(
        targetValue = Color(0xFF303030),
        animationSpec = tween(durationMillis = 140),
        label = "settings-toggle-track"
    )
    val knobInner by animateColorAsState(
        targetValue = if (checked) Color(0xFFD9D9D9) else Color(0xFF858585),
        animationSpec = tween(durationMillis = 170),
        label = "settings-toggle-knob-inner"
    )
    val knobOuter by animateColorAsState(
        targetValue = if (checked) Color(0xFF858585) else Color(0xFF303030),
        animationSpec = tween(durationMillis = 170),
        label = "settings-toggle-knob-outer"
    )
    val knobOffset by animateDpAsState(
        targetValue = destinationDp(if (checked) 82f else 14f, scale),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "settings-toggle-knob-x"
    )
    val knobSize by animateDpAsState(
        targetValue = destinationDp(if (checked) 61f else 47f, scale),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "settings-toggle-knob-size"
    )

    Box(
        modifier = modifier
            .width(destinationDp(SETTINGS_CONTROL_WIDTH, scale))
            .height(destinationDp(SETTINGS_CONTROL_HEIGHT, scale))
            .shadow(destinationDp(SETTINGS_CONTROL_SHADOW, scale), shape, clip = false)
            .clip(shape)
            .background(trackFill)
            .border(destinationDp(SETTINGS_CONTROL_BORDER_WIDTH, scale), Color.Black, shape)
            .toggleable(
                value = checked,
                onValueChange = { checked = it },
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset)
                .size(knobSize)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.radialGradient(
                        colors = listOf(knobInner, knobOuter)
                    )
                )
                .border(destinationDp(1f, scale), Color(0xFF303030), RoundedCornerShape(50))
        )
    }
}

@Composable
private fun SettingsLanguageDropdown(scale: Float) {
    var selectedLanguageIndex by rememberSaveable { mutableStateOf(0) }
    var menuVisible by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var menuClosePending by remember { mutableStateOf(false) }
    val language = SettingsLanguageOptions[selectedLanguageIndex]
    val interactionSource = remember { MutableInteractionSource() }
    val menuAnimationSpec = tween<Dp>(
        durationMillis = SETTINGS_LANGUAGE_ANIMATION_MS,
        easing = FastOutSlowInEasing
    )
    val menuHeight by animateDpAsState(
        targetValue = destinationDp(
            if (menuExpanded) SETTINGS_LANGUAGE_MENU_HEIGHT else SETTINGS_CONTROL_HEIGHT,
            scale
        ),
        animationSpec = menuAnimationSpec,
        label = "settings-language-menu-height"
    )
    val toggleHeight = destinationDp(SETTINGS_CONTROL_HEIGHT, scale)
    val menuOffsetY = (menuHeight - toggleHeight) / 2f
    val cornerRadius by animateDpAsState(
        targetValue = destinationDp(if (menuExpanded) 20f else SETTINGS_CONTROL_RADIUS, scale),
        animationSpec = menuAnimationSpec,
        label = "settings-language-corner-radius"
    )
    val shadowElevation by animateDpAsState(
        targetValue = destinationDp(if (menuExpanded) 7.5f else SETTINGS_CONTROL_SHADOW, scale),
        animationSpec = menuAnimationSpec,
        label = "settings-language-shadow"
    )
    val shape = RoundedCornerShape(cornerRadius)

    fun closeMenuAfterSelection(index: Int) {
        selectedLanguageIndex = index
        menuExpanded = false
        menuClosePending = true
    }

    LaunchedEffect(menuVisible) {
        if (menuVisible) {
            menuExpanded = false
            withFrameNanos { }
            menuExpanded = true
        } else {
            menuExpanded = false
        }
    }

    LaunchedEffect(menuClosePending) {
        if (menuClosePending) {
            delay(SETTINGS_LANGUAGE_ANIMATION_MS.toLong())
            menuVisible = false
            menuClosePending = false
        }
    }

    Box(
        modifier = Modifier
            .width(destinationDp(SETTINGS_CONTROL_WIDTH, scale))
            .height(toggleHeight),
        contentAlignment = Alignment.TopStart
    ) {
        Box(
            modifier = Modifier
                .width(destinationDp(SETTINGS_CONTROL_WIDTH, scale))
                .offset(y = menuOffsetY)
                .requiredHeight(menuHeight)
                .shadow(shadowElevation, shape, clip = false)
                .clip(shape)
                .background(Color(0xFFD9D9D9), shape)
                .border(destinationDp(SETTINGS_CONTROL_BORDER_WIDTH, scale), Color.Black, shape)
                .then(
                    if (menuVisible) {
                        Modifier
                    } else {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            menuClosePending = false
                            menuVisible = true
                        }
                    }
                ),
            contentAlignment = Alignment.TopStart
        ) {
            if (menuVisible) {
                SettingsLanguageMenuRow(
                    label = "English",
                    selected = selectedLanguageIndex == 0,
                    top = 9f,
                    scale = scale
                ) {
                    closeMenuAfterSelection(0)
                }
                SettingsLanguageMenuRow(
                    label = "Spanish",
                    selected = selectedLanguageIndex == 1,
                    top = 41f,
                    scale = scale
                ) {
                    closeMenuAfterSelection(1)
                }
                SettingsLanguageMenuRow(
                    label = "Mandarin",
                    selected = selectedLanguageIndex == 2,
                    top = 75f,
                    scale = scale
                ) {
                    closeMenuAfterSelection(2)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = destinationDp(SETTINGS_LANGUAGE_BUTTON_PADDING, scale)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = language,
                        color = Color(0xFF1F1F1F),
                        fontSize = SETTINGS_LANGUAGE_TEXT_SIZE.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = SETTINGS_LANGUAGE_LINE_HEIGHT.sp,
                        letterSpacing = 0.sp,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.weight(1f)
                    )
                    Canvas(
                        modifier = Modifier
                            .width(destinationDp(SETTINGS_LANGUAGE_ARROW_WIDTH, scale))
                            .height(destinationDp(SETTINGS_LANGUAGE_ARROW_HEIGHT, scale))
                    ) {
                        val path = Path().apply {
                            moveTo(size.width, size.height / 2f)
                            lineTo(size.width * 0.1026f, size.height * 0.0649f)
                            lineTo(size.width * 0.1026f, size.height * 0.9351f)
                            close()
                        }
                        drawPath(path, Color(0xFF1F1F1F))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsLanguageMenuRow(
    label: String,
    selected: Boolean,
    top: Float,
    scale: Float,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .offset(x = destinationDp(2f, scale), y = destinationDp(top, scale))
            .width(destinationDp(146f, scale))
            .height(destinationDp(SETTINGS_LANGUAGE_MENU_ROW_HEIGHT, scale))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(start = destinationDp(12f, scale), end = destinationDp(6f, scale)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF1F1F1F),
            fontSize = SETTINGS_LANGUAGE_TEXT_SIZE.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = SETTINGS_LANGUAGE_LINE_HEIGHT.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.weight(1f)
        )
        SettingsLanguageRadio(selected = selected, scale = scale)
    }
}

@Composable
private fun SettingsLanguageRadio(
    selected: Boolean,
    scale: Float
) {
    Canvas(
        modifier = Modifier.size(destinationDp(23f, scale))
    ) {
        val outerRadius = size.minDimension / 2f
        drawCircle(Color(0xFF303030), radius = outerRadius)
        drawCircle(Color(0xFFD9D9D9), radius = outerRadius - destinationDp(3f, scale).toPx())
        if (selected) {
            drawCircle(Color(0xFF303030), radius = destinationDp(6.5f, scale).toPx())
        }
    }
}

private data class SettingsRowSpec(
    val label: String,
    val detail: String,
    val control: SettingsControl = SettingsControl.Toggle
)

private enum class SettingsControl {
    Toggle,
    LanguageDropdown
}

private fun destinationDp(px: Float, scale: Float): Dp = (px * scale).dp

private fun destinationSectionTopPadding(index: Int, showViewportNav: Boolean): Dp {
    return if (index == 0) {
        if (showViewportNav) 48.dp else 72.dp
    } else {
        80.dp
    }
}

private data class DestinationRowSpec(
    val title: String,
    val genre: ViewportGenre,
    val programs: List<DestinationProgramSpec>
)

private data class DestinationProgramSpec(
    val title: String,
    val genre: ViewportGenre,
    @DrawableRes val drawableId: Int,
    @DrawableRes val listDrawableId: Int,
    val year: String,
    val runtime: String,
    val director: String,
    val producer: String,
    val metadataGenre: String,
    val synopsis: String,
    val rating: Int
)

private data class ProgramDetails(
    val year: String,
    val runtime: String,
    val director: String,
    val producer: String,
    val synopsis: String,
    val rating: Int,
    val genreLabel: String? = null
)

private val ProgramDetailsByTitle = mapOf(
    "One Last Breath" to ProgramDetails(
        year = "2013",
        runtime = "2h 13m",
        director = "J. Sarge",
        producer = "M. Holt",
        synopsis = "A curious woman sleuthing through unresolved family business uncovers more than she originally bargained for",
        rating = 4,
        genreLabel = "Crime"
    ),
    "Sink or Swim" to ProgramDetails(
        year = "2015",
        runtime = "1h 51m",
        director = "B. Feinholt",
        producer = "R. Anderson",
        synopsis = "Swimming takes on a whole new meaning when an unphased man is confronted with the inevitable",
        rating = 3,
        genreLabel = "Crime"
    ),
    "The Baller" to ProgramDetails(
        year = "2018",
        runtime = "1h 38m",
        director = "M. Marsh",
        producer = "S. Sebilla",
        synopsis = "Shooting hoops is all fun and games until life itself becomes the greatest challenge",
        rating = 4
    ),
    "Troublemaker" to ProgramDetails(
        year = "2009",
        runtime = "2h 5m",
        director = "A. Lockhart",
        producer = "A. Lockhart",
        synopsis = "No scolding in the world can discipline some children - at least not when it comes to little rascal Hunter Roddings",
        rating = 3
    ),
    "Ignition" to ProgramDetails(
        year = "2022",
        runtime = "2h 14m",
        director = "R. Fullstone",
        producer = "T. Tanning",
        synopsis = "Firey passion and noble truth are at odds when sheltered Mona encounters Steward, a man with a troubled past",
        rating = 3
    ),
    "Eruption" to ProgramDetails(
        year = "2024",
        runtime = "1h 34m",
        director = "M. Bennington",
        producer = "C. Lukinski",
        synopsis = "Not even a life of high-level combat training can prepare a seasoned navy seal when nature's fury breaks loose",
        rating = 5
    ),
    "If I May" to ProgramDetails(
        year = "2020",
        runtime = "1h 28m",
        director = "W. Wexler",
        producer = "B. Riley",
        synopsis = "A quiet confession changes the course of a family gathering before anyone is ready for the truth",
        rating = 3
    ),
    "The Playmate" to ProgramDetails(
        year = "2010",
        runtime = "2h 28m",
        director = "W. Stills",
        producer = "U. Bannon",
        synopsis = "A delectable but lethal young woman allures unsuspecting men into a game of never-ending hide-and-seek",
        rating = 3
    ),
    "Help" to ProgramDetails(
        year = "2025",
        runtime = "2h 0m",
        director = "K. Jewles",
        producer = "I. Kavan",
        synopsis = "Would you ignore a desperate call for aid if you were the only one around to lend a helping hand?",
        rating = 2
    )
)

private val PrototypeListTitleOrder = listOf(
    "One Last Breath",
    "Sink or Swim",
    "The Baller",
    "Troublemaker",
    "Ignition",
    "Eruption",
    "If I May",
    "The Playmate",
    "Help"
)

private val PrototypeMovieSmallTitleOrder = listOf(
    "One Last Breath",
    "Sink or Swim",
    "Troublemaker",
    "Ignition",
    "The Baller",
    "Eruption",
    "Under Attack",
    "If I May",
    "The Playmate",
    "Help"
)

private val PrototypeShowSmallTitleOrder = listOf(
    "No Trespassing",
    "Hungry Heart",
    "Enlightenment",
    "Deadbeat",
    "Playing with Fire",
    "Citric",
    "Laughing Matters",
    "Lost in Time",
    "Operation Firefly",
    "Smoke",
    "Joyriders",
    "Moments",
    "Chasing Light",
    "Breathing",
    "Falling Behind",
    "Still There",
    "Surfside",
    "Wheels",
    "Light as a Feather",
    "Incan Descent",
    "Or Not To Be",
    "Skin and Bones",
    "The Appetizer"
)

private val MovieRows = listOf(
    DestinationRowSpec(
        title = "Action Movies",
        genre = ViewportGenre.Action,
        programs = listOf(
            movie("Eruption", ViewportGenre.Action, R.drawable.home_action_01),
            movie("Under Attack", ViewportGenre.Action, R.drawable.home_action_02),
            movie("Operation Firefly", ViewportGenre.Action, R.drawable.home_action_03),
            movie("Smoke", ViewportGenre.Action, R.drawable.home_action_04),
            movie("Joyriders", ViewportGenre.Action, R.drawable.home_action_05)
        )
    ),
    DestinationRowSpec(
        title = "Comedy Movies",
        genre = ViewportGenre.Comedy,
        programs = listOf(
            movie("Citric", ViewportGenre.Comedy, R.drawable.home_comedy_01),
            movie("The Baller", ViewportGenre.Comedy, R.drawable.home_comedy_05),
            movie("Laughing Matters", ViewportGenre.Comedy, R.drawable.home_comedy_03),
            movie("Troublemaker", ViewportGenre.Comedy, R.drawable.home_comedy_02),
            movie("Lost in Time", ViewportGenre.Comedy, R.drawable.home_comedy_04)
        )
    ),
    DestinationRowSpec(
        title = "Crime Movies",
        genre = ViewportGenre.Crime,
        programs = listOf(
            movie("No Trespassing", ViewportGenre.Crime, R.drawable.home_crime_01),
            movie("One Last Breath", ViewportGenre.Crime, R.drawable.home_crime_02),
            movie("Sink or Swim", ViewportGenre.Crime, R.drawable.home_crime_03),
            movie("Hungry Heart", ViewportGenre.Crime, R.drawable.home_crime_04)
        )
    ),
    DestinationRowSpec(
        title = "Documentary Movies",
        genre = ViewportGenre.Documentary,
        programs = listOf(
            movie("Incan Descent", ViewportGenre.Documentary, R.drawable.home_documentary_01),
            movie("Or Not To Be", ViewportGenre.Documentary, R.drawable.home_documentary_02),
            movie("Surfside", ViewportGenre.Documentary, R.drawable.home_documentary_03),
            movie("Wheels", ViewportGenre.Documentary, R.drawable.home_documentary_04),
            movie("Light as a Feather", ViewportGenre.Documentary, R.drawable.home_documentary_05)
        )
    ),
    DestinationRowSpec(
        title = "Drama Movies",
        genre = ViewportGenre.Drama,
        programs = listOf(
            movie("Breathing", ViewportGenre.Drama, R.drawable.home_drama_01),
            movie("Falling Behind", ViewportGenre.Drama, R.drawable.home_drama_02),
            movie("Still There", ViewportGenre.Drama, R.drawable.home_drama_03),
            movie("If I May", ViewportGenre.Drama, R.drawable.home_drama_04),
            movie("Moments", ViewportGenre.Drama, R.drawable.home_drama_05),
            movie("Chasing Light", ViewportGenre.Drama, R.drawable.home_drama_06)
        )
    ),
    DestinationRowSpec(
        title = "Horror Movies",
        genre = ViewportGenre.Horror,
        programs = listOf(
            movie("The Playmate", ViewportGenre.Horror, R.drawable.home_horror_01),
            movie("Help", ViewportGenre.Horror, R.drawable.home_horror_02),
            movie("Skin and Bones", ViewportGenre.Horror, R.drawable.home_horror_03),
            movie("The Appetizer", ViewportGenre.Horror, R.drawable.home_horror_04)
        )
    ),
    DestinationRowSpec(
        title = "Thriller Movies",
        genre = ViewportGenre.Thriller,
        programs = listOf(
            movie("Enlightenment", ViewportGenre.Thriller, R.drawable.home_thriller_01),
            movie("Ignition", ViewportGenre.Thriller, R.drawable.home_thriller_02),
            movie("Deadbeat", ViewportGenre.Thriller, R.drawable.home_thriller_03),
            movie("Playing with Fire", ViewportGenre.Thriller, R.drawable.home_thriller_04)
        )
    )
)

private val ShowRows = listOf(
    DestinationRowSpec(
        title = "Action Shows",
        genre = ViewportGenre.Action,
        programs = listOf(
            show("Operation Firefly", ViewportGenre.Action, R.drawable.home_action_03),
            show("Smoke", ViewportGenre.Action, R.drawable.home_action_04),
            show("Under Attack", ViewportGenre.Action, R.drawable.home_action_02),
            show("Joyriders", ViewportGenre.Action, R.drawable.home_action_05)
        )
    ),
    DestinationRowSpec(
        title = "Comedy Shows",
        genre = ViewportGenre.Comedy,
        programs = listOf(
            show("Laughing Matters", ViewportGenre.Comedy, R.drawable.home_comedy_03),
            show("The Baller", ViewportGenre.Comedy, R.drawable.home_comedy_05),
            show("Troublemaker", ViewportGenre.Comedy, R.drawable.home_comedy_02),
            show("Lost in Time", ViewportGenre.Comedy, R.drawable.home_comedy_04)
        )
    ),
    DestinationRowSpec(
        title = "Crime Shows",
        genre = ViewportGenre.Crime,
        programs = listOf(
            show("No Trespassing", ViewportGenre.Crime, R.drawable.home_crime_01),
            show("Sink or Swim", ViewportGenre.Crime, R.drawable.home_crime_03),
            show("Hungry Heart", ViewportGenre.Crime, R.drawable.home_crime_04),
            show("One Last Breath", ViewportGenre.Crime, R.drawable.home_crime_02)
        )
    ),
    DestinationRowSpec(
        title = "Documentary Shows",
        genre = ViewportGenre.Documentary,
        programs = listOf(
            show("Or Not To Be", ViewportGenre.Documentary, R.drawable.home_documentary_02),
            show("Wheels", ViewportGenre.Documentary, R.drawable.home_documentary_04),
            show("Incan Descent", ViewportGenre.Documentary, R.drawable.home_documentary_01),
            show("Light as a Feather", ViewportGenre.Documentary, R.drawable.home_documentary_05)
        )
    ),
    DestinationRowSpec(
        title = "Drama Shows",
        genre = ViewportGenre.Drama,
        programs = listOf(
            show("Breathing", ViewportGenre.Drama, R.drawable.home_drama_01),
            show("Moments", ViewportGenre.Drama, R.drawable.home_drama_05),
            show("If I May", ViewportGenre.Drama, R.drawable.home_drama_04),
            show("Chasing Light", ViewportGenre.Drama, R.drawable.home_drama_06)
        )
    ),
    DestinationRowSpec(
        title = "Horror Shows",
        genre = ViewportGenre.Horror,
        programs = listOf(
            show("The Playmate", ViewportGenre.Horror, R.drawable.home_horror_01),
            show("Skin and Bones", ViewportGenre.Horror, R.drawable.home_horror_03),
            show("Help", ViewportGenre.Horror, R.drawable.home_horror_02),
            show("The Appetizer", ViewportGenre.Horror, R.drawable.home_horror_04)
        )
    ),
    DestinationRowSpec(
        title = "Thriller Shows",
        genre = ViewportGenre.Thriller,
        programs = listOf(
            show("Enlightenment", ViewportGenre.Thriller, R.drawable.home_thriller_01),
            show("Playing with Fire", ViewportGenre.Thriller, R.drawable.home_thriller_04),
            show("Deadbeat", ViewportGenre.Thriller, R.drawable.home_thriller_03),
            show("Ignition", ViewportGenre.Thriller, R.drawable.home_thriller_02)
        )
    )
)

private fun movie(
    title: String,
    genre: ViewportGenre,
    @DrawableRes drawableId: Int
): DestinationProgramSpec {
    val details = programDetails(title = title, genre = genre, isShow = false)
    return DestinationProgramSpec(
        title = title,
        genre = genre,
        drawableId = drawableId,
        listDrawableId = listPosterDrawableId(title) ?: drawableId,
        year = details.year,
        runtime = details.runtime,
        director = details.director,
        producer = details.producer,
        metadataGenre = details.genreLabel ?: genre.displayName,
        synopsis = details.synopsis,
        rating = details.rating
    )
}

private fun show(
    title: String,
    genre: ViewportGenre,
    @DrawableRes drawableId: Int
): DestinationProgramSpec {
    val details = programDetails(title = title, genre = genre, isShow = true)
    return DestinationProgramSpec(
        title = title,
        genre = genre,
        drawableId = drawableId,
        listDrawableId = listPosterDrawableId(title) ?: drawableId,
        year = details.year,
        runtime = details.runtime,
        director = details.director,
        producer = details.producer,
        metadataGenre = details.genreLabel ?: genre.displayName,
        synopsis = details.synopsis,
        rating = details.rating
    )
}

@DrawableRes
private fun listPosterDrawableId(title: String): Int? = when (title) {
    "One Last Breath" -> R.drawable.figma_card_one_last_breath
    "Sink or Swim" -> R.drawable.figma_card_sink_or_swim
    "The Baller" -> R.drawable.figma_card_the_baller
    "Troublemaker" -> R.drawable.figma_card_troublemaker
    "Ignition" -> R.drawable.figma_card_ignition
    "Eruption" -> R.drawable.figma_card_eruption
    "If I May" -> R.drawable.figma_card_if_i_may
    "The Playmate" -> R.drawable.figma_card_the_playmate
    "Help" -> R.drawable.figma_card_help
    else -> null
}

private fun programDetails(
    title: String,
    genre: ViewportGenre,
    isShow: Boolean
): ProgramDetails {
    ProgramDetailsByTitle[title]?.let { return it }

    val seed = title.fold(genre.ordinal * 17) { acc, char -> acc + char.code }
    val years = if (isShow) {
        listOf("2017", "2019", "2020", "2021", "2024")
    } else {
        listOf("2009", "2012", "2016", "2019", "2022")
    }
    val runtimes = if (isShow) {
        listOf("42m", "49m", "54m", "8 eps", "10 eps")
    } else {
        listOf("1h 28m", "1h 44m", "1h 57m", "2h 5m", "2h 18m")
    }
    val directors = listOf(
        "J. Sarge",
        "B. Feinholt",
        "M. Marsh",
        "A. Lockhart",
        "R. Fullstone",
        "M. Bennington",
        "W. Wexler",
        "K. Jewles"
    )
    val producers = listOf(
        "M. Holt",
        "R. Anderson",
        "S. Sebilla",
        "C. Lukinski",
        "B. Riley",
        "I. Kavan",
        "T. Tanning",
        "U. Bannon"
    )

    return ProgramDetails(
        year = years[seed % years.size],
        runtime = runtimes[seed % runtimes.size],
        director = directors[seed % directors.size],
        producer = producers[(seed / 3) % producers.size],
        synopsis = generatedSynopsis(title = title, genre = genre, isShow = isShow),
        rating = 2 + seed % 4
    )
}

private fun generatedSynopsis(
    title: String,
    genre: ViewportGenre,
    isShow: Boolean
): String {
    val format = if (isShow) "series" else "feature"
    return "This ${genre.displayName.lowercase()} $format follows $title through an escalating chain of choices, secrets, and second chances"
}

private fun prototypeListPriority(title: String): Int {
    val index = PrototypeListTitleOrder.indexOf(title)
    return if (index == -1) Int.MAX_VALUE else index
}

private fun listPrograms(programs: List<DestinationProgramSpec>): List<DestinationProgramSpec> {
    return programs
        .withIndex()
        .sortedWith(compareBy({ prototypeListPriority(it.value.title) }, { it.index }))
        .map { it.value }
}

private fun prototypeSmallPriority(destinationTitle: String, programTitle: String): Int {
    val order = if (destinationTitle == "Shows") {
        PrototypeShowSmallTitleOrder
    } else {
        PrototypeMovieSmallTitleOrder
    }
    val index = order.indexOf(programTitle)
    return if (index == -1) Int.MAX_VALUE else index
}

private fun smallCollagePrograms(
    destinationTitle: String,
    programs: List<DestinationProgramSpec>
): List<DestinationProgramSpec> {
    return programs
        .withIndex()
        .sortedWith(compareBy({ prototypeSmallPriority(destinationTitle, it.value.title) }, { it.index }))
        .map { it.value }
}
