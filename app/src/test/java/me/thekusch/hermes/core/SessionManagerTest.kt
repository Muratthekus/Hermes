package me.thekusch.hermes.core

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import me.thekusch.hermes.core.common.util.OtpRequestLimitException
import me.thekusch.hermes.core.datasource.UserRepository
import me.thekusch.hermes.core.datasource.local.cache.LocalCache
import me.thekusch.hermes.core.datasource.local.model.Result
import me.thekusch.hermes.core.datasource.local.room.tables.UserInfoEntity
import me.thekusch.hermes.core.datasource.supabase.Supabase
import me.thekusch.hermes.core.domain.SessionManager
import me.thekusch.hermes.core.domain.mapper.SessionMapper
import me.thekusch.hermes.core.worker.HermesWorkerManager
import me.thekusch.hermes.util.TestDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SessionManagerTest {

    @get:Rule
    val distpatcherRule = TestDispatcherRule()

    @MockK
    private lateinit var supabase: Supabase

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK(relaxUnitFun = true)
    private lateinit var localCache: LocalCache

    @MockK(relaxed = true)
    private lateinit var sessionMapper: SessionMapper

    @MockK
    private lateinit var workerManager: HermesWorkerManager

    @InjectMockKs
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `GIVEN login credentials, WHEN login success, THEN Result-Success should return`() =
        runTest {
            //GIVEN
            coEvery {
                supabase.signIn(any(), any())
            } returns true

            sessionManager.login("", "").test {
                //WHEN
                val result = awaitItem()
                //THEN
                assert(result is Result.Started)

                //WHEN
                val result2 = awaitItem()
                //THEN
                assert(result2 is Result.Success)

                awaitComplete()
            }
        }

    @Test
    fun `GIVEN login credentials, WHEN login throw error, THEN Result-Fail should return`() =
        runTest {
            //GIVEN
            coEvery {
                supabase.signIn(any(), any())
            } throws Exception("test Exception")

            sessionManager.login("", "").test {
                //WHEN
                val result = awaitItem()
                //THEN
                assert(result is Result.Started)

                //WHEN
                val result2 = awaitItem()
                //THEN
                assert(result2 is Result.Fail)

                awaitComplete()
            }
        }

    @Test
    fun `GIVEN signup credentials, WHEN otp limit exceeded, THEN OtpRequestLimitException should return`() = runTest {
        //GIVEN
        coEvery {
            supabase.signupUser(any(), any(), any())
        } just Runs

        every {
            localCache.isOtpRequestReachedThreshold()
        } returns true

        every {
            localCache.increaseOtpRequestWithInThreshold()
        } just Runs

        every {
            workerManager.safeStartOtpRequestWorker()
        } just Runs

        // WHEN
        val result = runCatching {
            sessionManager.signUpUser("", "", "")
        }

        // THEN
        verify {
            workerManager.safeStartOtpRequestWorker()
        }

        verify {
            localCache.isOtpRequestReachedThreshold()
        }

        verify {
            localCache.increaseOtpRequestWithInThreshold()
        }

        coVerify(exactly = 0) {
            supabase.signupUser(any(), any(), any())
        }

        assert(result.exceptionOrNull() is OtpRequestLimitException)

    }

    @Test
    fun `GIVEN signup credentials, WHEN otp limit not exceeded, supabase-signupUser should called`() = runTest {
        //GIVEN
        coEvery {
            supabase.signupUser(any(), any(), any())
        } just Runs

        every {
            localCache.isOtpRequestReachedThreshold()
        } returns false

        every {
            localCache.increaseOtpRequestWithInThreshold()
        } just Runs

        every {
            workerManager.safeStartOtpRequestWorker()
        } just Runs

        // WHEN
        val result = runCatching {
            sessionManager.signUpUser("", "", "")
        }

        verify {
            localCache.isOtpRequestReachedThreshold()
            workerManager.safeStartOtpRequestWorker()
            localCache.increaseOtpRequestWithInThreshold()
        }

        coVerify {
            supabase.signupUser(any(), any(), any())
        }

        assert(result.isSuccess)
    }

    @Test
    fun `GIVEN verify signup credentials, WHEN otp is correct, THEN Result-Success should return`() = runTest {
        coEvery {
            supabase.verifyEmailOtp(any(), any())
        } returns true

        every {
            workerManager.cancelOtpRequestWorker()
        } just Runs

        // WHEN && THEN
        sessionManager.verifySignUp("", "").test {
            val result = awaitItem()
            assert(result is Result.Started)
            val result2 = awaitItem()
            assert(result2 is Result.Success)
            awaitComplete()
        }

        verify {
            workerManager.cancelOtpRequestWorker()
        }
    }

    @Test
    fun `GIVEN verify signup credentials, WHEN otp is false, THEN Result-Retry should return`() = runTest {
        coEvery {
            supabase.verifyEmailOtp(any(), any())
        } returns false

        every {
            workerManager.cancelOtpRequestWorker()
        } just Runs

        // WHEN && THEN
        sessionManager.verifySignUp("", "").test {
            val result = awaitItem()
            assert(result is Result.Started)
            val result2 = awaitItem()
            assert(result2 is Result.Retry)
            awaitComplete()
        }

    }

    @Test
    fun `GIVEN verify signup credentials, WHEN supabase-verifyEmailOtp throw error, THEN Result-Fail should return`() = runTest {
        coEvery {
            supabase.verifyEmailOtp(any(), any())
        } throws Exception("test Exception")

        every {
            workerManager.cancelOtpRequestWorker()
        } just Runs

        // WHEN && THEN
        sessionManager.verifySignUp("", "").test {
            val result = awaitItem()
            assert(result is Result.Started)
            val result2 = awaitItem()
            assert(result2 is Result.Fail)
            awaitComplete()
        }

        verify(exactly = 0) {
            workerManager.cancelOtpRequestWorker()
        }

    }

    @Test
    fun `GIVEN resendOtp credentials, WHEN otp limit exceeded, THEN exception should thrown`() = runTest {
        //GIVEN
        coEvery {
            supabase.resendOtp(any())
        } just Runs

        every {
            localCache.isOtpRequestReachedThreshold()
        } returns true

        every {
            localCache.increaseOtpRequestWithInThreshold()
        } just Runs

        every {
            workerManager.safeStartOtpRequestWorker()
        } just Runs

        // WHEN
        val result = runCatching {
            sessionManager.resendOtp("")
        }

        verify {
            localCache.isOtpRequestReachedThreshold()
            workerManager.safeStartOtpRequestWorker()
            localCache.increaseOtpRequestWithInThreshold()
        }

        coVerify(exactly = 0) {
            supabase.resendOtp(any())
        }

        assert(result.isFailure)

        assert(result.exceptionOrNull() is OtpRequestLimitException)
    }

    @Test
    fun `GIVEN resendOtp credentials, WHEN otp limit not exceeded, THEN supabase-resendOtp should called`() = runTest {
        //GIVEN
        coEvery {
            supabase.resendOtp(any())
        } just Runs

        every {
            localCache.isOtpRequestReachedThreshold()
        } returns false

        every {
            localCache.increaseOtpRequestWithInThreshold()
        } just Runs

        every {
            workerManager.safeStartOtpRequestWorker()
        } just Runs

        // WHEN
        val result = runCatching {
            sessionManager.resendOtp("")
        }

        verify {
            localCache.isOtpRequestReachedThreshold()
            workerManager.safeStartOtpRequestWorker()
            localCache.increaseOtpRequestWithInThreshold()
        }

        coVerify {
            supabase.resendOtp(any())
        }

        assert(result.isSuccess)

    }

}