package me.thekusch.hermes.core.domain.mapper

import io.github.jan.supabase.gotrue.user.UserSession
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfoEntity
import javax.inject.Inject

class SessionMapper @Inject constructor() {

    fun mapOnVerifyOtpSuccess(
        userSession: UserSession?,
        name: String?
    ): UserInfoEntity? {
        userSession?.user?.let {
            with(it) {
                return UserInfoEntity(
                    id = id,
                    name = name.orEmpty(),
                    accountOwner = true,
                    email = email.orEmpty(),
                    confirmedAt = confirmedAt?.epochSeconds,
                    createdAt = createdAt?.epochSeconds,
                    emailConfirmedAt = emailConfirmedAt?.epochSeconds,
                    lastSignInAt = lastSignInAt?.epochSeconds,
                    phone = phone,
                    updatedAt = updatedAt?.epochSeconds,
                )
            }

        } ?: kotlin.run {
            return null
        }

    }
}