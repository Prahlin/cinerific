package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prahlin.cinerific.R

private const val DESTINATION_FRAME_WIDTH = 1194f
private const val DESTINATION_TOP_BAR_TITLE_BOTTOM = 18f
private const val DESTINATION_CARD_ASPECT = 350f / 263f
private const val DESTINATION_CARD_SCALE = 0.8f

private val DestinationTop = Color(0xFF080007)
private val DestinationMid = Color(0xFF23001F)
private val DestinationBottom = Color(0xFF060004)
private val DestinationText = Color(0xFFE7E7E7)
private val DestinationSubtle = Color(0xFFBDBDBD)

@Composable
internal fun CinerificDestinationScreen(
    destination: CinerificDestination,
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
        CinerificDestination.Favorites -> CinerificCatalogScreen(
            title = "Favorites",
            rows = FavoriteRows,
            showViewportNav = false,
            modifier = modifier
        )
        CinerificDestination.Settings -> CinerificSettingsScreen(modifier = modifier)
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
        var selectedMode by remember(title) { mutableStateOf(ViewportMode.Collage) }
        val scrollState = rememberScrollState()
        val visibleRows = if (!showViewportNav || selectedGenre == ViewportGenre.All) {
            rows
        } else {
            rows.filter { it.genre == selectedGenre }
        }
        val visiblePrograms = visibleRows.flatMap { it.programs }
        val visibleListPrograms = visiblePrograms
            .withIndex()
            .sortedWith(compareBy({ prototypeListPriority(it.value.title) }, { it.index }))
            .map { it.value }

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
                ViewportMode.Collage -> {
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
                            topPadding = if (index == 0) {
                                if (showViewportNav) 48.dp else 72.dp
                            } else {
                                80.dp
                            }
                        )
                    }
                }
                ViewportMode.Grid -> DestinationProgramGrid(
                    title = if (selectedGenre == ViewportGenre.All) "All $title" else "${selectedGenre.displayName} $title",
                    programs = visiblePrograms,
                    horizontalPadding = horizontalPadding,
                    rightPadding = rightPadding,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    cardGap = cardGap,
                    columnCount = contentColumnCount,
                    topPadding = 48.dp
                )
                ViewportMode.List -> DestinationProgramList(
                    programs = visibleListPrograms,
                    horizontalPadding = horizontalPadding,
                    rightPadding = rightPadding,
                    scale = scale,
                    topPadding = 48.dp
                )
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
        Text(
            text = title,
            color = DestinationText,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.sp,
            modifier = Modifier.padding(start = horizontalPadding, end = rightPadding)
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
private fun DestinationProgramGrid(
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
        Text(
            text = title,
            color = DestinationText,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.sp,
            modifier = Modifier.padding(start = horizontalPadding, end = rightPadding)
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
private fun DestinationProgramList(
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = horizontalPadding, end = rightPadding),
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
        verticalAlignment = Alignment.CenterVertically
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
private fun CinerificSettingsScreen(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DestinationBottom)
    ) {
        val scale = maxWidth.value / DESTINATION_FRAME_WIDTH
        val density = LocalDensity.current
        val horizontalPadding = destinationDp(64f, scale)
        val rightPadding = destinationDp(160f, scale)
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
                .padding(
                    start = horizontalPadding,
                    top = topBarHeight,
                    end = rightPadding,
                    bottom = 80.dp + bottomSystemPadding
                )
        ) {
            SettingsSection(
                title = "Accessibility",
                rows = listOf(
                    "Language" to "Select your language of choice",
                    "Simplify UI" to "Recommended for children",
                    "Dark mode" to "App darkens during night time"
                )
            )
            SettingsSection(
                title = "Playback",
                rows = listOf(
                    "Autoplay" to "Automatically play the next video",
                    "Background play" to "Play Cinerific behind other apps",
                    "Wifi downloads only" to "Some carriers will charge for data"
                )
            )
            SettingsSection(
                title = "Billing & Payment",
                rows = listOf(
                    "Bill automatically" to "Only applicable if billing information provided",
                    "Notifications" to "Receive email notifications 7 days prior to your billing date"
                )
            )
            SettingsSection(
                title = "Security",
                rows = listOf(
                    "Auto log out" to "Log out automatically following 10 minutes of inactivity",
                    "Two-Factor Authentication" to "Provides an extra level of security"
                )
            )
        }

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
    rows: List<Pair<String, String>>
) {
    Column(modifier = Modifier.padding(top = 72.dp)) {
        Text(
            text = title,
            color = DestinationText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.sp
        )

        rows.forEach { (label, detail) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 34.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        color = DestinationText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp
                    )
                    Text(
                        text = detail,
                        color = DestinationSubtle,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        letterSpacing = 0.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                TogglePill()
            }
        }
    }
}

