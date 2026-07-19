package com.prahlin.cinerific.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
    CollageLarge,
    CollageSmall,
    List
}

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
        CinerificViewportGenreNav(
            selectedGenre = selectedGenre,
            scale = scale,
            onGenreSelected = onGenreSelected
        )

        Spacer(modifier = Modifier.weight(1f))

        CinerificViewportModeNav(
            selectedMode = selectedMode,
            scale = scale,
            onModeSelected = onModeSelected
        )
    }
}
