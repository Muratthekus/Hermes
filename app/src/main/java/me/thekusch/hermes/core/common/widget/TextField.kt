package me.thekusch.hermes.core.common.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.thekusch.hermes.ui.theme.Black
import me.thekusch.hermes.ui.theme.BlackBlue
import me.thekusch.hermes.ui.theme.DarkGray
import me.thekusch.hermes.ui.theme.Error
import me.thekusch.hermes.ui.theme.LightGray
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
        errorLabelColor = Error
    )
}

@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    isEnabled: Boolean = true,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
        }
    }

    BasicTextField(
        modifier = modifier,
        enabled = isEnabled,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text, it.text.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        text = otpText
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String
) {
    val darkTheme = isSystemInDarkTheme()
    var shouldShowAsCircle = true
    val char = when {
        index == text.length -> "0"
        index > text.length -> ""
        else -> {
            shouldShowAsCircle = false
            text[index].toString()
        }
    }
    if (shouldShowAsCircle) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .background(
                    color = if (darkTheme) Black else LightGray,
                    shape = RoundedCornerShape(size = 24.dp)
                )
        )
    } else {
        Text(
            text = char,
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )
    }
}