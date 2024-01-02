package me.thekusch.hermes.signup.domain

import me.thekusch.hermes.core.domain.SessionManager
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {

    suspend fun signUpUser(
        email: String,
        password: String,
        name: String
    ) = sessionManager.signUpUser(email, password, name)
}