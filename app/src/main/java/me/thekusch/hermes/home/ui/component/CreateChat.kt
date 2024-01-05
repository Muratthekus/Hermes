package me.thekusch.hermes.home.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.thekusch.messager.controller.AdvertiseStatus
import me.thekusch.messager.controller.BaseStatus

enum class CreateChatMethod {
    DISCOVER,
    ADVERTISE
}

@Composable
private fun CreateChatWithAdvertise(
    modifier: Modifier = Modifier,
    advertiseStatus: AdvertiseStatus,
    onDismiss: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        PulseLoading()

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 24.dp, end = 24.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 46.dp)
                .clickable { onDismiss() },
            text = "Let's wave to everyone around. Wait until someone wave back",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
private fun CreateSearchWithDiscover(
    modifier: Modifier = Modifier,
) {

}

@Composable
fun CreateChat(
    modifier: Modifier = Modifier,
    selectedMethod: CreateChatMethod,
    hermesState: BaseStatus,
    onDismiss: () -> Unit
) {
    if (selectedMethod == CreateChatMethod.ADVERTISE) {
        CreateChatWithAdvertise(
            advertiseStatus = hermesState as AdvertiseStatus,
            onDismiss = onDismiss
        )
        return
    }
    CreateSearchWithDiscover()
}

@Composable
fun CreateChatDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onCreateChatMethodSelect: (CreateChatMethod) -> Unit
) {
    var openDialog by remember { mutableStateOf(true) }
    if (openDialog) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {
                onDismiss()
                openDialog = false
            },
            title = {
                Text(text = "Create Chat")
            },
            text = {
                Text("Would you like to discorver or advertise")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateChatMethodSelect(CreateChatMethod.DISCOVER)
                        onDismiss()
                        openDialog = false
                    }) {
                    Text("Discover")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onCreateChatMethodSelect(CreateChatMethod.ADVERTISE)
                        onDismiss()
                        openDialog = false
                    }) {
                    Text("Advertiese")
                }
            }
        )
    }
}


@Composable
fun PulseLoading(
    durationMillis: Int = 1000,
    maxPulseSize: Float = 300f,
    minPulseSize: Float = 50f,
    pulseColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
    centreColor: Color = MaterialTheme.colors.onBackground
) {
    val infiniteTransition = rememberInfiniteTransition()
    val size by infiniteTransition.animateFloat(
        initialValue = minPulseSize,
        targetValue = maxPulseSize,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(128.dp)) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .size(size.dp)
                .align(Alignment.Center)
                .alpha(alpha),
            backgroundColor = pulseColor,
            elevation = 0.dp
        ) {}
        Card(
            modifier = Modifier
                .size(minPulseSize.dp)
                .align(Alignment.Center),
            shape = CircleShape,
            backgroundColor = centreColor
        ) {}
    }
}
