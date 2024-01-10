package me.thekusch.hermes.login

import app.cash.turbine.test
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.thekusch.hermes.core.datasource.local.model.Result
import me.thekusch.hermes.login.domain.LoginUseCase
import me.thekusch.hermes.login.ui.LoginViewModel
import me.thekusch.hermes.util.TestDispatcherRule
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.thekusch.hermes.login.ui.LoginUiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    // Mock dependencies
    @MockK
    private lateinit var loginUseCase: LoginUseCase

    // Inject mocks into the ViewModel
    @InjectMockKs
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `GIVEN initial state, WHEN LoginViewModel initiated, THEN uiState should be Loading`() {
        val initialState = viewModel.loginUiState
        Truth.assertThat(initialState.value).isInstanceOf(LoginUiState.Init::class.java)
    }


    @Test
    fun `GIVEN info,WHEN login success, THEN ui state should be Success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password"

        coEvery { loginUseCase.login(any(), any()) } returns flowOf(Result.Started, Result.Success)

        // When
        viewModel.login(email, password)

        // Then
        viewModel.loginUiState.test {
            Truth.assertThat(awaitItem()).isEqualTo(LoginUiState.Success)
        }
    }

    @Test
    fun `GIVEN info, WHEN login fail, THEN ui state should be Error`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password"
        val errorMessage = "Login failed"
        val failResult = Result.Fail(Exception(errorMessage))

        coEvery { loginUseCase.login(any(), any()) } returns flowOf(Result.Started,  failResult)

        // Act
        viewModel.login(email, password)

        // Assert
        viewModel.loginUiState.test {
            Truth.assertThat(awaitItem()).isEqualTo(LoginUiState.Error(errorMessage))
        }
    }

}
