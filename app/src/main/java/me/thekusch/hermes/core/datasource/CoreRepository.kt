package me.thekusch.hermes.core.datasource

import me.thekusch.hermes.core.datasource.local.CoreLocalDataSource
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfoEntity
import javax.inject.Inject

class CoreRepository @Inject constructor(
    private val coreLocalDataSource: CoreLocalDataSource
) {
    suspend fun saveUserToDB(userInfo: UserInfoEntity) {
        coreLocalDataSource.saveUserToDB(userInfo)
    }

    suspend fun getUserOrNull() = coreLocalDataSource.getUserOrNull()
}