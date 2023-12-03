package me.thekusch.hermes.core.datasource.local

import me.thekusch.hermes.core.datasource.local.room.dao.UserDao
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfoEntity
import javax.inject.Inject

class CoreLocalDataSource @Inject constructor(
    private val userDao: UserDao
) {

    suspend fun saveUserToDB(userInfo: UserInfoEntity) {
        userDao.insertUser(userInfo)
    }

    suspend fun getUserOrNull(): UserInfoEntity? {
        return userDao.getUser().firstOrNull()
    }
}