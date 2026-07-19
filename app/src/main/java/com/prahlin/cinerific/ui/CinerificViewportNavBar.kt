package com.prahlin.cinerific.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal enum class ViewportGenre(
    val compactLabel: String,
    val displayName: String
) {
    All("", "All"),
    Action("ACT", "Action"),
    Comedy("COM", "Comedy"),
    Crime("CRI", "Crime"),
    Documentary("DOC", "Documentary"),
    Drama("DRA", "Drama"),
    Horror("HOR", "Horror"),
    Thriller("THR", "Thriller")
}

internal enum class ViewportMode {
    Collage,
    Grid,
    List
}

private val ViewportStroke = Color(0xFFE7E7E7)
private val ViewportInactive = Color(0xFF8A828C)
private val ViewportSelectedFill = Color(0xFFE7E7E7)
private val ViewportSelectedInk = Color(0xFF2A2A2A)
private val ViewportPanelFill = Color(0x221E1E1E)

private const val GENRE_GROUP_WIDTH = 160.464f
private const val GENRE_GROUP_HEIGHT = 77.913f
private const val GENRE_TEXT_CHIP_SIZE = 33.391f
private const val GENRE_ALL_CHIP_SIZE = 37.101f
private const val MODE_GROUP_WIDTH = 190f
private const val MODE_GROUP_HEIGHT = 58f
private const val MODE_BUTTON_SIZE = 54.692f

@Composable
internal fun CinerificViewportNavBar(
    selectedGenre: ViewportGenre,
    selectedMode: ViewportMode,
    onGenreSelected: (ViewportGenre) -> Unit,
    onModeSelected: (ViewportMode) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        GenreCluster(
            selectedGenre = selectedGenre,
            scale = scale,
            onGenreSelected = onGenreSelected
        )

        Spacer(modifier = Modifier.weight(1f))

        ViewportModeCluster(
            selectedMode = selectedMode,
            scale = scale,
            onModeSelected = onModeSelected
        )
    }
}

@Composable
private fun GenreCluster(
    selectedGenre: ViewportGenre,
    scale: Float,
    onGenreSelected: (ViewportGenre) -> Unit
) {
    Box(
        modifier = Modifier
            .width(viewportDp(GENRE_GROUP_WIDTH, scale))
            .height(viewportDp(GENRE_GROUP_HEIGHT, scale))
    ) {
        GenreChip(
            genre = ViewportGenre.All,
            selected = selectedGenre == ViewportGenre.All,
            x = 0f,
            y = 0.93f,
            width = GENRE_ALL_CHIP_SIZE,
            height = GENRE_ALL_CHIP_SIZE,
            scale = scale,
            onClick = onGenreSelected
        )
        GenreChip(
            genre = ViewportGenre.Action,
            selected = selectedGenre == ViewportGenre.Action,
            x = 43.59f,
            y = 2.78f,
            scale = scale,
            onClick = onGenreSelected
        )
        GenreChip(
            genre = ViewportGenre.Comedy,
            selected = selectedGenre == ViewportGenre.Comedy,
            x = 85.33f,
            y = 2.78f,
            scale = scale,
            onClick = onGenreSelected
        )
        GenreChip(
            genre = ViewportGenre.Crime,
            selected = selectedGenre == ViewportGenre.Crime,
            x = 127.07f,
            y = 2.78f,
            scale = scale,
            onClick = onGenreSelected
        )
        GenreChip(
            genre = ViewportGenre.Documentary,
            selected = selectedGenre == ViewportGenre.Documentary,
            x = 1.86f,
            y = 44.52f,
            scale = scale,
            onClick = onGenreSelected
        )
        GenreChip(
            genre = ViewportGenre.Drama,
            selected = selectedGenre == ViewportGenre.Drama,
            x = 43.59f,
            y = 44.52f,
            scale = scale,
            onClick = onGenreSelected
        )
        GenreChip(
            genre = ViewportGenre.Horror,
            selected = selectedGenre == ViewportGenre.Horror,
            x = 85.33f,
            y = 44.52f,
            scale = scale,
            onClick = onGenreSelected
        )
        GenreChip(
            genre = ViewportGenre.Thriller,
            selected = selectedGenre == ViewportGenre.Thriller,
            x = 127.07f,
            y = 44.52f,
            scale = scale,
            onClick = onGenreSelected
        )
    }
}

