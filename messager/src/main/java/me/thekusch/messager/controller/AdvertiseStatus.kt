package me.thekusch.messager.controller

import java.lang.Exception

public sealed interface AdvertiseStatus: BaseStatus {

    public object Advertising: AdvertiseStatus

    public object FinishedSuccessfully: AdvertiseStatus

    public data class FinishedWithError(
        val exception: Exception
    ): AdvertiseStatus
}