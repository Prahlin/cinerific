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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prahlin.cinerific.R

private const val HOME_FRAME_WIDTH = 1194f
private const val CARD_ASPECT = 350f / 263f

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
        val horizontalPadding = figmaDp(50f, scale)
        val cardWidth = figmaDp(350f, scale)
        val cardHeight = cardWidth / CARD_ASPECT
        val cardGap = figmaDp(50f, scale)

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
            Image(
                painter = painterResource(R.drawable.home_hero_header),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1194f / 1298f),
                contentScale = ContentScale.FillBounds
            )

            HomeProgramRows.forEachIndexed { index, row ->
                HomeProgramRow(
                    title = row.title,
                    cardIds = row.cardIds,
                    horizontalPadding = horizontalPadding,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    cardGap = cardGap,
                    topPadding = if (index == 0) 26.dp else 40.dp
                )
            }

            Spacer(modifier = Modifier.height(56.dp))
        }
    }
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
