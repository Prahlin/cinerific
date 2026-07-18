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
import androidx.compose.ui.text.font.FontWeight
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
                    title = if (selectedGenre == ViewportGenre.All) "All $title" else "${selectedGenre.displayName} $title",
                    programs = visiblePrograms,
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
            modifier = Modifier.width(destinationDp(424f, scale))
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
                .padding(top = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
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
    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(destinationDp(116f, scale))
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(destinationDp(12f, scale)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(destinationDp(140f, scale))
                .height(destinationDp(92f, scale))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
        ) {
            Image(
                painter = painterResource(program.drawableId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = destinationDp(26f, scale))
        ) {
            Text(
                text = program.title,
                color = DestinationText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
                maxLines = 1
            )
            Text(
                text = program.genre.displayName,
                color = DestinationSubtle,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
                modifier = Modifier.padding(top = 8.dp)
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
    @DrawableRes val drawableId: Int
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
            movie("Sunny Side", ViewportGenre.Comedy, R.drawable.home_comedy_02),
            movie("Odd Hours", ViewportGenre.Comedy, R.drawable.home_comedy_03),
            movie("Triple Maker", ViewportGenre.Comedy, R.drawable.home_comedy_04),
            movie("Fresh Laughs", ViewportGenre.Comedy, R.drawable.home_comedy_05)
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
            movie("Paper Hearts", ViewportGenre.Drama, R.drawable.home_drama_04),
            movie("Incan Descent", ViewportGenre.Drama, R.drawable.home_drama_05),
            movie("Last Light", ViewportGenre.Drama, R.drawable.home_drama_06)
        )
    ),
    DestinationRowSpec(
        title = "Horror Movies",
        genre = ViewportGenre.Horror,
        programs = listOf(
            movie("Hallway 9", ViewportGenre.Horror, R.drawable.home_horror_01),
            movie("Static Bloom", ViewportGenre.Horror, R.drawable.home_horror_02),
            movie("Open Door", ViewportGenre.Horror, R.drawable.home_horror_03),
            movie("The Hollow", ViewportGenre.Horror, R.drawable.home_horror_04)
        )
    ),
    DestinationRowSpec(
        title = "Thriller Movies",
        genre = ViewportGenre.Thriller,
        programs = listOf(
            movie("Red Angle", ViewportGenre.Thriller, R.drawable.home_thriller_01),
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
            show("Fresh Laughs", ViewportGenre.Comedy, R.drawable.home_comedy_05),
            show("Sunny Side", ViewportGenre.Comedy, R.drawable.home_comedy_02),
            show("Triple Maker", ViewportGenre.Comedy, R.drawable.home_comedy_04)
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
            show("Paper Hearts", ViewportGenre.Drama, R.drawable.home_drama_04),
            show("Last Light", ViewportGenre.Drama, R.drawable.home_drama_06)
        )
    ),
    DestinationRowSpec(
        title = "Horror Shows",
        genre = ViewportGenre.Horror,
        programs = listOf(
            show("Hallway 9", ViewportGenre.Horror, R.drawable.home_horror_01),
            show("Open Door", ViewportGenre.Horror, R.drawable.home_horror_03),
            show("Static Bloom", ViewportGenre.Horror, R.drawable.home_horror_02),
            show("The Hollow", ViewportGenre.Horror, R.drawable.home_horror_04)
        )
    ),
    DestinationRowSpec(
        title = "Thriller Shows",
        genre = ViewportGenre.Thriller,
        programs = listOf(
            show("Red Angle", ViewportGenre.Thriller, R.drawable.home_thriller_01),
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
            movie("Sunny Side", ViewportGenre.Comedy, R.drawable.home_comedy_02),
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
): DestinationProgramSpec = DestinationProgramSpec(title = title, genre = genre, drawableId = drawableId)

private fun show(
    title: String,
    genre: ViewportGenre,
    @DrawableRes drawableId: Int
): DestinationProgramSpec = DestinationProgramSpec(title = title, genre = genre, drawableId = drawableId)
