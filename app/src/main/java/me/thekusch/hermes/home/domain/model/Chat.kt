package me.thekusch.hermes.home.domain.model

import androidx.compose.runtime.Stable

@Stable
data class Chat(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val hasPendingMessages: Boolean = false,
    val slug: String
)