package me.thekusch.hermes.util.widget

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import me.thekusch.hermes.ui.theme.Black
import me.thekusch.hermes.ui.theme.BlackBlue
import me.thekusch.hermes.ui.theme.DarkGray
import me.thekusch.hermes.ui.theme.Error
import me.thekusch.hermes.ui.theme.White
import me.thekusch.hermes.ui.theme.WhiteVariant


@Composable
fun provideTextFieldColors(): TextFieldColors {

    val darkTheme = isSystemInDarkTheme()

    return TextFieldDefaults.textFieldColors(
        backgroundColor = if (darkTheme) Black else WhiteVariant,
        placeholderColor = if (darkTheme) WhiteVariant else DarkGray,
        textColor = if (darkTheme) White else BlackBlue,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = if (darkTheme) White else BlackBlue,
        errorCursorColor = if (darkTheme) White else BlackBlue,
        errorLabelColor = Error)
}