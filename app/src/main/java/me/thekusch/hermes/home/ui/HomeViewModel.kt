package me.thekusch.hermes.home.ui

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
        MutableStateFlow(false)

    private val _hermesState: MutableStateFlow<BaseStatus> =
        MutableStateFlow(BaseStatus.Initial)

    private val _errorState: MutableStateFlow<String> =
        MutableStateFlow("")

    // If Hermes fails in any reason we should rollback to previous HomeUiState
    private val _homeUiStateHistory: StateFlow<HomeUiStateHistory?> =
        _homeUiState.runningFold(
            initial = HomeUiStateHistory(
                null,
                HomeUiState.Init
            )
        ) { previous, current ->
            HomeUiStateHistory(previous.current, current)
        }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val homeState: StateFlow<HomeState> = combine(
        _homeUiState,
        _permissionState,
        _errorState,
        _hermesState,
    ) { homeUiState, permissionUiState, errorState, hermesState ->

        val homeState = when (hermesState) {
            is BaseStatus.Initial -> {
                homeUiState
            }
            is AdvertiseStatus.FinishedWithError -> {
                _errorState.value = hermesState.exception.localizedMessage.orEmpty()
                homeUiState
            }

            is DiscoveryStatus.DiscoveryFailed -> {
                _errorState.value = hermesState.exception.localizedMessage.orEmpty()
                homeUiState
            }

            else -> {
                val createChatMethod =
                    if (hermesState is AdvertiseStatus) CreateChatMethod.ADVERTISE
                    else CreateChatMethod.DISCOVER
                HomeUiState.CreateChat(createChatMethod)
            }
        }

        HomeState(
            uiState = homeState,
            permissionUiState = permissionUiState,
            errorState = errorState,
            hermesState = hermesState
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, HomeState())

    private lateinit var hermes: Hermes

    //TODO(murat) save if user rejected or accepted requests, observe permission changes
    fun initHermes(activity: FragmentActivity) {
        hermes = Hermes.init(activity, ::onPermissionNotGranted)
        viewModelScope.launch(homeExceptionHandler) {
            val user = homeUseCase.getCurrentUser()
            requireNotNull(user) { "User record can not be found thus Hermes couldn't initialize" }

            hermes.setUsername(user.name)
            hermes.build()
            setHermesListeners()
        }
    }

    fun createChat(
        method: CreateChatMethod
    ) {
        viewModelScope.launch(homeExceptionHandler) {
            if (method == CreateChatMethod.DISCOVER) {
                hermes.startDiscovery()
            }
            if (method == CreateChatMethod.ADVERTISE) {
                hermes.startAdvertising()
            }
        }
    }

    fun dismissCreateChat() {
        viewModelScope.launch {
            _homeUiState.value = _homeUiStateHistory.value?.previous ?:
            _homeUiState.value
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

    private fun onPermissionNotGranted() {
        viewModelScope.launch {
            _permissionState.value = false
        }
    }

    private fun setHermesListeners() {
        viewModelScope.launch {
            hermes.advertiseStatusListener = {
                _hermesState.value = it
            }
            hermes.discoveryStatusListener = {
                _hermesState.value = it
            }
        }
    }

    private data class HomeUiStateHistory(
        val previous: HomeUiState?,
        val current: HomeUiState
    )
}