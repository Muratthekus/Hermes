package me.thekusch.hermes.core.widget

import android.os.CountDownTimer
import androidx.compose.foundation.clickable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import java.util.concurrent.TimeUnit

@Composable
fun Timer(
    modifier: Modifier = Modifier,
    timeInMillis: Long,
    onFinishedText: String,
    onReSendCode: (() -> Unit)? = null,
) {
    val millisInFuture: Long = timeInMillis

    var timeData by remember {
        mutableStateOf(millisInFuture)
    }

    var isFinished by remember {
        mutableStateOf(false)
    }

    val countDownTimer =
        object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeData = millisUntilFinished
            }

            override fun onFinish() {
                isFinished = true
            }
        }

    DisposableEffect(key1 = "key") {
        countDownTimer.start()
        onDispose {
            countDownTimer.cancel()
        }
    }

    Text(
        modifier = modifier.clickable {
            if (isFinished) {
                onReSendCode?.invoke()
                with(countDownTimer) {
                    cancel()
                    start()
                }
                isFinished = isFinished.not()
            }
        },
        text = if (isFinished.not()) getAsMinutesAndSeconds(timeData) else onFinishedText,
        style = MaterialTheme.typography.subtitle2,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.onBackground
    )
}

private fun getAsMinutesAndSeconds(
    time: Long
): String {
    return String.format(
        "%02d : %02d",
        TimeUnit.MILLISECONDS.toMinutes(time),
        TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
    )
}
