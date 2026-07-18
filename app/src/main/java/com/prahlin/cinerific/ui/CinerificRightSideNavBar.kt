package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prahlin.cinerific.R
import kotlinx.coroutines.delay
import kotlin.math.min

private const val NAV_STAGE_WIDTH = 1194f
private const val NAV_STAGE_HEIGHT = 834f
private const val NAV_RAIL_WIDTH = 115f
private const val NAV_TOGGLE_HEIGHT = 50f
private const val NAV_TOGGLE_ICON_SIZE = 48f
private const val NAV_ICON_CENTER_GAP = 75f
private const val NAV_HOME_ICON_SIZE = 39f
private const val NAV_MOVIES_ICON_SIZE = 36f
private const val NAV_SHOWS_ICON_SIZE = 38f
private const val NAV_FAVORITES_ICON_SIZE = 36f
private const val NAV_SETTINGS_ICON_SIZE = 38f
private const val NAV_HOME_ICON_WIDTH = 39f
private const val NAV_HOME_ICON_HEIGHT = 35f
private const val NAV_MOVIES_ICON_WIDTH = 35f
private const val NAV_MOVIES_ICON_HEIGHT = 21f
private const val NAV_SHOWS_ICON_WIDTH = 39f
private const val NAV_SHOWS_ICON_HEIGHT = 21f
private const val NAV_FAVORITES_ICON_WIDTH = 32f
private const val NAV_FAVORITES_ICON_HEIGHT = 30f
private const val NAV_SETTINGS_ICON_WIDTH = 33f
private const val NAV_SETTINGS_ICON_HEIGHT = 33f
private const val NAV_LABEL_LINE_HEIGHT = 15f
private const val NAV_EDGE_GAP = NAV_ICON_CENTER_GAP - (NAV_TOGGLE_ICON_SIZE + NAV_HOME_ICON_SIZE) / 2f
private const val NAV_OPEN_MS = 150
private const val NAV_ROTATION_MS = 200
private const val HOME_AUTO_COLLAPSE_MS = 5000L

private val NavFrameDestinations = setOf(
    CinerificDestination.Home,
    CinerificDestination.Movies,
    CinerificDestination.Shows,
    CinerificDestination.Favorites,
    CinerificDestination.Settings
)

private val NavEaseOut = CubicBezierEasing(0f, 0f, 0.2f, 1f)
private val NavRotationEase = CubicBezierEasing(0.2f, 0f, 0.2f, 1f)
private val NavSelected = Color(0xFFE7E7E7)
private val NavInactive = Color(0xFF9A9A9A)

private data class NavIconAsset(
    @DrawableRes val resId: Int,
    val width: Float,
    val height: Float
)

