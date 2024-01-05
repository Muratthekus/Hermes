package me.thekusch.hermes.core.domain.model

data class User(
    val id: String,
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