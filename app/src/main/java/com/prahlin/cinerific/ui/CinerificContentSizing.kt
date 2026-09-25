package com.prahlin.cinerific.ui

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

internal const val CINERIFIC_GENRE_HEADER_FONT_SIZE_SP = 36f

private const val PORTRAIT_CARD_WIDTH_TO_GENRE_TEXT_RATIO = 6.9f
private const val LANDSCAPE_CARD_WIDTH_TO_GENRE_TEXT_RATIO = 8.34f

internal fun cinerificLargeTitleCardWidth(
    density: Density,
    isPortrait: Boolean
): Dp {
    val ratio = if (isPortrait) {
        PORTRAIT_CARD_WIDTH_TO_GENRE_TEXT_RATIO
    } else {
        LANDSCAPE_CARD_WIDTH_TO_GENRE_TEXT_RATIO
    }
    return with(density) { CINERIFIC_GENRE_HEADER_FONT_SIZE_SP.sp.toDp() } * ratio
}
