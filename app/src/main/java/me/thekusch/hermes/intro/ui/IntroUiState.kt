package me.thekusch.hermes.intro.ui

import me.thekusch.hermes.intro.domain.IntroItem

sealed class IntroUiState {

    data class Result(
        val introItems: List<IntroItem>
    ): IntroUiState()
    object Loading: IntroUiState()
}