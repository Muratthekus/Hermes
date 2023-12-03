package me.thekusch.hermes.login.domain

import me.thekusch.hermes.core.domain.SessionManager
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {

    suspend fun login(
        email: String,
        password: String
    ) = sessionManager.login(email, password)
}