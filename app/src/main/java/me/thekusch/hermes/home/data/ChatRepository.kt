package me.thekusch.hermes.home.data

import me.thekusch.hermes.core.datasource.local.CoreLocalDataSource
import me.thekusch.hermes.core.datasource.local.room.tables.ChatEntity
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val coreLocalDataSource: CoreLocalDataSource
) {

    suspend fun getChatHistory(): List<ChatEntity>? {
        return coreLocalDataSource.getChatHistory()
    }

    suspend fun createNewChat(chatEntity: ChatEntity) {
        coreLocalDataSource.createNewChat(chatEntity)
    }
}