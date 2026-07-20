package com.prahlin.cinerific.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import com.prahlin.cinerific.R

internal enum class ViewportGenre(
    @StringRes val compactLabelResId: Int?,
    @StringRes val displayNameResId: Int
) {
    All(null, R.string.genre_all),
    Action(R.string.genre_action_compact, R.string.genre_action),
    Comedy(R.string.genre_comedy_compact, R.string.genre_comedy),
    Crime(R.string.genre_crime_compact, R.string.genre_crime),
    Documentary(R.string.genre_documentary_compact, R.string.genre_documentary),
    Drama(R.string.genre_drama_compact, R.string.genre_drama),
    Horror(R.string.genre_horror_compact, R.string.genre_horror),
    Thriller(R.string.genre_thriller_compact, R.string.genre_thriller)
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
