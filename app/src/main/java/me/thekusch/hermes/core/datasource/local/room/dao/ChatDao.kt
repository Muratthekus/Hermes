package me.thekusch.hermes.core.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import me.thekusch.hermes.core.datasource.local.room.tables.ChatEntity
import me.thekusch.hermes.core.datasource.local.room.tables.ChatMessages
import me.thekusch.hermes.core.datasource.local.room.tables.ChatWithParticipants
import me.thekusch.hermes.core.datasource.local.room.tables.UserChatCrossRef

@Dao
interface ChatDao {
    @Transaction
    @Query("SELECT * FROM chat_entity WHERE id = :chatId")
    suspend fun getChatMessages(chatId: Long): ChatMessages?

    @Transaction
    @Query("SELECT * FROM chat_entity WHERE id = :chatId")
    suspend fun getChatParticipants(chatId: Long): ChatWithParticipants?

    @Query("SELECT * FROM chat_entity")
    suspend fun getChats(): List<ChatEntity>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserToChatCrossRef(userChatCrossRef: UserChatCrossRef)
}