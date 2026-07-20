package com.prahlin.cinerific.ui

import android.content.res.Configuration
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

internal enum class CinerificLanguage(val localeTag: String) {
    English("en"),
    Spanish("es"),
    Mandarin("zh-CN")
}

internal val LocalCinerificLanguage = staticCompositionLocalOf { CinerificLanguage.English }

@Composable
internal fun CinerificLocalizedResources(
    language: CinerificLanguage,
    content: @Composable () -> Unit
) {
    val baseContext = LocalContext.current
    val localizedConfiguration = remember(baseContext, language) {
        Configuration(baseContext.resources.configuration).apply {
            setLocales(LocaleList(Locale.forLanguageTag(language.localeTag)))
        }
    }
    val localizedContext = remember(baseContext, localizedConfiguration) {
        baseContext.createConfigurationContext(localizedConfiguration)
    }

    CompositionLocalProvider(
        LocalCinerificLanguage provides language,
        LocalConfiguration provides localizedConfiguration,
        LocalContext provides localizedContext,
        content = content
    )
}
