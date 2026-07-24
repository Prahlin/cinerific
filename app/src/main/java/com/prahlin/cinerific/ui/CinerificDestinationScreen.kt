package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.rounded.ChevronRight
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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
private const val DETAIL_FAVORITE_X = 994f
private const val DETAIL_FAVORITE_Y = 30f
private const val DETAIL_FAVORITE_WIDTH = 61f
private const val DETAIL_FAVORITE_HEIGHT = 59f
private const val DETAIL_TRANSPORT_X = 423f
private const val DETAIL_TRANSPORT_Y = 367f
private const val DETAIL_TRANSPORT_GAP = 50f
private const val DETAIL_SIDE_CONTROL_SIZE = 74f
private const val DETAIL_PLAY_CONTROL_SIZE = 100f
private const val FAVORITE_BURST_PADDING = 22f
private const val FAVORITE_BURST_STROKE = 2.4f
private const val FAVORITE_BURST_DURATION_MS = 240
private const val DETAIL_LOADING_SPINNER_WIDTH = 190f
private const val SINK_OR_SWIM_TITLE = "Sink or Swim"
private const val DETAIL_CARD_REVEAL_DELAY_MS = 2400L
private const val DETAIL_CARD_REVEAL_ANIMATION_MS = 900
internal const val CINERIFIC_FAVORITES_CAPACITY = 5
private const val FAVORITES_PLACEHOLDER_COUNT = CINERIFIC_FAVORITES_CAPACITY
private const val FAVORITES_CARD_WIDTH = 198.786f
private const val FAVORITES_CARD_HEIGHT = 138.85f
private const val FAVORITES_CARD_RADIUS = 6.619f
private const val FAVORITES_CARD_BORDER = 3.971f
private const val FAVORITES_CARD_SHADOW = 10.59f
private const val FAVORITES_REMOVE_BUTTON_SIZE = 33.131f
private const val FAVORITES_REMOVE_BUTTON_MARGIN = 6.619f

private val DestinationTop = Color(0xFF080007)
private val DestinationMid = Color(0xFF23001F)
private val DestinationBottom = Color(0xFF060004)
private val DestinationText = Color(0xFFE7E7E7)
private val DestinationSubtle = Color(0xFFBDBDBD)
private val FavoriteBurstYellow = Color(0xFFFFD43B)
private val SettingsLanguageOptions = listOf(
    CinerificLanguage.English to R.string.language_english,
    CinerificLanguage.Spanish to R.string.language_spanish,
    CinerificLanguage.Mandarin to R.string.language_mandarin
)

@Composable
internal fun CinerificDestinationScreen(
    destination: CinerificDestination,
    signedInProfile: CinerificProfile = CinerificProfile.Guest,
    selectedLanguage: CinerificLanguage = CinerificLanguage.English,
    onLanguageSelected: (CinerificLanguage) -> Unit = {},
    autoLogoutEnabled: Boolean = false,
    onAutoLogoutEnabledChange: (Boolean) -> Unit = {},
    onSignOut: () -> Unit = {},
    favoriteProgramTitles: List<String> = emptyList(),
    onFavoriteToggled: (String) -> Unit = {},
    onProgramSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (destination) {
        CinerificDestination.Movies -> CinerificCatalogScreen(
            titleResId = R.string.destination_movies,
            descriptionResId = R.string.movies_description,
            rows = MovieRows,
            showViewportNav = true,
            isShows = false,
            onProgramSelected = onProgramSelected,
            modifier = modifier
        )
        CinerificDestination.Shows -> CinerificCatalogScreen(
            titleResId = R.string.destination_shows,
            descriptionResId = R.string.shows_description,
            rows = ShowRows,
            showViewportNav = true,
            isShows = true,
            onProgramSelected = onProgramSelected,
            modifier = modifier
        )
        CinerificDestination.Favorites -> CinerificFavoritesScreen(
            favoriteProgramTitles = favoriteProgramTitles,
            onFavoriteToggled = onFavoriteToggled,
            onProgramSelected = onProgramSelected,
            modifier = modifier
        )
        CinerificDestination.Settings -> CinerificSettingsScreen(
            signedInProfile = signedInProfile,
            selectedLanguage = selectedLanguage,
            onLanguageSelected = onLanguageSelected,
            autoLogoutEnabled = autoLogoutEnabled,
            onAutoLogoutEnabledChange = onAutoLogoutEnabledChange,
            onSignOut = onSignOut,
            modifier = modifier
        )
        CinerificDestination.Home -> CinerificHomeScreen(
            onProgramSelected = onProgramSelected,
            modifier = modifier
        )
        CinerificDestination.ProgramDetails -> CinerificProgramDetailsScreen(
            programTitle = SINK_OR_SWIM_TITLE,
            favoriteProgramTitles = favoriteProgramTitles,
            onFavoriteToggled = onFavoriteToggled,
            onProgramSelected = onProgramSelected,
            modifier = modifier
        )
    }
}

@Composable
private fun CinerificCatalogScreen(
    @StringRes titleResId: Int,
    rows: List<DestinationRowSpec>,
    @StringRes descriptionResId: Int,
    showViewportNav: Boolean,
    isShows: Boolean,
    onProgramSelected: (String) -> Unit,
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
        var selectedGenre by remember(titleResId) { mutableStateOf(ViewportGenre.All) }
        var selectedMode by remember(titleResId) { mutableStateOf(ViewportMode.CollageLarge) }
        val scrollState = rememberScrollState()
        val title = stringResource(titleResId)
        val description = stringResource(descriptionResId)
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
                            title = stringResource(row.titleResId),
                            programs = row.programs,
                            horizontalPadding = horizontalPadding,
                            rightPadding = rightPadding,
                            cardWidth = cardWidth,
                            cardHeight = cardHeight,
                            cardGap = cardGap,
                            columnCount = contentColumnCount,
                            onProgramSelected = onProgramSelected,
                            topPadding = destinationSectionTopPadding(index = index, showViewportNav = showViewportNav)
                        )
                    }
                }
                ViewportMode.CollageSmall -> {
                    visibleRows.forEachIndexed { index, row ->
                        DestinationProgramSmallCollage(
                            title = stringResource(row.titleResId),
                            programs = smallCollagePrograms(isShows = isShows, programs = row.programs),
                            horizontalPadding = horizontalPadding,
                            rightPadding = rightPadding,
                            scale = scale,
                            onProgramSelected = onProgramSelected,
                            topPadding = destinationSectionTopPadding(index = index, showViewportNav = showViewportNav)
                        )
                    }
                }
                ViewportMode.List -> {
                    visibleRows.forEachIndexed { index, row ->
                        DestinationProgramList(
                            title = stringResource(row.titleResId),
                            programs = listPrograms(row.programs),
                            horizontalPadding = horizontalPadding,
                            rightPadding = rightPadding,
                            scale = scale,
                            onProgramSelected = onProgramSelected,
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
    onProgramSelected: (String) -> Unit,
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
                            program = program,
                            width = cardWidth,
                            height = cardHeight,
                            onProgramSelected = onProgramSelected
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
    onProgramSelected: (String) -> Unit,
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
                            scale = scale,
                            onProgramSelected = onProgramSelected
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
    scale: Float,
    onProgramSelected: (String) -> Unit
) {
    val shape = RoundedCornerShape(destinationDp(30f, scale))
    val title = stringResource(program.titleResId)

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
                .clickable(enabled = program.hasDetailHero) {
                    onProgramSelected(program.title)
                }
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
            text = title.uppercase(),
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
    onProgramSelected: (String) -> Unit,
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
                    scale = scale,
                    onProgramSelected = onProgramSelected
                )
            }
        }
    }
}

