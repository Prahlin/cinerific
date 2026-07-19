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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
internal fun CinerificViewportModeNav(
    selectedMode: ViewportMode,
    scale: Float,
    onModeSelected: (ViewportMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(viewportDp(MODE_GROUP_WIDTH, scale))
            .height(viewportDp(MODE_GROUP_HEIGHT, scale))
    ) {
        ViewportModeButton(
            mode = ViewportMode.CollageLarge,
            selected = selectedMode == ViewportMode.CollageLarge,
            x = 9f,
            y = 3.46f,
            scale = scale,
            onClick = onModeSelected
        )
        ViewportModeButton(
            mode = ViewportMode.CollageSmall,
            selected = selectedMode == ViewportMode.CollageSmall,
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
private fun ViewportModeGlyph(
    mode: ViewportMode,
    color: Color,
    scale: Float
) {
    when (mode) {
        ViewportMode.CollageLarge -> CollageLargeGlyph(color = color, scale = scale)
        ViewportMode.CollageSmall -> CollageSmallGlyph(color = color, scale = scale)
        ViewportMode.List -> ListGlyph(color = color, scale = scale)
    }
}

@Composable
private fun CollageLargeGlyph(color: Color, scale: Float) {
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
private fun CollageSmallGlyph(color: Color, scale: Float) {
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
