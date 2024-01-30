package me.thekusch.hermes.signup

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.test.runTest
import me.thekusch.hermes.signup.domain.SignUpUseCase
import me.thekusch.hermes.signup.ui.info.SignUpUiState
import me.thekusch.hermes.signup.ui.info.SignUpViewModel
import me.thekusch.hermes.util.TestDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignupViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    private lateinit var signUpUseCase: SignUpUseCase

    @InjectMockKs
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `GIVEN login credentials WHEN login usecase throw error, uistate should be error`() = runTest {

        coEvery { signUpUseCase.signUpUser(any(),any(),any()) } throws Exception("example exception")

        viewModel.signUpUser("email", "password", "password")

        assert(viewModel.signUpUiState.value is SignUpUiState.Error)

        val message = viewModel.signUpUiState.value as SignUpUiState.Error

        assert(message.message == "example exception")
    }

    @Test
    fun `GIVEN signup credentials WHEN login usecase return success, uistate should be success`() = runTest {
        coEvery { signUpUseCase.signUpUser(any(),any(),any()) } just Runs

        viewModel.signUpUser("email", "password", "password")

        assert(viewModel.signUpUiState.value is SignUpUiState.Success)
    }
}