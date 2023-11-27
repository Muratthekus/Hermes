package me.thekusch.hermes.signup.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import me.thekusch.hermes.core.datasource.local.HermesLocalDataSource
import me.thekusch.hermes.core.datasource.supabase.Supabase
import me.thekusch.hermes.signup.ui.info.SignUpUiState
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val supabase: Supabase,
    private val hermesLocalDataSource: HermesLocalDataSource
) {

    suspend fun signUpUser(
        email: String,
        password: String,
        name: String
    ) {
        withContext(Dispatchers.IO) {
            val signUpResult = supabase.signupUser(email, password)

            hermesLocalDataSource.apply {
                this.email = signUpResult
                this.password = password
                this.name = name
            }
        }

    }
}