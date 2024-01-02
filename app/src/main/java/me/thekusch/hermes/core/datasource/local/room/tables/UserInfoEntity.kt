package me.thekusch.hermes.core.datasource.local.room.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject

@Entity(tableName = "userInfo")
data class UserInfoEntity(
    @PrimaryKey val id: String,
    val accountOwner: Boolean?,
    val email: String?,
    val name: String,
    val confirmedAt: Long?,
    val createdAt: Long?,
    val emailConfirmedAt: Long?,
    val lastSignInAt: Long?,
    val phone: String?,
    val updatedAt: Long?,
)