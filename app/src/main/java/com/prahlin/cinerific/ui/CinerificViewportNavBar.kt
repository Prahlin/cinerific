package com.prahlin.cinerific.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
private val ViewportInactive = Color(0xFF8B858D)
private val ViewportSelectedFill = Color(0xFFE7E7E7)
private val ViewportSelectedInk = Color(0xFF2A2A2A)
private val ViewportPanelFill = Color(0x331E1E1E)

@Composable
internal fun CinerificViewportNavBar(
    selectedGenre: ViewportGenre,
    selectedMode: ViewportMode,
    onGenreSelected: (ViewportGenre) -> Unit,
    onModeSelected: (ViewportMode) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val chipGap = viewportDp(8f, scale)
    val modeGap = viewportDp(13f, scale)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(chipGap)) {
            Row(horizontalArrangement = Arrangement.spacedBy(chipGap)) {
                GenreChip(
                    genre = ViewportGenre.All,
                    selected = selectedGenre == ViewportGenre.All,
                    scale = scale,
                    onClick = onGenreSelected
                )
                GenreChip(
                    genre = ViewportGenre.Action,
                    selected = selectedGenre == ViewportGenre.Action,
                    scale = scale,
                    onClick = onGenreSelected
                )
                GenreChip(
                    genre = ViewportGenre.Comedy,
                    selected = selectedGenre == ViewportGenre.Comedy,
                    scale = scale,
                    onClick = onGenreSelected
                )
                GenreChip(
                    genre = ViewportGenre.Crime,
                    selected = selectedGenre == ViewportGenre.Crime,
                    scale = scale,
                    onClick = onGenreSelected
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(chipGap)) {
                GenreChip(
                    genre = ViewportGenre.Documentary,
                    selected = selectedGenre == ViewportGenre.Documentary,
                    scale = scale,
                    onClick = onGenreSelected
                )
                GenreChip(
                    genre = ViewportGenre.Drama,
                    selected = selectedGenre == ViewportGenre.Drama,
                    scale = scale,
                    onClick = onGenreSelected
                )
                GenreChip(
                    genre = ViewportGenre.Horror,
                    selected = selectedGenre == ViewportGenre.Horror,
                    scale = scale,
                    onClick = onGenreSelected
                )
                GenreChip(
                    genre = ViewportGenre.Thriller,
                    selected = selectedGenre == ViewportGenre.Thriller,
                    scale = scale,
                    onClick = onGenreSelected
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(modeGap)) {
            ViewportModeButton(
                mode = ViewportMode.Collage,
                selected = selectedMode == ViewportMode.Collage,
                scale = scale,
                onClick = onModeSelected
            )
            ViewportModeButton(
                mode = ViewportMode.Grid,
                selected = selectedMode == ViewportMode.Grid,
                scale = scale,
                onClick = onModeSelected
            )
            ViewportModeButton(
                mode = ViewportMode.List,
                selected = selectedMode == ViewportMode.List,
                scale = scale,
                onClick = onModeSelected
            )
        }
    }
}

@Composable
private fun GenreChip(
    genre: ViewportGenre,
    selected: Boolean,
    scale: Float,
    onClick: (ViewportGenre) -> Unit
) {
    val shape = RoundedCornerShape(viewportDp(5f, scale))
    val chipWidth = if (genre == ViewportGenre.All) viewportDp(36f, scale) else viewportDp(44f, scale)
    val chipHeight = viewportDp(32f, scale)
    val ink = if (selected) ViewportSelectedInk else ViewportStroke

    Box(
        modifier = Modifier
            .width(chipWidth)
            .height(chipHeight)
            .clip(shape)
            .background(if (selected) ViewportSelectedFill else ViewportPanelFill)
            .border(viewportDp(2f, scale), ViewportStroke.copy(alpha = if (selected) 1f else 0.65f), shape)
            .clickable { onClick(genre) },
        contentAlignment = Alignment.Center
    ) {
        if (genre == ViewportGenre.All) {
            AllGenreGlyph(color = ink, scale = scale)
        } else {
            Text(
                text = genre.compactLabel,
                color = ink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 12.sp,
                letterSpacing = 0.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ViewportModeButton(
    mode: ViewportMode,
    selected: Boolean,
    scale: Float,
    onClick: (ViewportMode) -> Unit
) {
    val shape = RoundedCornerShape(viewportDp(8f, scale))
    val color = if (selected) ViewportSelectedInk else ViewportInactive

    Box(
        modifier = Modifier
            .size(viewportDp(58f, scale))
            .clip(shape)
            .background(if (selected) ViewportSelectedFill else ViewportPanelFill)
            .border(viewportDp(3f, scale), ViewportStroke.copy(alpha = if (selected) 1f else 0.42f), shape)
            .clickable { onClick(mode) },
        contentAlignment = Alignment.Center
    ) {
        ViewportModeGlyph(mode = mode, color = color, scale = scale)
    }
}

@Composable
private fun AllGenreGlyph(color: Color, scale: Float) {
    Column(
        verticalArrangement = Arrangement.spacedBy(viewportDp(2f, scale)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(viewportDp(2f, scale))) {
                repeat(3) {
                    ViewportGlyphCell(
                        width = viewportDp(5f, scale),
                        height = viewportDp(5f, scale),
                        color = color
                    )
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
    Column(verticalArrangement = Arrangement.spacedBy(viewportDp(5f, scale))) {
        Row(horizontalArrangement = Arrangement.spacedBy(viewportDp(5f, scale))) {
            ViewportGlyphCell(viewportDp(11f, scale), viewportDp(11f, scale), color)
            ViewportGlyphCell(viewportDp(11f, scale), viewportDp(11f, scale), color)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(viewportDp(5f, scale))) {
            ViewportGlyphCell(viewportDp(11f, scale), viewportDp(11f, scale), color)
            ViewportGlyphCell(viewportDp(11f, scale), viewportDp(11f, scale), color)
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
