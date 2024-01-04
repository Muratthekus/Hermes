package me.thekusch.hermes.home.ui

import me.thekusch.hermes.home.domain.model.Chat
import me.thekusch.hermes.home.ui.component.CreateChatMethod

sealed class HomeUiState {

    object Init: HomeUiState()

    object Loading: HomeUiState()

    object EmptyChat: HomeUiState()

    data class Success(val chatHistory: List<Chat>): HomeUiState()

    data class Error(val message: String? = null): HomeUiState()

    data class CreateChat(val selectedCreateChatMethod: CreateChatMethod): HomeUiState()

    object PermissionNotGranted: HomeUiState()
}

fun HomeUiState.isLoading(): Boolean = this == HomeUiState.Loading

fun HomeUiState.isSuccess(): Boolean = this is HomeUiState.Success

fun HomeUiState.isEmptyChat(): Boolean = this is HomeUiState.EmptyChat

fun HomeUiState.isCreateChat(): Boolean = this is HomeUiState.CreateChat

fun HomeUiState.isPermissionNotGranted(): Boolean = this is HomeUiState.PermissionNotGranted