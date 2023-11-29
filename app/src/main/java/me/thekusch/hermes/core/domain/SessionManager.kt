package me.thekusch.hermes.core.domain

import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.thekusch.hermes.core.datasource.CoreRepository
import me.thekusch.hermes.core.datasource.local.cache.HermesLocalDataSource
import me.thekusch.hermes.core.datasource.local.model.Result
import me.thekusch.hermes.core.datasource.supabase.Supabase
import me.thekusch.hermes.core.domain.mapper.SessionMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val supabase: Supabase,
    private val hermesLocalDataSource: HermesLocalDataSource,
    private val coreRepository: CoreRepository,
    private val sessionMapper: SessionMapper
) {

    // TODO(murat) impl EventBus
    val handler =
        CoroutineExceptionHandler { context, throwable ->

        }

    private val useCaseScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        observeSessionChanges()
    }

    private fun observeSessionChanges() {
        useCaseScope.launch(handler) {
            supabase.sessionStatus.collectLatest { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val userSession = status.session
                        val userName = hermesLocalDataSource.name

                        val data = sessionMapper.mapOnVerifyOtpSuccess(
                            userSession, userName
                        )

                        data?.let {
                            coreRepository.saveUserToDB(it)
                        } ?: kotlin.run {
                            // TODO(murat) impl else block
                        }

                    }

                    else -> {
                        // no-op
                    }
                }
            }
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            val user = coreRepository.getUserOrNull()
            user != null
        }
    }

    suspend fun verifySignUp(
        email: String,
        otp: String
    ): Flow<Result> = flow {
        emit(Result.Started)
        val result = supabase.verifyEmailOtp(email, otp)

        if (result)
            emit(Result.Success)
        else
            emit(Result.Retry)
    }.catch {
        emit(Result.Fail(it))
    }
}