@Composable
private fun GenreChip(
    genre: ViewportGenre,
    selected: Boolean,
    x: Float,
    y: Float,
    width: Float = GENRE_TEXT_CHIP_SIZE,
    height: Float = GENRE_TEXT_CHIP_SIZE,
    scale: Float,
    onClick: (ViewportGenre) -> Unit
) {
    val shape = RoundedCornerShape(viewportDp(2.707f, scale))
    val ink = if (selected) ViewportSelectedInk else ViewportInactive

    Box(
        modifier = Modifier
            .absoluteOffset(x = viewportDp(x, scale), y = viewportDp(y, scale))
            .width(viewportDp(width, scale))
            .height(viewportDp(height, scale))
            .clip(shape)
            .background(if (selected) ViewportSelectedFill else Color(0xFF303030).copy(alpha = 0.5f))
            .border(viewportDp(1.805f, scale), ViewportStroke.copy(alpha = if (selected) 0f else 0.5f), shape)
            .clickable { onClick(genre) },
        contentAlignment = Alignment.Center
    ) {
        if (genre == ViewportGenre.All) {
            AllGenreGlyph(color = ink, scale = scale)
        } else {
            Text(
                text = genre.compactLabel,
                color = ink,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 11.sp,
                letterSpacing = 0.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ViewportModeCluster(
    selectedMode: ViewportMode,
    scale: Float,
    onModeSelected: (ViewportMode) -> Unit
) {
    Box(
        modifier = Modifier
            .width(viewportDp(MODE_GROUP_WIDTH, scale))
            .height(viewportDp(MODE_GROUP_HEIGHT, scale))
    ) {
        ViewportModeButton(
            mode = ViewportMode.Collage,
            selected = selectedMode == ViewportMode.Collage,
            x = 9f,
            y = 3.46f,
            scale = scale,
            onClick = onModeSelected
        )
        ViewportModeButton(
            mode = ViewportMode.Grid,
            selected = selectedMode == ViewportMode.Grid,
            x = 72f,
            y = 3.46f,
            scale = scale,
            onClick = onModeSelected
        )
        ViewportModeButton(
            mode = ViewportMode.List,
            selected = selectedMode == ViewportMode.List,
            x = 135f,
            y = 3.46f,
            scale = scale,
            onClick = onModeSelected
        )
    }
}

@Composable
private fun ViewportModeButton(
    mode: ViewportMode,
    selected: Boolean,
    x: Float,
    y: Float,
    scale: Float,
    onClick: (ViewportMode) -> Unit
) {
    val shape = RoundedCornerShape(viewportDp(6.923f, scale))
    val color = if (selected) ViewportSelectedInk else ViewportInactive

    Box(
        modifier = Modifier
            .absoluteOffset(x = viewportDp(x, scale), y = viewportDp(y, scale))
            .size(viewportDp(MODE_BUTTON_SIZE, scale))
            .clip(shape)
            .background(if (selected) ViewportSelectedFill else ViewportPanelFill)
            .border(viewportDp(3.462f, scale), ViewportStroke.copy(alpha = if (selected) 0f else 0.42f), shape)
            .clickable { onClick(mode) },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(viewportDp(39.462f, scale))
                    .border(
                        viewportDp(3.462f, scale),
                        ViewportSelectedInk,
                        RoundedCornerShape(viewportDp(0.692f, scale))
                    )
            )
        }
        ViewportModeGlyph(mode = mode, color = color, scale = scale)
    }
}

@Composable
private fun AllGenreGlyph(color: Color, scale: Float) {
    Box(
        modifier = Modifier
            .size(viewportDp(29.681f, scale))
            .border(
                viewportDp(1.855f, scale),
                color,
                RoundedCornerShape(viewportDp(0.928f, scale))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(viewportDp(1.8f, scale)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(3) {
                Row(horizontalArrangement = Arrangement.spacedBy(viewportDp(1.8f, scale))) {
                    repeat(3) {
                        ViewportGlyphCell(
                            width = viewportDp(3.9f, scale),
                            height = viewportDp(3.9f, scale),
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewportModeGlyph(
    mode: ViewportMode,
    color: Color,
    scale: Float
) {
    when (mode) {
        ViewportMode.Collage -> CollageGlyph(color = color, scale = scale)
        ViewportMode.Grid -> GridGlyph(color = color, scale = scale)
        ViewportMode.List -> ListGlyph(color = color, scale = scale)
    }
}

@Composable
private fun CollageGlyph(color: Color, scale: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(viewportDp(6.8f, scale))) {
        Row(horizontalArrangement = Arrangement.spacedBy(viewportDp(6.8f, scale))) {
            ViewportGlyphCell(viewportDp(8f, scale), viewportDp(8f, scale), color)
            ViewportGlyphCell(viewportDp(8f, scale), viewportDp(8f, scale), color)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(viewportDp(6.8f, scale))) {
            ViewportGlyphCell(viewportDp(8f, scale), viewportDp(8f, scale), color)
            ViewportGlyphCell(viewportDp(8f, scale), viewportDp(8f, scale), color)
        }
    }
}

@Composable
private fun GridGlyph(color: Color, scale: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(viewportDp(4f, scale))) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(viewportDp(4f, scale))) {
                repeat(3) {
                    ViewportGlyphCell(viewportDp(7f, scale), viewportDp(7f, scale), color)
                }
            }
        }
    }
}

@Composable
private fun ListGlyph(color: Color, scale: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(viewportDp(7f, scale))) {
        repeat(3) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(viewportDp(5f, scale)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ViewportGlyphCell(viewportDp(6f, scale), viewportDp(6f, scale), color)
                ViewportGlyphCell(viewportDp(24f, scale), viewportDp(4f, scale), color)
            }
        }
    }
}

@Composable
private fun ViewportGlyphCell(
    width: Dp,
    height: Dp,
    color: Color
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(1.dp))
            .background(color)
    )
}

private fun viewportDp(px: Float, scale: Float): Dp = (px * scale).dp
