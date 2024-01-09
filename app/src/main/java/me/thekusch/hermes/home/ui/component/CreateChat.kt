package me.thekusch.hermes.home.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.thekusch.hermes.R
import me.thekusch.hermes.ui.theme.Error
import me.thekusch.hermes.ui.theme.Success
import me.thekusch.messager.controller.BaseStatus

enum class CreateChatMethod {
    DISCOVER,
    ADVERTISE
}

@Composable
private fun ConnectionRequest(
    status: BaseStatus,
    onConnectionAnswer: (
        accept: Boolean,
        connectionData: BaseStatus.ConnectionInitiated
    ) -> Unit
) {
    AnimatedVisibility(
        visible = status is BaseStatus.ConnectionInitiated,
        enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) + expandIn(),
        exit = fadeOut(),
    ) {
        val data = status as BaseStatus.ConnectionInitiated
        Column {
            Text(
                text = "User: ${data.endpointName} wants to meet you",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { onConnectionAnswer(true, data) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Success)
                ) {
                    Text(
                        text = "Accept",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary
                    )
                }

                Button(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { onConnectionAnswer(false, data) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Error)
                ) {
                    Text(
                        text = "Reject",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateChatWithAdvertise(
    modifier: Modifier = Modifier,
    advertiseStatus: BaseStatus,
    onDismiss: () -> Unit,
    onConnectionAnswer: (accept: Boolean, data: BaseStatus.ConnectionInitiated) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(
            visible = advertiseStatus is BaseStatus.WavingStarting,
            enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) + expandIn(),
            exit = fadeOut(),
        ) {
            PulseLoading()
        }

        ConnectionRequest(
            status = advertiseStatus,
            onConnectionAnswer = onConnectionAnswer
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 24.dp, end = 24.dp)
                .padding(bottom = 46.dp)
                .clickable { onDismiss() },
            text = "Let's wave to everyone around. Wait until someone wave back",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )

        Image(
            modifier = Modifier
                .size(64.dp)
                .padding(top = 12.dp)
                .clickable { onDismiss() },
            painter = painterResource(id = R.drawable.ic_cancel),
            contentDescription = "cancel advertise"
        )
    }
}

@Composable
private fun CreateSearchWithDiscover(
    modifier: Modifier = Modifier,
    discoveryStatus: BaseStatus,
    onDismiss: () -> Unit,
    onClick: (BaseStatus.EndpointFound) -> Unit,
    onConnectionAnswer: (accept: Boolean, data: BaseStatus.ConnectionInitiated) -> Unit
) {
    val (endpointList, setEndPointList) = remember { mutableStateOf(listOf<BaseStatus.EndpointFound>()) }
    val lazyListState = rememberLazyListState()

    if (discoveryStatus is BaseStatus.EndpointLost) {
        setEndPointList(endpointList.filter { it.endpointId != discoveryStatus.endpointId })
    }

    if (discoveryStatus is BaseStatus.EndpointFound) {
        if (endpointList.any { it.endpointId == discoveryStatus.endpointId }.not())
            setEndPointList(endpointList + discoveryStatus)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(
            visible = discoveryStatus is BaseStatus.WavingStarting,
            enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) + expandIn(),
            exit = fadeOut(),
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PulseLoading()

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 24.dp, end = 24.dp)
                        .padding(bottom = 46.dp)
                        .clickable { onDismiss() },
                    text = "Let's look very is everyone",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onBackground
                )
            }
        }

        AnimatedVisibility(
            visible = endpointList.isNotEmpty() && (discoveryStatus is BaseStatus.ConnectionInitiated).not(),
            enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) + expandIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier.weight(5f),
                state = lazyListState,
            ) {
                items(endpointList, key = { it.endpointId }) { endpoint ->
                    DiscoveredChatGateWayItem(
                        endpointName = endpoint.endpointName
                    ) { onClick(endpoint) }
                }
            }
        }

        ConnectionRequest(status = discoveryStatus, onConnectionAnswer = onConnectionAnswer)

        if ((discoveryStatus is BaseStatus.Disconnected).not())
            Image(
                modifier = Modifier
                    .weight(1f)
                    .size(64.dp)
                    .padding(top = 12.dp)
                    .clickable { onDismiss() },
                painter = painterResource(id = R.drawable.ic_cancel),
                contentDescription = "cancel advertise"
            )
    }
}

@Composable
fun CreateChat(
    modifier: Modifier = Modifier,
    selectedMethod: CreateChatMethod,
    hermesState: BaseStatus,
    onDismiss: () -> Unit,
    onConnectionAnswer: (
        accept: Boolean,
        connectionData: BaseStatus.ConnectionInitiated
    ) -> Unit,
    onMakeRequest: ((BaseStatus.EndpointFound) -> Unit)? = null
) {
    if (selectedMethod == CreateChatMethod.ADVERTISE) {
        CreateChatWithAdvertise(
            advertiseStatus = hermesState,
            onDismiss = onDismiss,
            onConnectionAnswer = onConnectionAnswer
        )
        return
    }
    requireNotNull(onMakeRequest) { "onMakeRequest can not be null if Discovery selected" }
    CreateSearchWithDiscover(
        discoveryStatus = hermesState,
        onDismiss = onDismiss,
        onClick = onMakeRequest,
        onConnectionAnswer = onConnectionAnswer
    )
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
