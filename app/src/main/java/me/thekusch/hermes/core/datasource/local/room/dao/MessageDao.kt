package me.thekusch.hermes.core.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("DELETE FROM message_entity WHERE chatId = :chatId AND createdAt <= :cutoffTime")
    suspend fun deleteExpiredMessages(chatId: String, cutoffTime: Long)

}