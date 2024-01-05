package me.thekusch.hermes.core.domain.mapper

import io.github.jan.supabase.gotrue.user.UserSession
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfoEntity
import me.thekusch.hermes.core.domain.model.User
import javax.inject.Inject

class SessionMapper @Inject constructor() {

    fun mapOnVerifyOtpSuccess(
        userSession: UserSession?
    ): UserInfoEntity? {
        userSession?.user?.let {
            with(it) {
                return UserInfoEntity(
                    id = id,
                    name = it.userMetadata?.get("userValidName").toString(),
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

    fun mapOnGetUser(userInfoEntity: UserInfoEntity): User {
        return User(
            id = userInfoEntity.id,
            name = userInfoEntity.name,
            accountOwner = userInfoEntity.accountOwner,
            email = userInfoEntity.email,
            confirmedAt = userInfoEntity.confirmedAt,
            createdAt = userInfoEntity.createdAt,
            emailConfirmedAt = userInfoEntity.emailConfirmedAt,
            lastSignInAt = userInfoEntity.lastSignInAt,
            phone = userInfoEntity.phone,
            updatedAt = userInfoEntity.updatedAt
        )
    }
}