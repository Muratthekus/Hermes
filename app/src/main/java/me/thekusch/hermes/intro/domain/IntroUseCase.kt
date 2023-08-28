package me.thekusch.hermes.intro.domain

import javax.inject.Inject

class IntroUseCase @Inject constructor(
    private val introResourceLocalProvider: IntroResourceLocalProvider
) {

    fun provideIntroItems() = introResourceLocalProvider.provideIntroItems()
}