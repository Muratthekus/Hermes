package me.thekusch.hermes.signup.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.thekusch.hermes.core.datasource.local.cache.HermesLocalDataSource
import me.thekusch.hermes.core.datasource.supabase.Supabase
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
        hermesLocalDataSource.name = name
        supabase.signupUser(email, password)

    }
}