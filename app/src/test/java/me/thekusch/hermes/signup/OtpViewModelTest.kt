package me.thekusch.hermes.signup

import app.cash.turbine.test
import com.google.common.truth.Truth
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.thekusch.hermes.core.datasource.local.model.Result
import me.thekusch.hermes.signup.domain.OtpUseCase
import me.thekusch.hermes.signup.ui.otp.OtpInputUiState
import me.thekusch.hermes.signup.ui.otp.OtpInputViewModel
import me.thekusch.hermes.util.TestDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OtpViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private var otpUseCase: OtpUseCase = mockk(relaxed = true)

    private lateinit var otpViewModel: OtpInputViewModel

    @Before
    fun setup() {
       otpViewModel = spyk(
           OtpInputViewModel(
               otpUseCase = otpUseCase
           )
       )
    }

    @Test
    fun `GIVEN otp credentials WHEN otpUseCase success THEN uiState should be success`() = runTest {
        // GIVEN
        val email = "examplemail.com"
        val otpCredentials = "123456"

        coEvery {
            otpUseCase.verifySignUp(any(), any())
        } returns flowOf(Result.Started, Result.Success)

        // WHEN
        otpViewModel.verifyOtp(email, otpCredentials)

        // THEN
        otpViewModel.otpUiState.test {
            val state = awaitItem()
            Truth.assertThat(state).isEqualTo(OtpInputUiState.Success)
        }
    }

    @Test
    fun `GIVEN otp credentials WHEN otpUseCase throw errors THEN uiState should be error`() = runTest {
        // GIVEN
        val email = "examplemail.com"
        val otpCredentials = "123456"

        coEvery {
            otpUseCase.verifySignUp(any(), any())
        } throws Exception("error")

        // WHEN
        otpViewModel.verifyOtp(email, otpCredentials)

        // THEN
        otpViewModel.otpUiState.test {
            val state = awaitItem()
            Truth.assertThat(state).isInstanceOf(OtpInputUiState.Error::class.java)
            Truth.assertThat((state as OtpInputUiState.Error).message).isEqualTo("error")
        }
    }

    @Test
    fun `GIVEN otp credentials WHEN user try to verify again THEN previous should be cancelled and uiState should be success`() = runTest {
        // GIVEN
        val email = "examplemail.com"
        val otpCredentials = "123456"

        coEvery {
            otpUseCase.verifySignUp(any(), any())
        } returns flowOf(Result.Started, Result.Success)

        every {
            otpViewModel.resetUiState()
        } just Runs

        val field = OtpInputViewModel::class.java.getDeclaredField("verifyJob")
        field.isAccessible = true
        val verifyJobMock: Job = mockk(relaxed = true)
        field.set(otpViewModel, verifyJobMock)

        every {
            verifyJobMock.cancel()
        } just Runs


        // WHEN
        otpViewModel.verifyOtp(email, otpCredentials)


        verify(exactly = 1) {
            otpViewModel.resetUiState()
        }

        otpViewModel.otpUiState.test {
            Truth.assertThat(awaitItem()).isEqualTo(OtpInputUiState.Success)
        }

    }
}