@Composable
internal fun CinerificRightSideNavBar(
    currentDestination: CinerificDestination,
    onDestinationSelected: (CinerificDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val scale = min(maxWidth.value / NAV_STAGE_WIDTH, maxHeight.value / NAV_STAGE_HEIGHT)
        val density = LocalDensity.current
        val statusBarTop = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
        val navigationBarBottom = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        val railWidth = navDp(NAV_RAIL_WIDTH, scale)
        val toggleTop = statusBarTop + navDp(
            NAV_EDGE_GAP - (NAV_TOGGLE_HEIGHT - NAV_TOGGLE_ICON_SIZE) / 2f,
            scale
        )
        val settingsContentHeight = navDp(NAV_SETTINGS_ICON_SIZE, scale) +
            with(density) { NAV_LABEL_LINE_HEIGHT.sp.toDp() }
        val settingsTop = maxHeight - navigationBarBottom - navDp(NAV_EDGE_GAP, scale) - settingsContentHeight
        var expanded by remember { mutableStateOf(currentDestination != CinerificDestination.Home) }
        var visualDestination by remember { mutableStateOf(currentDestination) }
        val openProgress by animateFloatAsState(
            targetValue = if (expanded) 1f else 0f,
            animationSpec = tween(durationMillis = NAV_OPEN_MS, easing = NavEaseOut),
            label = "right-nav-open"
        )

        LaunchedEffect(currentDestination) {
            visualDestination = currentDestination
            if (currentDestination != CinerificDestination.Home) {
                expanded = true
            }
        }

        LaunchedEffect(expanded, currentDestination) {
            if (expanded && currentDestination == CinerificDestination.Home) {
                delay(HOME_AUTO_COLLAPSE_MS)
                expanded = false
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(railWidth)
                .fillMaxHeight()
        ) {
            CinerificChromeBackground(
                modifier = Modifier.fillMaxSize(),
                alpha = openProgress
            )

            NavToggleButton(
                expanded = expanded,
                top = toggleTop,
                scale = scale,
                onClick = {
                    visualDestination = currentDestination
                    if (visualDestination !in NavFrameDestinations) {
                        visualDestination = CinerificDestination.Home
                    }
                    expanded = !expanded
                }
            )

            NavItemButton(
                destination = CinerificDestination.Home,
                label = "HOME",
                icon = NavIconAsset(
                    resId = R.drawable.nav_icon_home,
                    width = NAV_HOME_ICON_WIDTH,
                    height = NAV_HOME_ICON_HEIGHT
                ),
                top = navStackItemTop(toggleTop, index = 1, iconSize = NAV_HOME_ICON_SIZE, scale = scale),
                iconSize = NAV_HOME_ICON_SIZE,
                scale = scale,
                openProgress = openProgress,
                selected = visualDestination == CinerificDestination.Home,
                enabled = expanded,
                onClick = {
                    visualDestination = CinerificDestination.Home
                    expanded = true
                    onDestinationSelected(CinerificDestination.Home)
                }
            )
            NavItemButton(
                destination = CinerificDestination.Movies,
                label = "MOVIES",
                icon = NavIconAsset(
                    resId = R.drawable.nav_icon_movies,
                    width = NAV_MOVIES_ICON_WIDTH,
                    height = NAV_MOVIES_ICON_HEIGHT
                ),
                top = navStackItemTop(toggleTop, index = 2, iconSize = NAV_MOVIES_ICON_SIZE, scale = scale),
                iconSize = NAV_MOVIES_ICON_SIZE,
                scale = scale,
                openProgress = openProgress,
                selected = visualDestination == CinerificDestination.Movies,
                enabled = expanded,
                onClick = {
                    visualDestination = CinerificDestination.Movies
                    expanded = true
                    onDestinationSelected(CinerificDestination.Movies)
                }
            )
            NavItemButton(
                destination = CinerificDestination.Shows,
                label = "SHOWS",
                icon = NavIconAsset(
                    resId = R.drawable.nav_icon_shows,
                    width = NAV_SHOWS_ICON_WIDTH,
                    height = NAV_SHOWS_ICON_HEIGHT
                ),
                top = navStackItemTop(toggleTop, index = 3, iconSize = NAV_SHOWS_ICON_SIZE, scale = scale),
                iconSize = NAV_SHOWS_ICON_SIZE,
                scale = scale,
                openProgress = openProgress,
                selected = visualDestination == CinerificDestination.Shows,
                enabled = expanded,
                onClick = {
                    visualDestination = CinerificDestination.Shows
                    expanded = true
                    onDestinationSelected(CinerificDestination.Shows)
                }
            )
            NavItemButton(
                destination = CinerificDestination.Favorites,
                label = "FAVORITES",
                icon = NavIconAsset(
                    resId = R.drawable.nav_icon_favorites,
                    width = NAV_FAVORITES_ICON_WIDTH,
                    height = NAV_FAVORITES_ICON_HEIGHT
                ),
                top = navStackItemTop(toggleTop, index = 4, iconSize = NAV_FAVORITES_ICON_SIZE, scale = scale),
                iconSize = NAV_FAVORITES_ICON_SIZE,
                scale = scale,
                openProgress = openProgress,
                selected = visualDestination == CinerificDestination.Favorites,
                enabled = expanded,
                onClick = {
                    visualDestination = CinerificDestination.Favorites
                    expanded = true
                    onDestinationSelected(CinerificDestination.Favorites)
                }
            )
            NavItemButton(
                destination = CinerificDestination.Settings,
                label = "SETTINGS",
                icon = NavIconAsset(
                    resId = R.drawable.nav_icon_settings,
                    width = NAV_SETTINGS_ICON_WIDTH,
                    height = NAV_SETTINGS_ICON_HEIGHT
                ),
                top = settingsTop,
                iconSize = NAV_SETTINGS_ICON_SIZE,
                scale = scale,
                openProgress = openProgress,
                selected = visualDestination == CinerificDestination.Settings,
                enabled = expanded,
                onClick = {
                    visualDestination = CinerificDestination.Settings
                    expanded = true
                    onDestinationSelected(CinerificDestination.Settings)
                }
            )
        }
    }
}

@Composable
private fun NavToggleButton(
    expanded: Boolean,
    top: Dp,
    scale: Float,
    onClick: () -> Unit
) {
    val rotationProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis = NAV_ROTATION_MS, easing = NavRotationEase),
        label = "right-nav-burger-rotation"
    )

    Box(
        modifier = Modifier
            .absoluteOffset(y = top)
            .fillMaxWidth()
            .height(navDp(NAV_TOGGLE_HEIGHT, scale))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = if (expanded) "Collapse navigation" else "Open navigation",
            tint = NavInactive,
            modifier = Modifier
                .size(navDp(NAV_TOGGLE_ICON_SIZE, scale))
                .graphicsLayer {
                    rotationZ = rotationProgress * 90f
                }
        )
    }
}

@Composable
private fun NavItemButton(
    @Suppress("UNUSED_PARAMETER") destination: CinerificDestination,
    label: String,
    icon: NavIconAsset,
    top: Dp,
    iconSize: Float,
    scale: Float,
    openProgress: Float,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) NavSelected else NavInactive
    val itemAlpha = openProgress * if (selected) 1f else 0.58f

    Box(
        modifier = Modifier
            .absoluteOffset(y = top)
            .fillMaxWidth()
            .height(navDp(66f, scale))
            .graphicsLayer { alpha = itemAlpha }
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(navDp(iconSize, scale)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon.resId),
                    contentDescription = label.lowercase().replaceFirstChar { it.uppercase() },
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(color),
                    modifier = Modifier
                        .width(navDp(icon.width, scale))
                        .height(navDp(icon.height, scale))
                )
            }
            Text(
                text = label,
                color = color,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                lineHeight = 15.sp,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

private fun navStackItemTop(toggleTop: Dp, index: Int, iconSize: Float, scale: Float): Dp {
    return toggleTop + navDp(NAV_TOGGLE_HEIGHT / 2f + NAV_ICON_CENTER_GAP * index - iconSize / 2f, scale)
}

private fun navDp(px: Float, scale: Float): Dp = (px * scale).dp
