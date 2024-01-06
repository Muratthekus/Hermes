package me.thekusch.hermes.home.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.thekusch.hermes.core.common.util.getRandomColor
import me.thekusch.hermes.core.common.util.shortenString
import me.thekusch.hermes.home.domain.model.Chat


typealias ColumnScopeComposableView = @Composable ColumnScope.() -> Unit
typealias RowScopeComposableView = @Composable RowScope.() -> Unit

@Composable
fun ChatGateWayItemSkeleton(
    modifier: Modifier = Modifier,
    chatImage: RowScopeComposableView,
    chatInfo: RowScopeComposableView
) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        chatImage()
        chatInfo()
    }
}

@Composable
fun BasicChatGateWayItem(
    chat: Chat,
    onClick: (Chat) -> Unit
) {
    ChatGateWayItemSkeleton(
        modifier = Modifier.clickable { onClick(chat) },
        chatImage = {
            BasicChatDisplayImage(
                modifier = Modifier.weight(1f),
                chatSlug = chat.slug
            )
        }
    ) {
        ChatGatewayInfoWithMessages(modifier = Modifier.weight(5f), chat = chat)
    }
}

// TODO(murat) update `ChatGatewayInfoWithMessages` with latest chat message after DB update
@Composable
fun ChatGatewayInfoWithMessages(
    modifier: Modifier = Modifier,
    chat: Chat
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = chat.slug,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                text = chat.updatedAt,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BasicChatDisplayImage(
    modifier: Modifier = Modifier,
    chatSlug: String
) {

    val chatColor = getRandomColor()
    val shortenSlug = chatSlug.shortenString()

    Box(
        modifier = modifier
            .size(64.dp)
            .background(chatColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {


        Text(
            modifier = Modifier.padding(8.dp),
            text = shortenSlug,
            textAlign = TextAlign.Center,
            color = Color.White,
            style = MaterialTheme.typography.body1
        )

    }

}