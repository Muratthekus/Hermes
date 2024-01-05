package me.thekusch.hermes.home.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.thekusch.hermes.core.domain.SessionManager
import me.thekusch.hermes.core.domain.model.User
import me.thekusch.hermes.home.data.ChatRepository
import me.thekusch.hermes.home.domain.mapper.HomeMapper
import me.thekusch.hermes.home.domain.model.Chat
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val homeMapper: HomeMapper,
    private val sessionManager: SessionManager
) {

    suspend fun getChatHistory(): List<Chat> {
        return withContext(Dispatchers.IO) {
            val chatHistory = chatRepository.getChatHistory()

            chatHistory?.map(homeMapper::mapOnGetChatHistory) ?: run {
                emptyList()
            }

        }
    }

    suspend fun getCurrentUser(): User? {
        return withContext(Dispatchers.IO) {
            sessionManager.getCurrentUser()
        }
    }
}