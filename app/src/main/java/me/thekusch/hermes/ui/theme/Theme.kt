package me.thekusch.hermes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = LightBlue,
    primaryVariant = Denee,
    background = DarkBlack,
    surface = DarkBlack,
    onBackground = White,
    onSurface = White,
    error = Error,
    onPrimary = White
)

private val LightColorPalette = lightColors(
    primary = Blue,
    primaryVariant = LightBlue,
    background = White,
    surface = White,
    onBackground = BlackBlue,
    onSurface = BlackBlue,
    error = Error,
    onPrimary = White
)

@Composable
fun HermesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    val colors = if (darkTheme) {
        systemUiController.setSystemBarsColor(
            color = DarkBlack
        )
        DarkColorPalette
    } else {
        systemUiController.setSystemBarsColor(
            color = White
        )
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = HermesTypography,
        content = content
    )
}