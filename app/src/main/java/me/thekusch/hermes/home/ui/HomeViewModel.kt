package me.thekusch.hermes.home.ui

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.thekusch.hermes.home.domain.ChatUseCase
import me.thekusch.hermes.home.ui.component.CreateChatMethod
import me.thekusch.messager.Hermes
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase
) : ViewModel() {

    private val _homeUiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Init)

    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    private lateinit var hermes: Hermes


    //TODO(murat) set username, save if user rejected or accepted requests, observe permission changes
    // do not call Hermes.init in the onViewCreated
    fun initHermes(hermes: Hermes) {
        this.hermes = hermes
    }

    fun onPermissionNotGranted() {
        viewModelScope.launch {
            _homeUiState.value = HomeUiState.PermissionNotGranted
        }
    }

    fun getChatHistory() {
        viewModelScope.launch(chatExceptionHandler) {
            _homeUiState.value = HomeUiState.Loading

            val chatHistory = chatUseCase.getChatHistory()

            if (chatHistory.isEmpty()) {
                _homeUiState.value = HomeUiState.EmptyChat
                return@launch
            }

            _homeUiState.value = HomeUiState.Success(chatHistory)
        }
    }

    private val chatExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _homeUiState.value = HomeUiState.Error(throwable.localizedMessage)
    }

    fun createChat(
        method: CreateChatMethod
    ) {
        viewModelScope.launch(chatExceptionHandler) {
            _homeUiState.value = HomeUiState.CreateChat(method)
        }
    }
}