@Composable
private fun DestinationProgramListItem(
    program: DestinationProgramSpec,
    scale: Float,
    onProgramSelected: (String) -> Unit
) {
    val imageShape = RoundedCornerShape(destinationDp(15f, scale))
    val title = stringResource(program.titleResId)
    val genre = stringResource(program.metadataGenreResId)
    val synopsis = program.synopsisResId?.let { stringResource(it) } ?: stringResource(
        R.string.program_generated_synopsis,
        stringResource(program.genre.displayNameResId).lowercase(),
        stringResource(if (program.isShow) R.string.program_format_series else R.string.program_format_feature),
        title
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(destinationDp(237f, scale))
            .clickable(enabled = program.hasDetailHero) {
                onProgramSelected(program.title)
            },
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
                        append(title.uppercase())
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
                    text = synopsis,
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
                    bottom = genre,
                    width = destinationDp(71f, scale),
                    scale = scale
                )
                DestinationListMetaColumn(
                    top = stringResource(R.string.program_meta_director, program.director),
                    bottom = stringResource(R.string.program_meta_producer, program.producer),
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
    program: DestinationProgramSpec,
    width: Dp,
    height: Dp,
    onProgramSelected: (String) -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(12.dp, shape, clip = false)
            .clip(shape)
            .clickable(enabled = program.hasDetailHero) {
                onProgramSelected(program.title)
            }
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(program.drawableId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
internal fun CinerificProgramDetailsScreen(
    programTitle: String,
    favoriteProgramTitles: List<String> = emptyList(),
    onFavoriteToggled: (String) -> Unit = {},
    onProgramSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DestinationBottom)
    ) {
        val scale = maxWidth.value / DESTINATION_FRAME_WIDTH
        val density = LocalDensity.current
        val bottomSystemPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        val heroHeight = maxHeight - bottomSystemPadding
        val program = detailProgramSpec(programTitle) ?: detailProgramSpec(SINK_OR_SWIM_TITLE)!!
        val title = stringResource(program.titleResId)
        val details = programDetails(
            title = program.title,
            genre = program.genre,
            isShow = program.isShow
        )
        val genre = stringResource((details.genreLabel ?: program.genre).displayNameResId)
        val synopsis = details.synopsisResId?.let { stringResource(it) }.orEmpty()
        val heroDrawableId = detailHeroDrawableId(program.title) ?: program.drawableId
        val playbackSessionController = LocalCinerificPlaybackSessionController.current
        val previousProgramTitle = remember(program.title) {
            adjacentDetailProgramTitle(program.title, offset = -1)
        }
        val nextProgramTitle = remember(program.title) {
            adjacentDetailProgramTitle(program.title, offset = 1)
        }
        val isFavorited = program.title in favoriteProgramTitles
        val cardReveal = remember(program.title) { Animatable(0f) }

        LaunchedEffect(program.title) {
            cardReveal.snapTo(0f)
            delay(DETAIL_CARD_REVEAL_DELAY_MS)
            cardReveal.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = DETAIL_CARD_REVEAL_ANIMATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(DestinationBottom)
        ) {
            ProgramDetailRevealImage(
                revealProgress = cardReveal.value,
                height = heroHeight,
                heroDrawableId = heroDrawableId,
                contentDescription = title,
                scale = scale,
                isFavorited = isFavorited,
                onFavoriteToggled = { onFavoriteToggled(program.title) },
                onPrevious = { onProgramSelected(previousProgramTitle) },
                onPlay = {
                    if (playbackSessionController.isUserInitiatedPlaybackActive) {
                        playbackSessionController.onUserInitiatedPlaybackFinished()
                    } else {
                        playbackSessionController.onActionablePlayTapped()
                    }
                },
                onNext = { onProgramSelected(nextProgramTitle) }
            )
            SinkOrSwimInfoPanel(
                title = title,
                year = details.year,
                runtime = details.runtime,
                genre = genre,
                synopsis = synopsis,
                director = details.director,
                producer = details.producer,
                rating = details.rating,
                scale = scale
            )
            SinkOrSwimLibraryRows(
                scale = scale,
                onProgramSelected = onProgramSelected
            )
            Spacer(modifier = Modifier.height(bottomSystemPadding + destinationDp(72f, scale)))
        }
    }
}

@Composable
private fun ProgramDetailRevealImage(
    revealProgress: Float,
    height: Dp,
    @DrawableRes heroDrawableId: Int,
    contentDescription: String,
    scale: Float,
    isFavorited: Boolean,
    onFavoriteToggled: () -> Unit,
    onPrevious: () -> Unit,
    onPlay: () -> Unit,
    onNext: () -> Unit
) {
    val heroFullyVisible = revealProgress >= 0.999f
    val spinnerHeight = DETAIL_LOADING_SPINNER_WIDTH *
        CINERIFIC_LOADING_SPINNER_CANVAS_HEIGHT /
        CINERIFIC_LOADING_SPINNER_CANVAS_WIDTH

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(heroDrawableId),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 0.46f + revealProgress * 0.54f
                },
            contentScale = ContentScale.FillBounds
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6D6D6D).copy(alpha = (1f - revealProgress) * 0.58f))
        )
        if (heroFullyVisible) {
            HeroFavoriteToggleButton(
                isFavorited = isFavorited,
                onFavoriteToggled = onFavoriteToggled,
                scale = scale
            )
            HeroTransportControls(
                scale = scale,
                onPrevious = onPrevious,
                onPlay = onPlay,
                onNext = onNext
            )
        } else {
            CinerificLoadingSpinner(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(destinationDp(DETAIL_LOADING_SPINNER_WIDTH, scale))
                    .height(destinationDp(spinnerHeight, scale))
            )
        }
    }
}

