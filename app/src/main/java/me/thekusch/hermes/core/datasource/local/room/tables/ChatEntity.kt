package me.thekusch.hermes.core.datasource.local.room.tables

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity("chat_entity")
data class ChatEntity(
    @PrimaryKey val id: String,
    val createdAt: Long,
    val updatedAt: Long,
    val hasPendingMessages: Boolean = false,
    val slug: String,
)

data class ChatMessages(
    @Embedded val chat: ChatEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "chatId"
    )
    val messages: List<MessageEntity>
)


@Entity(tableName = "chatParticipant")
data class ChatParticipant(
    @PrimaryKey val id: String,
    val email: String?,
    val name: String,
)

@Entity(tableName = "user_chat_cross_ref", primaryKeys = ["userId", "chatId"])
data class UserChatCrossRef(
    val userId: Long,
    val chatId: Long
)

data class ChatWithParticipants(
    @Embedded val chat: ChatEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = ChatParticipant::class,
        associateBy = Junction(
            value = UserChatCrossRef::class,
            parentColumn = "chatId",
            entityColumn = "userId"
        )
    )
    val participants: List<ChatParticipant>
)
