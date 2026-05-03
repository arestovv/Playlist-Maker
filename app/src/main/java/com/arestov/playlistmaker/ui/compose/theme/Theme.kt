package com.arestov.playlistmaker.ui.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = YpBlue,
    onPrimary = YpWhite,
    secondary = YpBlue,
    onSecondary = YpWhite,
    background = YpWhite,
    onBackground = YpBlack,
    surface = YpWhite,
    onSurface = YpBlack,
    surfaceVariant = YpLightGray,
    onSurfaceVariant = YpBlack,
    outline = YpGray,
    error = YpRed,
)

private val DarkColors = darkColorScheme(
    primary = YpBlue,
    onPrimary = YpWhite,
    secondary = YpBlue,
    onSecondary = YpWhite,
    background = YpBlack,
    onBackground = YpWhite,
    surface = YpBlack,
    onSurface = YpWhite,
    surfaceVariant = YpBlack,
    onSurfaceVariant = YpWhite,
    outline = YpGray,
    error = YpRed,
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
