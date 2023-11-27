package me.thekusch.hermes.core.datasource.local.room.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject

@Entity(tableName = "userInfo")
data class UserInfo(
    @PrimaryKey val id: String,
    val email: String?,
    val name: String,
    val confirmedAt: Instant?,
    val createdAt: Instant?,
    val emailConfirmedAt: Instant?,
    val lastSignInAt: Instant?,
    val phone: String?,
    val updatedAt: Instant?,
    val userMetadata: JsonObject?
)