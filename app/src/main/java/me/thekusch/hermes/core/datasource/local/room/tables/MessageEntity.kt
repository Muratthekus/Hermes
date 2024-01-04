package me.thekusch.hermes.core.datasource.local.room.tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "message_entity", foreignKeys = [
        ForeignKey(
            entity = UserInfoEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("senderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ), ForeignKey(
            entity = ChatEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chatId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: Int,
    val senderId: Long,
    val chatId: Long,
    val content: String?,
    val voiceRecordPath: String?,
    val createdAt: Long,
    val seen: Boolean
)