package me.thekusch.hermes.home.ui

import android.content.Context
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
import me.thekusch.messager.controller.BaseStatus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private lateinit var hermes: Hermes

    private val _latestChatConnectionInfo: MutableStateFlow<BaseStatus.ConnectionInitiated?> =
        MutableStateFlow(null)

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

        hermesStateMapper(hermesState)

        HomeState(
            uiState = homeUiState,
            permissionUiState = permissionUiState,
            errorState = errorState,
            hermesState = hermesState
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, HomeState())

    //TODO(murat) save if user rejected or accepted requests, observe permission changes
    // handle context storage in Hermes
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

    fun dismissCreateChat() {
        hermes.dismissConnection()
    }

    fun connectionAnswer(
        answer: Boolean,
        data: BaseStatus.ConnectionInitiated,
        context: Context
    ) {
        if (answer) {
            hermes.acceptConnection(data.endpointId, context)
            _latestChatConnectionInfo.value = data
            return
        }
        hermes.rejectConnection(data.endpointId, context)
    }

    private suspend fun hermesStateMapper(hermesState: BaseStatus) {
        when (hermesState) {
            is BaseStatus.WavingStarting -> {
                if (hermesState is BaseStatus.StartFinishedWithError) {
                    _errorState.value = hermesState.exception.localizedMessage.orEmpty()

                }

                if (hermesState is BaseStatus.StartFinishedWithSuccess) {
                    _homeUiState.value = HomeUiState.CreateChat(CreateChatMethod.ADVERTISE)
                }
            }

            is BaseStatus.WavingMatchDetecting -> {
                if (hermesState is BaseStatus.ConnectionResultStatus) {

                    if (hermesState.result == BaseStatus.ConnectionResultStatus.CONNECTED) {
                        homeUseCase.createNewChat(
                            _latestChatConnectionInfo.value!!.endpointId,
                            _latestChatConnectionInfo.value!!.endpointName
                        )
                    }
                    if (hermesState.result == BaseStatus.ConnectionResultStatus.ERROR) {
                        _errorState.value = "Unexpted error happen please try later"
                    }
                }
            }

            is BaseStatus.Dismissed -> {
                _homeUiStateHistory.value?.previous?.let {
                    _homeUiState.value = it
                }
            }

            else -> {
                //no-op
            }
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