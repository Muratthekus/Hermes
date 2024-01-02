package me.thekusch.hermes.core.datasource.local.model

import java.lang.Exception

/**
* General purpose Result wrapper. Can be use to update the
 * upstream about progress
* */
sealed interface Result {
    object Started: Result

    object Success: Result

    data class Fail(val exception: Throwable): Result

    object Retry: Result

}