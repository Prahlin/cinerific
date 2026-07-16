package com.prahlin.cinerific.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalMovies
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.TheaterComedy
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.min

private const val NAV_STAGE_WIDTH = 1194f
private const val NAV_STAGE_HEIGHT = 834f
private const val NAV_RAIL_WIDTH = 115f
private const val NAV_OPEN_MS = 150
private const val HOME_AUTO_COLLAPSE_MS = 5000L

private val NavEaseOut = CubicBezierEasing(0f, 0f, 0.2f, 1f)
private val NavRail = Color(0xFF1F1F1F)
private val NavSelected = Color(0xFFE7E7E7)
private val NavInactive = Color(0xFF9A9A9A)

@Composable
internal fun CinerificRightSideNavBar(
    currentDestination: CinerificDestination,
    onDestinationSelected: (CinerificDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val scale = min(maxWidth.value / NAV_STAGE_WIDTH, maxHeight.value / NAV_STAGE_HEIGHT)
        val railWidth = navDp(NAV_RAIL_WIDTH, scale)
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
                .background(NavRail.copy(alpha = 0.82f))
                .clickable(enabled = !expanded) {
                    visualDestination = currentDestination
                    expanded = true
                }
        ) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Open navigation",
                tint = NavInactive,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .absoluteOffset(y = navDp(41f, scale))
                    .size(navDp(48f, scale))
                    .graphicsLayer { alpha = 1f - openProgress }
            )

            NavItemButton(
                destination = CinerificDestination.Home,
                label = "HOME",
                icon = Icons.Rounded.Home,
                top = 33f,
                iconSize = 39f,
                scale = scale,
                openProgress = openProgress,
                selected = visualDestination == CinerificDestination.Home,
                enabled = expanded,
                onClick = {
                    visualDestination = CinerificDestination.Home
                    expanded = false
                    onDestinationSelected(CinerificDestination.Home)
                }
            )
            NavItemButton(
                destination = CinerificDestination.Movies,
                label = "MOVIES",
                icon = Icons.Rounded.LocalMovies,
                top = 125.2f,
                iconSize = 36f,
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
                icon = Icons.Rounded.TheaterComedy,
                top = 202.31f,
                iconSize = 38f,
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
                icon = Icons.Rounded.StarBorder,
                top = 279.03f,
                iconSize = 36f,
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
                icon = Icons.Rounded.Settings,
                top = 724.27f,
                iconSize = 38f,
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
private fun NavItemButton(
    @Suppress("UNUSED_PARAMETER") destination: CinerificDestination,
    label: String,
    icon: ImageVector,
    top: Float,
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
            .absoluteOffset(y = navDp(top, scale))
            .fillMaxWidth()
            .height(navDp(66f, scale))
            .graphicsLayer { alpha = itemAlpha }
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label.lowercase().replaceFirstChar { it.uppercase() },
                tint = color,
                modifier = Modifier.size(navDp(iconSize, scale))
            )
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

private fun navDp(px: Float, scale: Float): Dp = (px * scale).dp
