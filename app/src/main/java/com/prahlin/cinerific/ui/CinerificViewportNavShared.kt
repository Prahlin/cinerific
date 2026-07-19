package com.prahlin.cinerific.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val ViewportStroke = Color(0xFFE7E7E7)
internal val ViewportInactive = Color(0xFF8A828C)
internal val ViewportSelectedFill = Color(0xFFE7E7E7)
internal val ViewportSelectedInk = Color(0xFF2A2A2A)
internal val ViewportPanelFill = Color(0x221E1E1E)

internal const val GENRE_GROUP_WIDTH = 160.464f
internal const val GENRE_GROUP_HEIGHT = 77.913f
internal const val GENRE_TEXT_CHIP_SIZE = 33.391f
internal const val GENRE_ALL_CHIP_SIZE = 37.101f
internal const val MODE_GROUP_WIDTH = 190f
internal const val MODE_GROUP_HEIGHT = 58f
internal const val MODE_BUTTON_SIZE = 54.692f

@Composable
internal fun ViewportGlyphCell(
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

internal fun viewportDp(px: Float, scale: Float): Dp = (px * scale).dp
