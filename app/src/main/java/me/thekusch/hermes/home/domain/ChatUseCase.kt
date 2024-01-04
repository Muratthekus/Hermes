package me.thekusch.hermes.home.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.thekusch.hermes.home.data.ChatRepository
import me.thekusch.hermes.home.domain.mapper.ChatMapper
import me.thekusch.hermes.home.domain.model.Chat
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val chatMapper: ChatMapper
) {

    suspend fun getChatHistory(): List<Chat> {
        return withContext(Dispatchers.IO) {
            val chatHistory = chatRepository.getChatHistory()

            chatHistory?.map(chatMapper::mapOnGetChatHistory) ?: run {
                emptyList()
            }

        }
    }
}