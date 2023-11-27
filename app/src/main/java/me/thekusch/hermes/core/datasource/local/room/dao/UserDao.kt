package me.thekusch.hermes.core.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfo

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userInfo: UserInfo)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateUser(userInfo: UserInfo)

    @Delete
    suspend fun deleteUser(userInfo: UserInfo)

    @Query("SELECT * FROM userInfo")
    suspend fun getUser(): List<UserInfo>
}