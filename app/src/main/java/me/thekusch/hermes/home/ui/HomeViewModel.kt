package me.thekusch.hermes.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.thekusch.hermes.core.common.flow.withHistory
import me.thekusch.hermes.home.domain.HomeUseCase
import me.thekusch.hermes.home.ui.component.CreateChatMethod
import me.thekusch.messager.Hermes
import me.thekusch.messager.controller.AdvertiseStatus
import me.thekusch.messager.controller.BaseStatus
import me.thekusch.messager.controller.DiscoveryStatus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private val _homeUiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Init)

    private val _permissionState: MutableStateFlow<Boolean> =
        MutableStateFlow(true)

    private val _hermesState: MutableStateFlow<BaseStatus> =
        MutableStateFlow(BaseStatus.Initial)

    private val _errorState: MutableStateFlow<String> =
        MutableStateFlow("")

    // If Hermes fails in any reason we should rollback to previous HomeUiState
    private val _homeUiStateHistory = _homeUiState.withHistory(viewModelScope)

    val homeState: StateFlow<HomeState> = combine(
        _homeUiState,
        _permissionState,
        _errorState,
        _hermesState,
    ) { homeUiState, permissionUiState, errorState, hermesState ->

        when (hermesState) {
            is BaseStatus.Initial -> {
                // no-op
            }

            is AdvertiseStatus.FinishedWithError -> {
                _errorState.value = hermesState.exception.localizedMessage.orEmpty()
            }

            is DiscoveryStatus.DiscoveryFailed -> {
                _errorState.value = hermesState.exception.localizedMessage.orEmpty()
            }

            is BaseStatus.Dismissed -> {
                _homeUiStateHistory.value?.previous?.let {
                    _homeUiState.value = it
                }
            }

            else -> {
                val createChatMethod =
                    if (hermesState is AdvertiseStatus) CreateChatMethod.ADVERTISE
                    else CreateChatMethod.DISCOVER
                _homeUiState.value = HomeUiState.CreateChat(createChatMethod)
            }
        }

        HomeState(
            uiState = homeUiState,
            permissionUiState = permissionUiState,
            errorState = errorState,
            hermesState = hermesState
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, HomeState())

    private lateinit var hermes: Hermes

    //TODO(murat) save if user rejected or accepted requests, observe permission changes
    fun initHermes(hermes: Hermes) {
        viewModelScope.launch(homeExceptionHandler) {
            val user = async { homeUseCase.getCurrentUser() }.await()
            requireNotNull(user) { "User record can not be found thus Hermes couldn't initialize" }

            this@HomeViewModel.hermes = hermes
            this@HomeViewModel.hermes.apply {
                build()
                setUsername(user.name)
                advertiseStatusListener = {
                    _hermesState.value = it
                }
                discoveryStatusListener = {
                    _hermesState.value = it
                }
            }
        }
    }

    fun createChat(
        method: CreateChatMethod
    ) {
        if (method == CreateChatMethod.DISCOVER) {
            hermes.startDiscovery()
        }
        if (method == CreateChatMethod.ADVERTISE) {
            hermes.startAdvertising()
        }

    }

    fun dismissCreateChat(
        method: CreateChatMethod
    ) {
        if (method == CreateChatMethod.DISCOVER) {
            hermes.stopDiscovery()
        }
        if (method == CreateChatMethod.ADVERTISE) {
            hermes.stopAdvertising()
        }
    }

    fun getChatHistory() {
        viewModelScope.launch(homeExceptionHandler) {
            _homeUiState.value = HomeUiState.Loading

            val chatHistory = homeUseCase.getChatHistory()

            if (chatHistory.isEmpty()) {
                _homeUiState.value = HomeUiState.EmptyChat
                return@launch
            }

            _homeUiState.value = HomeUiState.Success(chatHistory)
        }
    }

    private val homeExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _errorState.value = throwable.localizedMessage?.toString() ?: "Unexpected error occured"
    }

    fun onPermissionNotGranted() {
        viewModelScope.launch {
            _permissionState.value = false
        }
    }
}