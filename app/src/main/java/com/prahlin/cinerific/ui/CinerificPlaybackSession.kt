package com.prahlin.cinerific.ui

import androidx.compose.runtime.staticCompositionLocalOf

// Use this only for user-started full-title playback. Preview loops and home reels stay idle-loggable.
internal class CinerificPlaybackSessionController(
    val isUserInitiatedPlaybackActive: Boolean = false,
    private val onUserInitiatedPlaybackStarted: () -> Unit = {},
    private val finishUserInitiatedPlayback: () -> Unit = {}
) {
    fun onActionablePlayTapped() {
        onUserInitiatedPlaybackStarted()
    }

    fun onUserInitiatedPlaybackFinished() {
        finishUserInitiatedPlayback()
    }
}

internal val LocalCinerificPlaybackSessionController = staticCompositionLocalOf {
    CinerificPlaybackSessionController()
}
