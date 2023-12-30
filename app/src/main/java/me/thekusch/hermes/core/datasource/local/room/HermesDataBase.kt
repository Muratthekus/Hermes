package me.thekusch.hermes.core.datasource.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import me.thekusch.hermes.core.datasource.local.room.dao.ChatDao
import me.thekusch.hermes.core.datasource.local.room.dao.MessageDao
import me.thekusch.hermes.core.datasource.local.room.dao.UserDao
import me.thekusch.hermes.core.datasource.local.room.tables.ChatEntity
import me.thekusch.hermes.core.datasource.local.room.tables.MessageEntity
import me.thekusch.hermes.core.datasource.local.room.tables.UserChatCrossRef
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfoEntity

@Database(
    entities = [
        UserInfoEntity::class,
        MessageEntity::class,
        ChatEntity::class,
        UserChatCrossRef::class
    ],
    version = 1
)
abstract class HermesDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun chatDao(): ChatDao

    abstract fun messageDao(): MessageDao
}