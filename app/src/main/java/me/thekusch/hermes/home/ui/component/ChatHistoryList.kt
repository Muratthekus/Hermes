package me.thekusch.hermes.home.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.thekusch.hermes.home.domain.model.Chat

@Composable
fun UserChatHistoryList(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    chatList: List<Chat> = emptyList(),
    onClick: (Chat) -> Unit,
) {
    LazyColumn(modifier = modifier, state = state) {
        items(items = chatList, key = { it.id }) { chat ->
            BasicChatGateWayItem(chat = chat, onClick)
            Divider(color = Color.Gray)
        }
    }
}