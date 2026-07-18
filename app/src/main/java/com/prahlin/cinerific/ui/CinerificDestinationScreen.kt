package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            rows = MovieRows,
            modifier = modifier
        )
        CinerificDestination.Shows -> CinerificCatalogScreen(
            title = "Shows",
            rows = ShowRows,
            modifier = modifier
        )
        CinerificDestination.Favorites -> CinerificCatalogScreen(
            title = "Favorites",
            rows = FavoriteRows,
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
                .padding(top = topBarHeight)
        ) {
            rows.forEachIndexed { index, row ->
                DestinationProgramRow(
                    title = row.title,
                    cardIds = row.cardIds,
                    horizontalPadding = horizontalPadding,
                    rightPadding = rightPadding,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    cardGap = cardGap,
                    topPadding = if (index == 0) 72.dp else 80.dp
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
private fun DestinationProgramRow(
    title: String,
    @DrawableRes cardIds: List<Int>,
    horizontalPadding: Dp,
    rightPadding: Dp,
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
        Text(
            text = title,
            color = DestinationText,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.sp,
            modifier = Modifier.padding(start = horizontalPadding, end = rightPadding)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = horizontalPadding, end = rightPadding)
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(cardGap)
        ) {
            cardIds.forEach { cardId ->
                DestinationProgramCard(
                    drawableId = cardId,
                    width = cardWidth,
                    height = cardHeight
                )
            }
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
    @DrawableRes val cardIds: List<Int>
)

private val MovieRows = listOf(
    DestinationRowSpec(
        "All Movies",
        listOf(
            R.drawable.home_action_01,
            R.drawable.home_crime_02,
            R.drawable.home_comedy_01,
            R.drawable.home_drama_03,
            R.drawable.home_thriller_02
        )
    ),
    DestinationRowSpec(
        "Featured",
        listOf(
            R.drawable.home_action_04,
            R.drawable.home_documentary_01,
            R.drawable.home_horror_02,
            R.drawable.home_comedy_04
        )
    )
)

private val ShowRows = listOf(
    DestinationRowSpec(
        "All Shows",
        listOf(
            R.drawable.home_crime_01,
            R.drawable.home_thriller_01,
            R.drawable.home_drama_01,
            R.drawable.home_documentary_02,
            R.drawable.home_horror_01
        )
    ),
    DestinationRowSpec(
        "Collections",
        listOf(
            R.drawable.home_comedy_03,
            R.drawable.home_action_03,
            R.drawable.home_drama_05,
            R.drawable.home_thriller_04
        )
    )
)

private val FavoriteRows = listOf(
    DestinationRowSpec(
        "Favorites",
        listOf(
            R.drawable.home_crime_03,
            R.drawable.home_comedy_02,
            R.drawable.home_thriller_03,
            R.drawable.home_action_05,
            R.drawable.home_drama_06
        )
    )
)
