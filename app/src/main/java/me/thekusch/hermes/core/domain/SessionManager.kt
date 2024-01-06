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
import me.thekusch.hermes.core.datasource.UserRepository
import me.thekusch.hermes.core.datasource.local.cache.LocalCache
import me.thekusch.hermes.core.datasource.local.model.Result
import me.thekusch.hermes.core.datasource.supabase.Supabase
import me.thekusch.hermes.core.domain.mapper.SessionMapper
import me.thekusch.hermes.core.domain.model.User
import me.thekusch.hermes.core.common.util.OtpRequestLimitException
import me.thekusch.hermes.core.worker.HermesWorkerManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val supabase: Supabase,
    private val userRepository: UserRepository,
    private val sessionMapper: SessionMapper,
    private val localCache: LocalCache,
    private val workerManager: HermesWorkerManager
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

                        val data = sessionMapper.mapOnVerifyOtpSuccess(
                            userSession
                        )

                        data?.let {
                            userRepository.saveUserToDB(it)
                        } ?: kotlin.run {
                            // TODO(murat) impl else block
                        }

                    }

                    is SessionStatus.NotAuthenticated -> {
                        // TODO(murat) send logout event with EventBus
                    }

                    else -> {
                        // no-op
                    }
                }
            }
        }
    }

    @Throws
    suspend fun signUpUser(
        email: String,
        password: String,
        name: String
    ) {
        workerManager.safeStartOtpRequestWorker()
        localCache.increaseOtpRequestWithInThreshold()
        if (localCache.isOtpRequestReachedThreshold()) {
            throw OtpRequestLimitException()
        }
        supabase.signupUser(email, password, name)
    }

    @Throws
    suspend fun resendOtp(email: String) {
        workerManager.safeStartOtpRequestWorker()
        localCache.increaseOtpRequestWithInThreshold()
        if (localCache.isOtpRequestReachedThreshold()) {
            throw OtpRequestLimitException()
        }
        withContext(Dispatchers.IO) {
            supabase.resendOtp(email)
        }
    }

    suspend fun getCurrentUser(): User? {
        return withContext(Dispatchers.IO) {
            val user = userRepository.getUserOrNull()
            user?.let { entity ->
                sessionMapper.mapOnGetUser(entity)
            } ?: kotlin.run {
                null
            }
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            val user = userRepository.getUserOrNull()
            user != null
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Flow<Result> = flow {
        emit(Result.Started)

        val result = supabase.signIn(email, password)

        if (result)
            emit(Result.Success)
        else
            emit(Result.Retry)
    }.catch {
        emit(Result.Fail(it))
    }

    suspend fun verifySignUp(
        email: String,
        otp: String
    ): Flow<Result> = flow {
        emit(Result.Started)
        val result = supabase.verifyEmailOtp(email, otp)

        if (result) {
            // cancel any running otp worker if signup process is finished
            workerManager.cancelOtpRequestWorker()
            emit(Result.Success)
        } else
            emit(Result.Retry)
    }.catch {
        emit(Result.Fail(it))
    }
}