package me.thekusch.hermes.home.ui

import me.thekusch.messager.controller.BaseStatus

data class HomeState(
    val uiState: HomeUiState = HomeUiState.Init,
    val permissionUiState: Boolean = true,
    val errorState: String = "",
    val hermesState: BaseStatus = BaseStatus.Initial
)