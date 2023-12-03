package me.thekusch.hermes.signup.domain

import me.thekusch.hermes.core.datasource.supabase.Supabase
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val supabase: Supabase,
) {

    suspend fun signUpUser(
        email: String,
        password: String,
        name: String
    ) {
        supabase.signupUser(email, password, name)

    }
}