@Composable
private fun TogglePill() {
    Box(
        modifier = Modifier
            .width(92.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Color(0xFF3A3338))
            .padding(5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFD9D9D9))
        )
    }
}

private fun destinationDp(px: Float, scale: Float): Dp = (px * scale).dp

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

private val MovieRows = listOf(
    DestinationRowSpec(
        title = "Action Movies",
        genre = ViewportGenre.Action,
        programs = listOf(
            movie("Eruption", ViewportGenre.Action, R.drawable.home_action_01),
            movie("Skyline Rush", ViewportGenre.Action, R.drawable.home_action_02),
            movie("Signal Run", ViewportGenre.Action, R.drawable.home_action_03),
            movie("Impact Window", ViewportGenre.Action, R.drawable.home_action_04),
            movie("Final Lift", ViewportGenre.Action, R.drawable.home_action_05)
        )
    ),
    DestinationRowSpec(
        title = "Comedy Movies",
        genre = ViewportGenre.Comedy,
        programs = listOf(
            movie("Citric", ViewportGenre.Comedy, R.drawable.home_comedy_01),
            movie("The Baller", ViewportGenre.Comedy, R.drawable.home_comedy_05),
            movie("Odd Hours", ViewportGenre.Comedy, R.drawable.home_comedy_03),
            movie("Troublemaker", ViewportGenre.Comedy, R.drawable.home_comedy_02),
            movie("Lost Time", ViewportGenre.Comedy, R.drawable.home_comedy_04)
        )
    ),
    DestinationRowSpec(
        title = "Crime Movies",
        genre = ViewportGenre.Crime,
        programs = listOf(
            movie("Night Ledger", ViewportGenre.Crime, R.drawable.home_crime_01),
            movie("Cold Case", ViewportGenre.Crime, R.drawable.home_crime_02),
            movie("Quiet Witness", ViewportGenre.Crime, R.drawable.home_crime_03),
            movie("City Trace", ViewportGenre.Crime, R.drawable.home_crime_04)
        )
    ),
    DestinationRowSpec(
        title = "Documentary Movies",
        genre = ViewportGenre.Documentary,
        programs = listOf(
            movie("Sink or Swim", ViewportGenre.Documentary, R.drawable.home_documentary_01),
            movie("Field Notes", ViewportGenre.Documentary, R.drawable.home_documentary_02),
            movie("Open Road", ViewportGenre.Documentary, R.drawable.home_documentary_03),
            movie("Still Moving", ViewportGenre.Documentary, R.drawable.home_documentary_04),
            movie("The Long Frame", ViewportGenre.Documentary, R.drawable.home_documentary_05)
        )
    ),
    DestinationRowSpec(
        title = "Drama Movies",
        genre = ViewportGenre.Drama,
        programs = listOf(
            movie("One Last Breath", ViewportGenre.Drama, R.drawable.home_drama_01),
            movie("After the Rain", ViewportGenre.Drama, R.drawable.home_drama_02),
            movie("The Quiet Room", ViewportGenre.Drama, R.drawable.home_drama_03),
            movie("If I May", ViewportGenre.Drama, R.drawable.home_drama_04),
            movie("Incan Descent", ViewportGenre.Drama, R.drawable.home_drama_05),
            movie("Last Light", ViewportGenre.Drama, R.drawable.home_drama_06)
        )
    ),
    DestinationRowSpec(
        title = "Horror Movies",
        genre = ViewportGenre.Horror,
        programs = listOf(
            movie("Help", ViewportGenre.Horror, R.drawable.home_horror_01),
            movie("The Playmate", ViewportGenre.Horror, R.drawable.home_horror_02),
            movie("Open Door", ViewportGenre.Horror, R.drawable.home_horror_03),
            movie("The Hollow", ViewportGenre.Horror, R.drawable.home_horror_04)
        )
    ),
    DestinationRowSpec(
        title = "Thriller Movies",
        genre = ViewportGenre.Thriller,
        programs = listOf(
            movie("Ignition", ViewportGenre.Thriller, R.drawable.home_thriller_01),
            movie("Threshold", ViewportGenre.Thriller, R.drawable.home_thriller_02),
            movie("Hidden Current", ViewportGenre.Thriller, R.drawable.home_thriller_03),
            movie("Last Signal", ViewportGenre.Thriller, R.drawable.home_thriller_04)
        )
    )
)

