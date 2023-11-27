package me.thekusch.hermes.core.datasource.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import me.thekusch.hermes.core.datasource.local.room.dao.UserDao
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfo

@Database(entities = [UserInfo::class], version = 1)
abstract class HermesDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
}