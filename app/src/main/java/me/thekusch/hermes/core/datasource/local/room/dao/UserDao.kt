package me.thekusch.hermes.core.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfoEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userInfo: UserInfoEntity)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateUser(userInfo: UserInfoEntity)

    @Delete
    suspend fun deleteUser(userInfo: UserInfoEntity)

    @Query("SELECT * FROM userInfo")
    suspend fun getUser(): List<UserInfoEntity>
}