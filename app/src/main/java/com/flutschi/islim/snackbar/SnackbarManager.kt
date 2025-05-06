package com.flutschi.islim.snackbar

import androidx.compose.runtime.*
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SnackbarManager {
    private var snackbarHostState: SnackbarHostState? = null
    private var coroutineScope: CoroutineScope? = null

    fun init(snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
        this.snackbarHostState = snackbarHostState
        this.coroutineScope = coroutineScope
    }

    fun showMessage(message: String) {
        coroutineScope?.launch {
            snackbarHostState?.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
}

@Composable
fun GlobalSnackbarHost() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    SnackbarManager.init(snackbarHostState, coroutineScope)
    SnackbarHost(hostState = snackbarHostState)
}