@Composable
private fun HeroFavoriteToggleButton(
    isFavorited: Boolean,
    onFavoriteToggled: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val favoriteBounceScale = remember { Animatable(1f) }
    val favoriteBurstProgress = remember { Animatable(1f) }
    var previousIsFavorited by remember { mutableStateOf(isFavorited) }

    LaunchedEffect(isFavorited) {
        if (isFavorited && !previousIsFavorited) {
            launch {
                favoriteBurstProgress.snapTo(0f)
                favoriteBurstProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = FAVORITE_BURST_DURATION_MS,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            favoriteBounceScale.snapTo(1.08f)
            favoriteBounceScale.animateTo(
                targetValue = 1.16f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
            favoriteBounceScale.animateTo(
                targetValue = 0.94f,
                animationSpec = tween(durationMillis = 45)
            )
            favoriteBounceScale.animateTo(
                targetValue = 1.26f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
            favoriteBounceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else if (!isFavorited) {
            favoriteBounceScale.snapTo(1f)
            favoriteBurstProgress.snapTo(1f)
        }
        previousIsFavorited = isFavorited
    }

    val controlWidth = destinationDp(DETAIL_FAVORITE_WIDTH, scale)
    val controlHeight = destinationDp(DETAIL_FAVORITE_HEIGHT, scale)
    val burstPadding = destinationDp(FAVORITE_BURST_PADDING, scale)

    Box(
        modifier = modifier
            .offset(
                x = destinationDp(DETAIL_FAVORITE_X - FAVORITE_BURST_PADDING, scale),
                y = destinationDp(DETAIL_FAVORITE_Y - FAVORITE_BURST_PADDING, scale)
            )
            .width(controlWidth + burstPadding + burstPadding)
            .height(controlHeight + burstPadding + burstPadding)
            .graphicsLayer {
                scaleX = favoriteBounceScale.value
                scaleY = favoriteBounceScale.value
            }
    ) {
        HeroImageControlButton(
            drawableId = if (isFavorited) {
                R.drawable.hero_control_favorite_active
            } else {
                R.drawable.hero_control_favorite
            },
            contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
            width = controlWidth,
            height = controlHeight,
            role = Role.Checkbox,
            onClick = onFavoriteToggled,
            modifier = Modifier.offset(x = burstPadding, y = burstPadding)
        )
        FavoriteStarBurstLines(
            progress = favoriteBurstProgress.value,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun FavoriteStarBurstLines(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val p = progress.coerceIn(0f, 1f)
    if (p >= 0.999f) return

    Canvas(modifier = modifier) {
        val controlWidth = size.width *
            DETAIL_FAVORITE_WIDTH /
            (DETAIL_FAVORITE_WIDTH + FAVORITE_BURST_PADDING * 2f)
        val controlHeight = size.height *
            DETAIL_FAVORITE_HEIGHT /
            (DETAIL_FAVORITE_HEIGHT + FAVORITE_BURST_PADDING * 2f)
        val figmaUnit = min(controlWidth, controlHeight) /
            min(DETAIL_FAVORITE_WIDTH, DETAIL_FAVORITE_HEIGHT)
        val center = Offset(size.width / 2f, size.height / 2f)
        val starPointRadius = min(controlWidth, controlHeight) * 0.38f
        val startRadius = starPointRadius + (2f + p * 10f) * figmaUnit
        val lineLength = (7f + p * 7f) * figmaUnit
        val fade = (1f - p) * (1f - p)
        val color = FavoriteBurstYellow.copy(alpha = fade.coerceIn(0f, 1f))
        val strokeWidth = FAVORITE_BURST_STROKE * figmaUnit

        repeat(5) { pointIndex ->
            val pointAngle = -Math.PI / 2.0 + pointIndex * Math.PI * 2.0 / 5.0
            listOf(-0.22, 0.22).forEach { sideOffset ->
                val angle = pointAngle + sideOffset
                val direction = Offset(
                    x = cos(angle).toFloat(),
                    y = sin(angle).toFloat()
                )
                val start = center + direction * startRadius
                val end = center + direction * (startRadius + lineLength)
                drawLine(
                    color = color,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun HeroTransportControls(
    scale: Float,
    onPrevious: () -> Unit,
    onPlay: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .offset(
                x = destinationDp(DETAIL_TRANSPORT_X, scale),
                y = destinationDp(DETAIL_TRANSPORT_Y, scale)
            )
            .width(
                destinationDp(
                    DETAIL_SIDE_CONTROL_SIZE +
                        DETAIL_TRANSPORT_GAP +
                        DETAIL_PLAY_CONTROL_SIZE +
                        DETAIL_TRANSPORT_GAP +
                        DETAIL_SIDE_CONTROL_SIZE,
                    scale
                )
            )
            .height(destinationDp(DETAIL_PLAY_CONTROL_SIZE, scale)),
        horizontalArrangement = Arrangement.spacedBy(destinationDp(DETAIL_TRANSPORT_GAP, scale)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeroImageControlButton(
            drawableId = R.drawable.hero_control_prev,
            contentDescription = "Previous title",
            width = destinationDp(DETAIL_SIDE_CONTROL_SIZE, scale),
            height = destinationDp(DETAIL_SIDE_CONTROL_SIZE, scale),
            onClick = onPrevious
        )
        HeroImageControlButton(
            drawableId = R.drawable.hero_control_play,
            contentDescription = "Play title",
            width = destinationDp(DETAIL_PLAY_CONTROL_SIZE, scale),
            height = destinationDp(DETAIL_PLAY_CONTROL_SIZE, scale),
            onClick = onPlay
        )
        HeroImageControlButton(
            drawableId = R.drawable.hero_control_next,
            contentDescription = "Next title",
            width = destinationDp(DETAIL_SIDE_CONTROL_SIZE, scale),
            height = destinationDp(DETAIL_SIDE_CONTROL_SIZE, scale),
            onClick = onNext
        )
    }
}

@Composable
private fun HeroImageControlButton(
    @DrawableRes drawableId: Int,
    contentDescription: String,
    width: Dp,
    height: Dp,
    role: Role = Role.Button,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "hero-control-press-scale"
    )

    Image(
        painter = painterResource(drawableId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .width(width)
            .height(height)
            .graphicsLayer {
                scaleX = pressedScale
                scaleY = pressedScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = role,
                onClick = onClick
            )
    )
}

@Composable
private fun SinkOrSwimLibraryRows(
    scale: Float,
    onProgramSelected: (String) -> Unit
) {
    val horizontalPadding = destinationDp(50f, scale)
    val cardWidth = destinationDp(350f, scale) * DESTINATION_CARD_SCALE
    val cardHeight = cardWidth / DESTINATION_CARD_ASPECT
    val cardGap = destinationDp(50f, scale)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DestinationMid, DestinationBottom)
                )
            )
            .padding(top = destinationDp(86f, scale))
    ) {
        SinkOrSwimLibraryRowSpecs.forEachIndexed { index, row ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(destinationDp(80f, scale)))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(row.titleResId),
                    color = DestinationText,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = DestinationText,
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
                row.programs.forEach { program ->
                    SinkOrSwimLibraryCard(
                        program = program,
                        width = cardWidth,
                        height = cardHeight,
                        onProgramSelected = onProgramSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun SinkOrSwimLibraryCard(
    program: DestinationProgramSpec,
    width: Dp,
    height: Dp,
    onProgramSelected: (String) -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(12.dp, shape, clip = false)
            .clip(shape)
            .clickable(enabled = program.hasDetailHero) {
                onProgramSelected(program.title)
            }
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(program.drawableId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
private fun SinkOrSwimInfoPanel(
    title: String,
    year: String,
    runtime: String,
    genre: String,
    synopsis: String,
    director: String,
    producer: String,
    rating: Int,
    scale: Float
) {
    val panelHorizontalPadding = destinationDp(165f, scale)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF444444))
            .padding(
                start = panelHorizontalPadding,
                top = destinationDp(48f, scale),
                end = panelHorizontalPadding,
                bottom = destinationDp(56f, scale)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                    append(title.uppercase())
                }
                append(" ")
                withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                    append("($year)")
                }
            },
            color = DestinationText,
            fontSize = 30.sp,
            lineHeight = 38.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(destinationDp(28f, scale)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = synopsis,
                color = DestinationText.copy(alpha = 0.82f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
                modifier = Modifier.width(destinationDp(245f, scale))
            )

            Column(
                modifier = Modifier.width(destinationDp(140f, scale)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(destinationDp(18f, scale))
            ) {
                SinkOrSwimPanelText(text = runtime, textAlign = TextAlign.Center)
                SinkOrSwimPanelText(text = genre, textAlign = TextAlign.Center)
            }

            Column(
                modifier = Modifier.width(destinationDp(245f, scale)),
                verticalArrangement = Arrangement.spacedBy(destinationDp(18f, scale))
            ) {
                SinkOrSwimPanelText(text = stringResource(R.string.program_meta_director, director))
                SinkOrSwimPanelText(text = stringResource(R.string.program_meta_producer, producer))
            }
        }

        Spacer(modifier = Modifier.height(destinationDp(22f, scale)))
        SinkOrSwimRatingStars(rating = rating, scale = scale)
    }
}

@Composable
private fun SinkOrSwimPanelText(
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        color = DestinationText.copy(alpha = 0.9f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
        textAlign = textAlign,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun SinkOrSwimRatingStars(
    rating: Int,
    scale: Float
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(destinationDp(8f, scale)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFC91B) else Color(0xFFC9C4CC),
                modifier = Modifier.size(destinationDp(16f, scale))
            )
        }
    }
}

@Composable
private fun DestinationDetailMetaText(
    top: String,
    bottom: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = top,
            color = DestinationText.copy(alpha = 0.9f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        )
        Text(
            text = bottom,
            color = DestinationText.copy(alpha = 0.72f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        )
    }
}

@Composable
private fun CinerificSettingsScreen(
    signedInProfile: CinerificProfile,
    selectedLanguage: CinerificLanguage,
    onLanguageSelected: (CinerificLanguage) -> Unit,
    autoLogoutEnabled: Boolean,
    onAutoLogoutEnabledChange: (Boolean) -> Unit,
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
                titleResId = R.string.settings_accessibility,
                rows = listOf(
                    SettingsRowSpec(
                        labelResId = R.string.settings_language,
                        detailResId = R.string.settings_language_detail,
                        control = SettingsControl.LanguageDropdown
                    ),
                    SettingsRowSpec(
                        labelResId = R.string.settings_simplify_ui,
                        detailResId = R.string.settings_simplify_ui_detail
                    ),
                    SettingsRowSpec(
                        labelResId = R.string.settings_dark_mode,
                        detailResId = R.string.settings_dark_mode_detail
                    )
                ),
                scale = scale,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
            SettingsSection(
                titleResId = R.string.settings_playback,
                rows = listOf(
                    SettingsRowSpec(
                        labelResId = R.string.settings_autoplay,
                        detailResId = R.string.settings_autoplay_detail
                    ),
                    SettingsRowSpec(
                        labelResId = R.string.settings_background_play,
                        detailResId = R.string.settings_background_play_detail
                    ),
                    SettingsRowSpec(
                        labelResId = R.string.settings_wifi_downloads_only,
                        detailResId = R.string.settings_wifi_downloads_only_detail
                    )
                ),
                scale = scale,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
            SettingsSection(
                titleResId = R.string.settings_billing_payment,
                rows = listOf(
                    SettingsRowSpec(
                        labelResId = R.string.settings_bill_automatically,
                        detailResId = R.string.settings_bill_automatically_detail
                    ),
                    SettingsRowSpec(
                        labelResId = R.string.settings_notifications,
                        detailResId = R.string.settings_notifications_detail
                    )
                ),
                scale = scale,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
            SettingsSection(
                titleResId = R.string.settings_security,
                rows = listOf(
                    SettingsRowSpec(
                        labelResId = R.string.settings_auto_log_out,
                        detailResId = R.string.settings_auto_log_out_detail,
                        checked = autoLogoutEnabled,
                        onCheckedChange = onAutoLogoutEnabledChange
                    ),
                    SettingsRowSpec(
                        labelResId = R.string.settings_two_factor_authentication,
                        detailResId = R.string.settings_two_factor_authentication_detail
                    )
                ),
                scale = scale,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected,
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
            title = stringResource(R.string.destination_settings),
            height = topBarHeight,
            horizontalPadding = horizontalPadding,
            rightPadding = rightPadding,
            titleBottomPadding = titleBottomPadding
        )
    }
}

@Composable
private fun CinerificFavoritesScreen(
    favoriteProgramTitles: List<String>,
    onFavoriteToggled: (String) -> Unit,
    onProgramSelected: (String) -> Unit,
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
        val bottomSystemPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        val favoritePrograms = remember(favoriteProgramTitles) {
            favoriteProgramSpecs(favoriteProgramTitles)
        }
        val favoriteMovies = favoritePrograms
            .filterNot { it.isShow }
            .take(FAVORITES_PLACEHOLDER_COUNT)
        val favoriteShows = favoritePrograms
            .filter { it.isShow }
            .take(FAVORITES_PLACEHOLDER_COUNT)

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
                title = stringResource(R.string.destination_movies),
                programs = favoriteMovies,
                onFavoriteToggled = onFavoriteToggled,
                onProgramSelected = onProgramSelected,
                horizontalPadding = horizontalPadding,
                scale = scale
            )
            Spacer(modifier = Modifier.height(destinationDp(82f, scale)))
            FavoritesPlaceholderSection(
                title = stringResource(R.string.destination_shows),
                programs = favoriteShows,
                onFavoriteToggled = onFavoriteToggled,
                onProgramSelected = onProgramSelected,
                horizontalPadding = horizontalPadding,
                scale = scale
            )
            FavoritesSummaryHeaders(
                horizontalPadding = horizontalPadding,
                scale = scale
            )
        }

        DestinationTopBar(
            title = stringResource(R.string.destination_favorites),
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
    programs: List<DestinationProgramSpec>,
    onFavoriteToggled: (String) -> Unit,
    onProgramSelected: (String) -> Unit,
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
            repeat(FAVORITES_PLACEHOLDER_COUNT) { index ->
                FavoritePlaceholderCard(
                    program = programs.getOrNull(index),
                    scale = scale,
                    onFavoriteToggled = onFavoriteToggled,
                    onProgramSelected = onProgramSelected
                )
            }
        }
    }
}

@Composable
private fun FavoritePlaceholderCard(
    program: DestinationProgramSpec?,
    scale: Float,
    onFavoriteToggled: (String) -> Unit,
    onProgramSelected: (String) -> Unit
) {
    val shape = RoundedCornerShape(destinationDp(FAVORITES_CARD_RADIUS, scale))
    val title = program?.let { stringResource(it.titleResId) }
    val cardModifier = Modifier
        .width(destinationDp(FAVORITES_CARD_WIDTH, scale))
        .height(destinationDp(FAVORITES_CARD_HEIGHT, scale))
        .shadow(destinationDp(FAVORITES_CARD_SHADOW, scale), shape, clip = false)
        .clip(shape)
        .background(Color.Black.copy(alpha = 0.08f))
        .border(destinationDp(FAVORITES_CARD_BORDER, scale), Color.Black, shape)
    val actionableModifier = if (program?.hasDetailHero == true) {
        cardModifier.clickable {
            onProgramSelected(program.title)
        }
    } else {
        cardModifier
    }

    Box(
        modifier = actionableModifier
    ) {
        if (program != null) {
            Image(
                painter = painterResource(program.drawableId),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            FavoriteRemoveButton(
                title = title.orEmpty(),
                scale = scale,
                onClick = { onFavoriteToggled(program.title) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = -destinationDp(FAVORITES_REMOVE_BUTTON_MARGIN, scale),
                        y = destinationDp(FAVORITES_REMOVE_BUTTON_MARGIN, scale)
                    )
            )
        }
    }
}

@Composable
private fun FavoriteRemoveButton(
    title: String,
    scale: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "favorite-remove-press-scale"
    )

    Image(
        painter = painterResource(R.drawable.favorite_remove_button),
        contentDescription = "Remove $title from favorites",
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .size(destinationDp(FAVORITES_REMOVE_BUTTON_SIZE, scale))
            .graphicsLayer {
                scaleX = pressedScale
                scaleY = pressedScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
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
        FavoritesSummaryHeader(text = stringResource(R.string.favorites_most_frequently_viewed))
        Spacer(modifier = Modifier.height(destinationDp(96f, scale)))
        FavoritesSummaryHeader(text = stringResource(R.string.favorites_longest_playtime))
        Spacer(modifier = Modifier.height(destinationDp(134f, scale)))
        FavoritesSummaryHeader(text = stringResource(R.string.favorites_most_used_device))
        Spacer(modifier = Modifier.height(destinationDp(51f, scale)))
        FavoritesSummaryHeader(text = stringResource(R.string.favorites_favorite_genre))
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
    @StringRes titleResId: Int,
    rows: List<SettingsRowSpec>,
    scale: Float,
    selectedLanguage: CinerificLanguage,
    onLanguageSelected: (CinerificLanguage) -> Unit,
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
        SettingsSectionHeader(title = stringResource(titleResId), scale = scale)

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
                        text = stringResource(row.labelResId),
                        color = DestinationText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp
                    )
                    Text(
                        text = stringResource(row.detailResId),
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
                            stateKey = row.labelResId,
                            scale = scale,
                            checked = row.checked,
                            onCheckedChange = row.onCheckedChange,
                            modifier = toggleModifier
                        )
                        SettingsControl.LanguageDropdown -> SettingsLanguageDropdown(
                            selectedLanguage = selectedLanguage,
                            onLanguageSelected = onLanguageSelected,
                            scale = scale
                        )
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
    val titleFontSize = if (LocalCinerificLanguage.current == CinerificLanguage.Spanish) 20.sp else 24.sp
    val titleLineHeight = if (LocalCinerificLanguage.current == CinerificLanguage.Spanish) 28.sp else 36.sp

    Box(
        modifier = modifier
            .width(destinationDp(SETTINGS_SIGNED_IN_WIDTH, scale))
            .height(destinationDp(SETTINGS_SIGNED_IN_HEIGHT, scale))
    ) {
        Text(
            text = stringResource(R.string.settings_signed_in_as),
            color = DestinationText,
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold,
            lineHeight = titleLineHeight,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .offset(x = 0.dp, y = destinationDp(SETTINGS_SIGNED_IN_TITLE_TOP, scale))
                .width(destinationDp(SETTINGS_SIGNED_IN_WIDTH, scale))
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
            text = stringResource(R.string.settings_sign_out),
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
    @StringRes stateKey: Int,
    scale: Float,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var localChecked by rememberSaveable(stateKey) { mutableStateOf(false) }
    val isChecked = checked ?: localChecked
    val shape = RoundedCornerShape(destinationDp(SETTINGS_CONTROL_RADIUS, scale))
    val interactionSource = remember { MutableInteractionSource() }
    val trackFill by animateColorAsState(
        targetValue = Color(0xFF303030),
        animationSpec = tween(durationMillis = 140),
        label = "settings-toggle-track"
    )
    val knobInner by animateColorAsState(
        targetValue = if (isChecked) Color(0xFFD9D9D9) else Color(0xFF858585),
        animationSpec = tween(durationMillis = 170),
        label = "settings-toggle-knob-inner"
    )
    val knobOuter by animateColorAsState(
        targetValue = if (isChecked) Color(0xFF858585) else Color(0xFF303030),
        animationSpec = tween(durationMillis = 170),
        label = "settings-toggle-knob-outer"
    )
    val knobOffset by animateDpAsState(
        targetValue = destinationDp(if (isChecked) 82f else 14f, scale),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "settings-toggle-knob-x"
    )
    val knobSize by animateDpAsState(
        targetValue = destinationDp(if (isChecked) 61f else 47f, scale),
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
                value = isChecked,
                onValueChange = { nextChecked ->
                    if (checked == null) {
                        localChecked = nextChecked
                    }
                    onCheckedChange?.invoke(nextChecked)
                },
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
private fun SettingsLanguageDropdown(
    selectedLanguage: CinerificLanguage,
    onLanguageSelected: (CinerificLanguage) -> Unit,
    scale: Float
) {
    var menuVisible by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var menuClosePending by remember { mutableStateOf(false) }
    val selectedLabelResId = SettingsLanguageOptions.firstOrNull { it.first == selectedLanguage }?.second
        ?: R.string.language_english
    val language = stringResource(selectedLabelResId)
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

    fun closeMenuAfterSelection(language: CinerificLanguage) {
        onLanguageSelected(language)
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
                SettingsLanguageOptions.forEachIndexed { index, option ->
                    SettingsLanguageMenuRow(
                        label = stringResource(option.second),
                        selected = selectedLanguage == option.first,
                        top = when (index) {
                            0 -> 9f
                            1 -> 41f
                            else -> 75f
                        },
                        scale = scale
                    ) {
                        closeMenuAfterSelection(option.first)
                    }
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
    @StringRes val labelResId: Int,
    @StringRes val detailResId: Int,
    val control: SettingsControl = SettingsControl.Toggle,
    val checked: Boolean? = null,
    val onCheckedChange: ((Boolean) -> Unit)? = null
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
    @StringRes val titleResId: Int,
    val genre: ViewportGenre,
    val programs: List<DestinationProgramSpec>
)

private data class SinkOrSwimLibraryRowSpec(
    @StringRes val titleResId: Int,
    val programs: List<DestinationProgramSpec>
)

private data class DestinationProgramSpec(
    val title: String,
    @StringRes val titleResId: Int,
    val genre: ViewportGenre,
    @DrawableRes val drawableId: Int,
    @DrawableRes val listDrawableId: Int,
    val year: String,
    val runtime: String,
    val director: String,
    val producer: String,
    @StringRes val metadataGenreResId: Int,
    @StringRes val synopsisResId: Int?,
    val isShow: Boolean,
    val rating: Int
)

private val DestinationProgramSpec.hasDetailHero: Boolean
    get() = detailHeroDrawableId(title) != null

private data class ProgramDetails(
    val year: String,
    val runtime: String,
    val director: String,
    val producer: String,
    @StringRes val synopsisResId: Int?,
    val rating: Int,
    val genreLabel: ViewportGenre? = null
)

private val ProgramDetailsByTitle = mapOf(
    "One Last Breath" to ProgramDetails(
        year = "2013",
        runtime = "2h 13m",
        director = "J. Sarge",
        producer = "M. Holt",
        synopsisResId = R.string.program_synopsis_one_last_breath,
        rating = 4,
        genreLabel = ViewportGenre.Crime
    ),
    "Sink or Swim" to ProgramDetails(
        year = "2015",
        runtime = "1h 51m",
        director = "B. Feinholt",
        producer = "R. Anderson",
        synopsisResId = R.string.program_synopsis_sink_or_swim,
        rating = 3,
        genreLabel = ViewportGenre.Crime
    ),
    "The Baller" to ProgramDetails(
        year = "2018",
        runtime = "1h 38m",
        director = "M. Marsh",
        producer = "S. Sebilla",
        synopsisResId = R.string.program_synopsis_the_baller,
        rating = 4
    ),
    "Troublemaker" to ProgramDetails(
        year = "2009",
        runtime = "2h 5m",
        director = "A. Lockhart",
        producer = "A. Lockhart",
        synopsisResId = R.string.program_synopsis_troublemaker,
        rating = 3
    ),
    "Ignition" to ProgramDetails(
        year = "2022",
        runtime = "2h 14m",
        director = "R. Fullstone",
        producer = "T. Tanning",
        synopsisResId = R.string.program_synopsis_ignition,
        rating = 3
    ),
    "Eruption" to ProgramDetails(
        year = "2024",
        runtime = "1h 34m",
        director = "M. Bennington",
        producer = "C. Lukinski",
        synopsisResId = R.string.program_synopsis_eruption,
        rating = 5
    ),
    "If I May" to ProgramDetails(
        year = "2020",
        runtime = "1h 28m",
        director = "W. Wexler",
        producer = "B. Riley",
        synopsisResId = R.string.program_synopsis_if_i_may,
        rating = 3
    ),
    "The Playmate" to ProgramDetails(
        year = "2010",
        runtime = "2h 28m",
        director = "W. Stills",
        producer = "U. Bannon",
        synopsisResId = R.string.program_synopsis_the_playmate,
        rating = 3
    ),
    "Help" to ProgramDetails(
        year = "2025",
        runtime = "2h 0m",
        director = "K. Jewles",
        producer = "I. Kavan",
        synopsisResId = R.string.program_synopsis_help,
        rating = 2
    ),
    "Light As Air" to ProgramDetails(
        year = "2024",
        runtime = "8 eps",
        director = "M. Marsh",
        producer = "R. Anderson",
        synopsisResId = R.string.program_synopsis_light_as_air,
        rating = 4,
        genreLabel = ViewportGenre.Documentary
    ),
    "Infatuation" to ProgramDetails(
        year = "2021",
        runtime = "8 eps",
        director = "A. Lockhart",
        producer = "S. Sebilla",
        synopsisResId = R.string.program_synopsis_infatuation,
        rating = 4,
        genreLabel = ViewportGenre.Drama
    ),
    "Into The Wild" to ProgramDetails(
        year = "2023",
        runtime = "10 eps",
        director = "B. Feinholt",
        producer = "M. Holt",
        synopsisResId = R.string.program_synopsis_into_the_wild,
        rating = 5,
        genreLabel = ViewportGenre.Documentary
    ),
    "Morbid Temptations" to ProgramDetails(
        year = "2022",
        runtime = "1h 57m",
        director = "W. Wexler",
        producer = "T. Tanning",
        synopsisResId = R.string.program_synopsis_morbid_temptations,
        rating = 4,
        genreLabel = ViewportGenre.Thriller
    )
)

private val PrototypeListTitleOrder = listOf(
    "Light As Air",
    "Into The Wild",
    "Infatuation",
    "Morbid Temptations",
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
    "Morbid Temptations",
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
    "Light As Air",
    "Into The Wild",
    "Infatuation",
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
        titleResId = R.string.row_action_movies,
        genre = ViewportGenre.Action,
        programs = listOf(
            movie("Eruption", ViewportGenre.Action, R.drawable.eruption_card),
            movie("Under Attack", ViewportGenre.Action, R.drawable.under_attack_card),
            movie("Operation Firefly", ViewportGenre.Action, R.drawable.operation_firefly_card),
            movie("Smoke", ViewportGenre.Action, R.drawable.smoke_card),
            movie("Joyriders", ViewportGenre.Action, R.drawable.joyriders_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_comedy_movies,
        genre = ViewportGenre.Comedy,
        programs = listOf(
            movie("Citric", ViewportGenre.Comedy, R.drawable.citric_card),
            movie("The Baller", ViewportGenre.Comedy, R.drawable.the_baller_card),
            movie("Laughing Matters", ViewportGenre.Comedy, R.drawable.laughing_matters_card),
            movie("Troublemaker", ViewportGenre.Comedy, R.drawable.troublemaker_card),
            movie("Lost in Time", ViewportGenre.Comedy, R.drawable.lost_in_time_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_crime_movies,
        genre = ViewportGenre.Crime,
        programs = listOf(
            movie("No Trespassing", ViewportGenre.Crime, R.drawable.no_trespassing_card),
            movie("One Last Breath", ViewportGenre.Crime, R.drawable.one_last_breath_card),
            movie("Sink or Swim", ViewportGenre.Crime, R.drawable.sink_or_swim_card),
            movie("Hungry Heart", ViewportGenre.Crime, R.drawable.hungry_heart_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_documentary_movies,
        genre = ViewportGenre.Documentary,
        programs = listOf(
            movie("Incan Descent", ViewportGenre.Documentary, R.drawable.incan_descent_card),
            movie("Or Not To Be", ViewportGenre.Documentary, R.drawable.or_not_to_be_card),
            movie("Surfside", ViewportGenre.Documentary, R.drawable.surfside_card),
            movie("Wheels", ViewportGenre.Documentary, R.drawable.wheels_card),
            movie("Light as a Feather", ViewportGenre.Documentary, R.drawable.light_as_a_feather_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_drama_movies,
        genre = ViewportGenre.Drama,
        programs = listOf(
            movie("Breathing", ViewportGenre.Drama, R.drawable.breathing_card),
            movie("Falling Behind", ViewportGenre.Drama, R.drawable.falling_behind_card),
            movie("Still There", ViewportGenre.Drama, R.drawable.still_there_card),
            movie("If I May", ViewportGenre.Drama, R.drawable.if_i_may_card),
            movie("Moments", ViewportGenre.Drama, R.drawable.moments_card),
            movie("Chasing Light", ViewportGenre.Drama, R.drawable.chasing_light_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_horror_movies,
        genre = ViewportGenre.Horror,
        programs = listOf(
            movie("The Playmate", ViewportGenre.Horror, R.drawable.the_playmate_card),
            movie("Help", ViewportGenre.Horror, R.drawable.help_card),
            movie("Skin and Bones", ViewportGenre.Horror, R.drawable.skin_and_bones_card),
            movie("The Appetizer", ViewportGenre.Horror, R.drawable.the_appetizer_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_thriller_movies,
        genre = ViewportGenre.Thriller,
        programs = listOf(
            movie("Morbid Temptations", ViewportGenre.Thriller, R.drawable.morbid_temptations_card),
            movie("Enlightenment", ViewportGenre.Thriller, R.drawable.enlightenment_card),
            movie("Ignition", ViewportGenre.Thriller, R.drawable.ignition_card),
            movie("Deadbeat", ViewportGenre.Thriller, R.drawable.deadbeat_card),
            movie("Playing with Fire", ViewportGenre.Thriller, R.drawable.playing_with_fire_card)
        )
    )
)

private val ShowRows = listOf(
    DestinationRowSpec(
        titleResId = R.string.row_action_shows,
        genre = ViewportGenre.Action,
        programs = listOf(
            show("Operation Firefly", ViewportGenre.Action, R.drawable.operation_firefly_card),
            show("Smoke", ViewportGenre.Action, R.drawable.smoke_card),
            show("Joyriders", ViewportGenre.Action, R.drawable.joyriders_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_comedy_shows,
        genre = ViewportGenre.Comedy,
        programs = listOf(
            show("Citric", ViewportGenre.Comedy, R.drawable.citric_card),
            show("Laughing Matters", ViewportGenre.Comedy, R.drawable.laughing_matters_card),
            show("Lost in Time", ViewportGenre.Comedy, R.drawable.lost_in_time_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_crime_shows,
        genre = ViewportGenre.Crime,
        programs = listOf(
            show("No Trespassing", ViewportGenre.Crime, R.drawable.no_trespassing_card),
            show("Hungry Heart", ViewportGenre.Crime, R.drawable.hungry_heart_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_documentary_shows,
        genre = ViewportGenre.Documentary,
        programs = listOf(
            show("Light As Air", ViewportGenre.Documentary, R.drawable.light_as_air_card),
            show("Into The Wild", ViewportGenre.Documentary, R.drawable.into_the_wild_card),
            show("Surfside", ViewportGenre.Documentary, R.drawable.surfside_card),
            show("Wheels", ViewportGenre.Documentary, R.drawable.wheels_card),
            show("Light as a Feather", ViewportGenre.Documentary, R.drawable.light_as_a_feather_card),
            show("Incan Descent", ViewportGenre.Documentary, R.drawable.incan_descent_card),
            show("Or Not To Be", ViewportGenre.Documentary, R.drawable.or_not_to_be_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_drama_shows,
        genre = ViewportGenre.Drama,
        programs = listOf(
            show("Infatuation", ViewportGenre.Drama, R.drawable.infatuation_card),
            show("Moments", ViewportGenre.Drama, R.drawable.moments_card),
            show("Chasing Light", ViewportGenre.Drama, R.drawable.chasing_light_card),
            show("Breathing", ViewportGenre.Drama, R.drawable.breathing_card),
            show("Falling Behind", ViewportGenre.Drama, R.drawable.falling_behind_card),
            show("Still There", ViewportGenre.Drama, R.drawable.still_there_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_horror_shows,
        genre = ViewportGenre.Horror,
        programs = listOf(
            show("Skin and Bones", ViewportGenre.Horror, R.drawable.skin_and_bones_card),
            show("The Appetizer", ViewportGenre.Horror, R.drawable.the_appetizer_card)
        )
    ),
    DestinationRowSpec(
        titleResId = R.string.row_thriller_shows,
        genre = ViewportGenre.Thriller,
        programs = listOf(
            show("Enlightenment", ViewportGenre.Thriller, R.drawable.enlightenment_card),
            show("Deadbeat", ViewportGenre.Thriller, R.drawable.deadbeat_card),
            show("Playing with Fire", ViewportGenre.Thriller, R.drawable.playing_with_fire_card)
        )
    )
)

private val SinkOrSwimLibraryGenreOrder = listOf(
    ViewportGenre.Crime,
    ViewportGenre.Thriller,
    ViewportGenre.Comedy,
    ViewportGenre.Action,
    ViewportGenre.Drama,
    ViewportGenre.Documentary,
    ViewportGenre.Horror
)

private val SinkOrSwimLibraryRowSpecs = SinkOrSwimLibraryGenreOrder.map { genre ->
    SinkOrSwimLibraryRowSpec(
        titleResId = sinkOrSwimLibraryTitleResId(genre),
        programs = (MovieRows + ShowRows)
            .filter { it.genre == genre }
            .flatMap { it.programs }
            .distinctBy { it.title }
    )
}

@StringRes
private fun sinkOrSwimLibraryTitleResId(genre: ViewportGenre): Int = when (genre) {
    ViewportGenre.Crime -> R.string.home_row_crime
    ViewportGenre.Thriller -> R.string.home_row_thriller
    ViewportGenre.Comedy -> R.string.home_row_comedy
    ViewportGenre.Action -> R.string.home_row_action
    ViewportGenre.Drama -> R.string.home_row_drama
    ViewportGenre.Documentary -> R.string.home_row_documentary
    ViewportGenre.Horror -> R.string.home_row_horror
    ViewportGenre.All -> R.string.app_name
}

private fun movie(
    title: String,
    genre: ViewportGenre,
    @DrawableRes drawableId: Int
): DestinationProgramSpec {
    val details = programDetails(title = title, genre = genre, isShow = false)
    return DestinationProgramSpec(
        title = title,
        titleResId = programTitleResId(title),
        genre = genre,
        drawableId = drawableId,
        listDrawableId = listPosterDrawableId(title) ?: drawableId,
        year = details.year,
        runtime = details.runtime,
        director = details.director,
        producer = details.producer,
        metadataGenreResId = (details.genreLabel ?: genre).displayNameResId,
        synopsisResId = details.synopsisResId,
        isShow = false,
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
        titleResId = programTitleResId(title),
        genre = genre,
        drawableId = drawableId,
        listDrawableId = listPosterDrawableId(title) ?: drawableId,
        year = details.year,
        runtime = details.runtime,
        director = details.director,
        producer = details.producer,
        metadataGenreResId = (details.genreLabel ?: genre).displayNameResId,
        synopsisResId = details.synopsisResId,
        isShow = true,
        rating = details.rating
    )
}

@StringRes
private fun programTitleResId(title: String): Int = when (title) {
    "One Last Breath" -> R.string.program_one_last_breath
    "Sink or Swim" -> R.string.program_sink_or_swim
    "The Baller" -> R.string.program_the_baller
    "Troublemaker" -> R.string.program_troublemaker
    "Ignition" -> R.string.program_ignition
    "Eruption" -> R.string.program_eruption
    "If I May" -> R.string.program_if_i_may
    "The Playmate" -> R.string.program_the_playmate
    "Help" -> R.string.program_help
    "Under Attack" -> R.string.program_under_attack
    "No Trespassing" -> R.string.program_no_trespassing
    "Hungry Heart" -> R.string.program_hungry_heart
    "Enlightenment" -> R.string.program_enlightenment
    "Deadbeat" -> R.string.program_deadbeat
    "Playing with Fire" -> R.string.program_playing_with_fire
    "Citric" -> R.string.program_citric
    "Laughing Matters" -> R.string.program_laughing_matters
    "Lost in Time" -> R.string.program_lost_in_time
    "Operation Firefly" -> R.string.program_operation_firefly
    "Smoke" -> R.string.program_smoke
    "Joyriders" -> R.string.program_joyriders
    "Moments" -> R.string.program_moments
    "Chasing Light" -> R.string.program_chasing_light
    "Breathing" -> R.string.program_breathing
    "Falling Behind" -> R.string.program_falling_behind
    "Still There" -> R.string.program_still_there
    "Surfside" -> R.string.program_surfside
    "Wheels" -> R.string.program_wheels
    "Light as a Feather" -> R.string.program_light_as_a_feather
    "Incan Descent" -> R.string.program_incan_descent
    "Or Not To Be" -> R.string.program_or_not_to_be
    "Skin and Bones" -> R.string.program_skin_and_bones
    "The Appetizer" -> R.string.program_the_appetizer
    "Light As Air" -> R.string.program_light_as_air
    "Infatuation" -> R.string.program_infatuation
    "Into The Wild" -> R.string.program_into_the_wild
    "Morbid Temptations" -> R.string.program_morbid_temptations
    else -> R.string.app_name
}

@DrawableRes
private fun listPosterDrawableId(title: String): Int? = when (title) {
    "One Last Breath" -> R.drawable.one_last_breath_card
    "Sink or Swim" -> R.drawable.sink_or_swim_card
    "The Baller" -> R.drawable.the_baller_card
    "Troublemaker" -> R.drawable.troublemaker_card
    "Ignition" -> R.drawable.ignition_card
    "Eruption" -> R.drawable.eruption_card
    "If I May" -> R.drawable.if_i_may_card
    "The Playmate" -> R.drawable.the_playmate_card
    "Help" -> R.drawable.help_card
    "Under Attack" -> R.drawable.under_attack_card
    "No Trespassing" -> R.drawable.no_trespassing_card
    "Hungry Heart" -> R.drawable.hungry_heart_card
    "Enlightenment" -> R.drawable.enlightenment_card
    "Deadbeat" -> R.drawable.deadbeat_card
    "Playing with Fire" -> R.drawable.playing_with_fire_card
    "Citric" -> R.drawable.citric_card
    "Laughing Matters" -> R.drawable.laughing_matters_card
    "Lost in Time" -> R.drawable.lost_in_time_card
    "Operation Firefly" -> R.drawable.operation_firefly_card
    "Smoke" -> R.drawable.smoke_card
    "Joyriders" -> R.drawable.joyriders_card
    "Moments" -> R.drawable.moments_card
    "Chasing Light" -> R.drawable.chasing_light_card
    "Breathing" -> R.drawable.breathing_card
    "Falling Behind" -> R.drawable.falling_behind_card
    "Still There" -> R.drawable.still_there_card
    "Surfside" -> R.drawable.surfside_card
    "Wheels" -> R.drawable.wheels_card
    "Light as a Feather" -> R.drawable.light_as_a_feather_card
    "Incan Descent" -> R.drawable.incan_descent_card
    "Or Not To Be" -> R.drawable.or_not_to_be_card
    "Skin and Bones" -> R.drawable.skin_and_bones_card
    "The Appetizer" -> R.drawable.the_appetizer_card
    "Light As Air" -> R.drawable.light_as_air_card
    "Infatuation" -> R.drawable.infatuation_card
    "Into The Wild" -> R.drawable.into_the_wild_card
    "Morbid Temptations" -> R.drawable.morbid_temptations_card
    else -> null
}

@DrawableRes
private fun detailHeroDrawableId(title: String): Int? = when (title) {
    "One Last Breath" -> R.drawable.one_last_breath_hero
    "Sink or Swim" -> R.drawable.sink_or_swim_hero
    "The Baller" -> R.drawable.the_baller_hero
    "Troublemaker" -> R.drawable.troublemaker_hero
    "Ignition" -> R.drawable.ignition_hero
    "Eruption" -> R.drawable.eruption_hero
    "If I May" -> R.drawable.if_i_may_hero
    "The Playmate" -> R.drawable.the_playmate_hero
    "Help" -> R.drawable.help_hero
    "Under Attack" -> R.drawable.under_attack_hero
    "No Trespassing" -> R.drawable.no_trespassing_hero
    "Hungry Heart" -> R.drawable.hungry_heart_hero
    "Enlightenment" -> R.drawable.enlightenment_hero
    "Deadbeat" -> R.drawable.deadbeat_hero
    "Playing with Fire" -> R.drawable.playing_with_fire_hero
    "Citric" -> R.drawable.citric_hero
    "Laughing Matters" -> R.drawable.laughing_matters_hero
    "Lost in Time" -> R.drawable.lost_in_time_hero
    "Operation Firefly" -> R.drawable.operation_firefly_hero
    "Smoke" -> R.drawable.smoke_hero
    "Joyriders" -> R.drawable.joyriders_hero
    "Moments" -> R.drawable.moments_hero
    "Chasing Light" -> R.drawable.chasing_light_hero
    "Breathing" -> R.drawable.breathing_hero
    "Falling Behind" -> R.drawable.falling_behind_hero
    "Still There" -> R.drawable.still_there_hero
    "Surfside" -> R.drawable.surfside_hero
    "Wheels" -> R.drawable.wheels_hero
    "Light as a Feather" -> R.drawable.light_as_a_feather_hero
    "Incan Descent" -> R.drawable.incan_descent_hero
    "Or Not To Be" -> R.drawable.or_not_to_be_hero
    "Skin and Bones" -> R.drawable.skin_and_bones_hero
    "The Appetizer" -> R.drawable.the_appetizer_hero
    "Light As Air" -> R.drawable.light_as_air_hero
    "Infatuation" -> R.drawable.infatuation_hero
    "Into The Wild" -> R.drawable.into_the_wild_hero
    "Morbid Temptations" -> R.drawable.morbid_temptations_hero
    else -> null
}

private fun detailProgramSpec(title: String): DestinationProgramSpec? {
    val rows = if (title in PrototypeShowSmallTitleOrder) {
        ShowRows + MovieRows
    } else {
        MovieRows + ShowRows
    }

    return rows
        .asSequence()
        .flatMap { it.programs.asSequence() }
        .firstOrNull { it.title == title }
}

internal fun cinerificProgramIsShow(title: String): Boolean {
    return detailProgramSpec(title)?.isShow ?: (title in PrototypeShowSmallTitleOrder)
}

internal fun cinerificFilledFavoritePlaceholderCount(
    titles: List<String>,
    isShow: Boolean
): Int {
    return favoriteProgramSpecs(titles)
        .filter { it.isShow == isShow }
        .take(CINERIFIC_FAVORITES_CAPACITY)
        .count()
}

private fun favoriteProgramSpecs(titles: List<String>): List<DestinationProgramSpec> {
    return titles
        .distinct()
        .mapNotNull { detailProgramSpec(it) }
}

private fun adjacentDetailProgramTitle(title: String, offset: Int): String {
    val titles = SinkOrSwimLibraryRowSpecs
        .flatMap { it.programs }
        .map { it.title }
        .distinct()
    if (titles.isEmpty()) return title

    val currentIndex = titles.indexOf(title)
    if (currentIndex == -1) return title

    val nextIndex = (currentIndex + offset).floorMod(titles.size)
    return titles[nextIndex]
}

private fun Int.floorMod(modulus: Int): Int = ((this % modulus) + modulus) % modulus

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
        synopsisResId = null,
        rating = 2 + seed % 4
    )
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

private fun prototypeSmallPriority(isShows: Boolean, programTitle: String): Int {
    val order = if (isShows) {
        PrototypeShowSmallTitleOrder
    } else {
        PrototypeMovieSmallTitleOrder
    }
    val index = order.indexOf(programTitle)
    return if (index == -1) Int.MAX_VALUE else index
}

private fun smallCollagePrograms(
    isShows: Boolean,
    programs: List<DestinationProgramSpec>
): List<DestinationProgramSpec> {
    return programs
        .withIndex()
        .sortedWith(compareBy({ prototypeSmallPriority(isShows, it.value.title) }, { it.index }))
        .map { it.value }
}