private val ShowRows = listOf(
    DestinationRowSpec(
        title = "Action Shows",
        genre = ViewportGenre.Action,
        programs = listOf(
            show("Run Point", ViewportGenre.Action, R.drawable.home_action_03),
            show("Impact Window", ViewportGenre.Action, R.drawable.home_action_04),
            show("Skyline Rush", ViewportGenre.Action, R.drawable.home_action_02),
            show("Final Lift", ViewportGenre.Action, R.drawable.home_action_05)
        )
    ),
    DestinationRowSpec(
        title = "Comedy Shows",
        genre = ViewportGenre.Comedy,
        programs = listOf(
            show("Odd Hours", ViewportGenre.Comedy, R.drawable.home_comedy_03),
            show("The Baller", ViewportGenre.Comedy, R.drawable.home_comedy_05),
            show("Troublemaker", ViewportGenre.Comedy, R.drawable.home_comedy_02),
            show("Lost Time", ViewportGenre.Comedy, R.drawable.home_comedy_04)
        )
    ),
    DestinationRowSpec(
        title = "Crime Shows",
        genre = ViewportGenre.Crime,
        programs = listOf(
            show("Night Ledger", ViewportGenre.Crime, R.drawable.home_crime_01),
            show("Quiet Witness", ViewportGenre.Crime, R.drawable.home_crime_03),
            show("City Trace", ViewportGenre.Crime, R.drawable.home_crime_04),
            show("Cold Case", ViewportGenre.Crime, R.drawable.home_crime_02)
        )
    ),
    DestinationRowSpec(
        title = "Documentary Shows",
        genre = ViewportGenre.Documentary,
        programs = listOf(
            show("Field Notes", ViewportGenre.Documentary, R.drawable.home_documentary_02),
            show("Still Moving", ViewportGenre.Documentary, R.drawable.home_documentary_04),
            show("Sink or Swim", ViewportGenre.Documentary, R.drawable.home_documentary_01),
            show("The Long Frame", ViewportGenre.Documentary, R.drawable.home_documentary_05)
        )
    ),
    DestinationRowSpec(
        title = "Drama Shows",
        genre = ViewportGenre.Drama,
        programs = listOf(
            show("One Last Breath", ViewportGenre.Drama, R.drawable.home_drama_01),
            show("Incan Descent", ViewportGenre.Drama, R.drawable.home_drama_05),
            show("If I May", ViewportGenre.Drama, R.drawable.home_drama_04),
            show("Last Light", ViewportGenre.Drama, R.drawable.home_drama_06)
        )
    ),
    DestinationRowSpec(
        title = "Horror Shows",
        genre = ViewportGenre.Horror,
        programs = listOf(
            show("Help", ViewportGenre.Horror, R.drawable.home_horror_01),
            show("Open Door", ViewportGenre.Horror, R.drawable.home_horror_03),
            show("The Playmate", ViewportGenre.Horror, R.drawable.home_horror_02),
            show("The Hollow", ViewportGenre.Horror, R.drawable.home_horror_04)
        )
    ),
    DestinationRowSpec(
        title = "Thriller Shows",
        genre = ViewportGenre.Thriller,
        programs = listOf(
            show("Ignition", ViewportGenre.Thriller, R.drawable.home_thriller_01),
            show("Last Signal", ViewportGenre.Thriller, R.drawable.home_thriller_04),
            show("Hidden Current", ViewportGenre.Thriller, R.drawable.home_thriller_03),
            show("Threshold", ViewportGenre.Thriller, R.drawable.home_thriller_02)
        )
    )
)

private val FavoriteRows = listOf(
    DestinationRowSpec(
        title = "Favorites",
        genre = ViewportGenre.All,
        programs = listOf(
            movie("Quiet Witness", ViewportGenre.Crime, R.drawable.home_crime_03),
            movie("The Baller", ViewportGenre.Comedy, R.drawable.home_comedy_05),
            movie("Hidden Current", ViewportGenre.Thriller, R.drawable.home_thriller_03),
            movie("Final Lift", ViewportGenre.Action, R.drawable.home_action_05),
            movie("Last Light", ViewportGenre.Drama, R.drawable.home_drama_06)
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
