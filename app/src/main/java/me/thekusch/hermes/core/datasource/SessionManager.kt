package me.thekusch.hermes.core.datasource

import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.thekusch.hermes.core.datasource.local.HermesLocalDataSource
import me.thekusch.hermes.core.datasource.supabase.Supabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val supabase: Supabase,
    private val hermesLocalDataSource: HermesLocalDataSource,
) {

    private val useCaseScope = CoroutineScope(Dispatchers.IO)

    init {
        observeSessionChanges()
    }

    private fun observeSessionChanges() {
        useCaseScope.launch {
            supabase.sessionStatus.collectLatest { status ->
                when(status) {
                    is SessionStatus.Authenticated -> {

                    }
                    else -> {

                    }
                }
            }
        }
    }

    suspend fun completeSignUp() {
        withContext(Dispatchers.IO) {


        }
    }
}