package com.prahlin.cinerific.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
internal fun CinerificViewportGenreNav(
    selectedGenre: ViewportGenre,
    scale: Float,
    onGenreSelected: (ViewportGenre) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
                text = stringResource(genre.compactLabelResId ?: genre.displayNameResId),
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
