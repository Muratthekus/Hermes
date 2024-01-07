package me.thekusch.hermes.home.domain.mapper

import me.thekusch.hermes.core.datasource.local.room.tables.ChatEntity
import me.thekusch.hermes.home.domain.model.Chat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class HomeMapper @Inject constructor() {

    fun mapOnGetChatHistory(entity: ChatEntity): Chat {
        return Chat(
            id = entity.id,
            createdAt = convertTimestampToString(entity.createdAt),
            updatedAt = convertTimestampToString(entity.updatedAt),
            hasPendingMessages = false,
            slug = entity.slug
        )
    }

    fun mapOnCreateNewChat(
        endpointId: String,
        endpointName: String
    ): ChatEntity {
        return ChatEntity(
            id = endpointId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            slug = endpointName
        )
    }

    private fun convertTimestampToString(timestamp: Long): String {
        val currentDate = Date()
        val inputDate = Date(timestamp)

        // Check if the date is today
        val isToday = isSameDay(currentDate, inputDate)

        return if (isToday) {
            // If it's today, return only the hours
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(inputDate)
        } else {
            // If it's not today, return day and month (shortened)
            val sdf = SimpleDateFormat("d MMM", Locale.getDefault())
            sdf.format(inputDate)
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(date1) == sdf.format(date2)
    }
}