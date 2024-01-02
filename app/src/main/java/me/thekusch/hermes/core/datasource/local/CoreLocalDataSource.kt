package me.thekusch.hermes.core.datasource.local

import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.thekusch.hermes.core.datasource.local.room.HermesDataBase
import me.thekusch.hermes.core.datasource.local.room.dao.ChatDao
import me.thekusch.hermes.core.datasource.local.room.dao.MessageDao
import me.thekusch.hermes.core.datasource.local.room.dao.UserDao
import me.thekusch.hermes.core.datasource.local.room.tables.MessageEntity
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfoEntity
import javax.inject.Inject

class CoreLocalDataSource @Inject constructor(
    private val userDao: UserDao,
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val dataBase: HermesDataBase
) {

    suspend fun saveUserToDB(userInfo: UserInfoEntity) {
        userDao.insertUser(userInfo)
    }

    suspend fun getUserOrNull(): UserInfoEntity? {
        return userDao.getUser().firstOrNull()
    }

    suspend fun getChatMessagesWithCleanup(chatId: Long): List<MessageEntity>? {
        val cutoffTime = System.currentTimeMillis() - cutOffTime // 7 days ago

        return withContext(Dispatchers.IO) {
            dataBase.withTransaction {
                messageDao.deleteExpiredMessages(chatId, cutoffTime)

                // Return remaining messages
                return@withTransaction chatDao.getChatMessages(chatId)?.messages
            }
        }
    }

    suspend fun getChatParticipants(chatId: Long): List<UserInfoEntity>? {
        return withContext(Dispatchers.IO) {
            dataBase.withTransaction {
                chatDao.getChatParticipants(chatId)?.participants
            }
        }
    }

    companion object {
        private const val cutOffTime = 7 * 24 * 60 * 60 * 1000
    }
}