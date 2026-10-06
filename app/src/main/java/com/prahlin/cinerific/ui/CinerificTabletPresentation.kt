package com.prahlin.cinerific.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.prahlin.cinerific.R

private const val TABLET_MIN_WIDTH_DP = 600
private val TabletChrome = Color(0xF220001C)
private val TabletSelected = Color(0xFFE7E7E7)
private val TabletInactive = Color(0xFF777077)

internal fun isCinerificTablet(smallestScreenWidthDp: Int): Boolean =
    smallestScreenWidthDp >= TABLET_MIN_WIDTH_DP

/**
 * Tablet-only interaction layer over the legacy intro artwork. The Android View keeps drawing the
 * established design; these Compose targets exclusively own the three account actions so they can
 * never resolve as an avatar selection.
 */
@Composable
internal fun CinerificTabletIntroAccountActions(
    onCreateAccount: () -> Unit,
    onSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.screenHeightDp > configuration.screenWidthDp

    Box(modifier = modifier.fillMaxSize().zIndex(5f)) {
        if (isPortrait) {
            TabletIntroActionTarget(
                label = "Create My Account",
                onClick = onCreateAccount,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.82f)
                    .offset(y = (configuration.screenHeightDp * 0.635f).dp)
                    .height((configuration.screenHeightDp * 0.09f).dp)
            )
            TabletIntroActionTarget(
                label = "Sign In",
                onClick = onSignIn,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.70f)
                    .offset(y = (configuration.screenHeightDp * 0.71f).dp)
                    .height((configuration.screenHeightDp * 0.075f).dp)
            )
            TabletIntroActionTarget(
                label = "Forgot Password",
                onClick = onForgotPassword,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.82f)
                    .offset(y = (configuration.screenHeightDp * 0.785f).dp)
                    .height((configuration.screenHeightDp * 0.085f).dp)
            )
        } else {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .offset(y = (configuration.screenHeightDp * 0.77f).dp)
                    .height((configuration.screenHeightDp * 0.13f).dp)
            ) {
                TabletIntroActionTarget(
                    label = "Create My Account",
                    onClick = onCreateAccount,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                TabletIntroActionTarget(
                    label = "Sign In",
                    onClick = onSignIn,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                TabletIntroActionTarget(
                    label = "Forgot Password",
                    onClick = onForgotPassword,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun TabletIntroActionTarget(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clickable(
            role = Role.Button,
            onClickLabel = label,
            onClick = onClick
        )
    )
}

@Composable
internal fun BoxScope.CinerificTabletNavigation(
    currentDestination: CinerificDestination,
    portraitHiddenFraction: Float,
    onPortraitBarHeightChanged: (Float) -> Unit,
    onDestinationSelected: (CinerificDestination) -> Unit
) {
    val configuration = LocalConfiguration.current
    if (configuration.screenHeightDp > configuration.screenWidthDp) {
        CinerificTabletBottomNavigation(
            currentDestination = currentDestination,
            portraitHiddenFraction = portraitHiddenFraction,
            onPortraitBarHeightChanged = onPortraitBarHeightChanged,
            onDestinationSelected = onDestinationSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    } else {
        CinerificTabletSideNavigation(
            currentDestination = currentDestination,
            onDestinationSelected = onDestinationSelected,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun CinerificTabletBottomNavigation(
    currentDestination: CinerificDestination,
    portraitHiddenFraction: Float,
    onPortraitBarHeightChanged: (Float) -> Unit,
    onDestinationSelected: (CinerificDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val navigationBarBottom = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
    val contentHeight = 90.dp
    val totalHeight = contentHeight + navigationBarBottom
    val totalHeightPx = with(density) { totalHeight.toPx() }

    LaunchedEffect(totalHeightPx) {
        onPortraitBarHeightChanged(totalHeightPx)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
            .graphicsLayer {
                translationY = totalHeightPx * portraitHiddenFraction.coerceIn(0f, 1f)
            }
            .background(TabletChrome)
            .padding(bottom = navigationBarBottom)
            .zIndex(10f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabletDestinations.forEach { item ->
            TabletNavigationItem(
                item = item,
                selected = tabletVisualDestination(currentDestination) == item.destination,
                onClick = { onDestinationSelected(item.destination) },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}

@Composable
private fun CinerificTabletSideNavigation(
    currentDestination: CinerificDestination,
    onDestinationSelected: (CinerificDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val navigationBarBottom = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
    var expanded by remember { mutableStateOf(currentDestination != CinerificDestination.Home) }

    LaunchedEffect(currentDestination) {
        expanded = currentDestination != CinerificDestination.Home
    }

    Column(
        modifier = modifier
            .width(115.dp)
            .fillMaxHeight()
            .background(if (expanded) TabletChrome else Color.Transparent)
            .padding(bottom = navigationBarBottom)
            .zIndex(10f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = if (expanded) "Home" else "Open navigation"
                ) {
                    if (expanded) {
                        onDestinationSelected(CinerificDestination.Home)
                    } else {
                        expanded = true
                    }
                },
            contentAlignment = Alignment.TopCenter
        ) {
            if (expanded) {
                TabletNavigationItemContent(
                    item = TabletDestinations.first(),
                    selected = currentDestination == CinerificDestination.Home,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Open navigation",
                    tint = TabletInactive,
                    modifier = Modifier.padding(top = 22.dp).size(48.dp)
                )
            }
        }

        TabletDestinations.drop(1).dropLast(1).forEach { item ->
            TabletNavigationItem(
                item = item,
                selected = currentDestination == item.destination,
                enabled = expanded,
                onClick = { onDestinationSelected(item.destination) },
                modifier = Modifier.fillMaxWidth().height(76.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TabletNavigationItem(
            item = TabletDestinations.last(),
            selected = currentDestination == CinerificDestination.Settings,
            enabled = expanded,
            onClick = { onDestinationSelected(CinerificDestination.Settings) },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )
    }
}

@Composable
private fun TabletNavigationItem(
    item: TabletNavigationItemSpec,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier.clickable(
            enabled = enabled,
            role = Role.Button,
            onClickLabel = item.label,
            onClick = onClick
        ),
        contentAlignment = Alignment.Center
    ) {
        if (enabled) {
            TabletNavigationItemContent(item = item, selected = selected)
        }
    }
}

@Composable
private fun TabletNavigationItemContent(
    item: TabletNavigationItemSpec,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (selected) TabletSelected else TabletInactive
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Image(
            painter = painterResource(item.iconResId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.size(34.dp)
        )
        Text(
            text = item.label.uppercase(),
            color = color,
            fontFamily = CinerificAppTextFontFamily,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
internal fun CinerificTabletSignOutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.74f)
                    .border(2.dp, Color(0xFFC86BE0), RoundedCornerShape(15.dp))
                    .background(Color(0xFF252126), RoundedCornerShape(12.dp))
                    .clickable(enabled = false, onClick = {})
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Sign out from\nCinerific?",
                    color = Color.White,
                    fontFamily = CinerificAppTextFontFamily,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF600878)),
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text("Yes", fontFamily = CinerificAppTextFontFamily, fontSize = 20.sp)
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A343C)),
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text("No", fontFamily = CinerificAppTextFontFamily, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

private fun tabletVisualDestination(destination: CinerificDestination): CinerificDestination =
    if (destination in TabletDestinations.map { it.destination }) destination else CinerificDestination.Home

private data class TabletNavigationItemSpec(
    val destination: CinerificDestination,
    val label: String,
    @DrawableRes val iconResId: Int
)

private val TabletDestinations = listOf(
    TabletNavigationItemSpec(CinerificDestination.Home, "Home", R.drawable.nav_icon_home),
    TabletNavigationItemSpec(CinerificDestination.Movies, "Movies", R.drawable.nav_icon_movies),
    TabletNavigationItemSpec(CinerificDestination.Shows, "Shows", R.drawable.nav_icon_shows),
    TabletNavigationItemSpec(CinerificDestination.Favorites, "Favorites", R.drawable.nav_icon_favorites),
    TabletNavigationItemSpec(CinerificDestination.Settings, "Settings", R.drawable.nav_icon_settings)
)
