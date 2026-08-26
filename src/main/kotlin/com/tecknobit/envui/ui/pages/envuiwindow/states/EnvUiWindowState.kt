package com.tecknobit.envui.ui.pages.envuiwindow.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

/**
 * The `EnvUiWindowState` class is useful to expose the environment sources and active search query of the window
 *
 * @property sources The environment sources displayed by the window, or `null` while unavailable
 * @property query The current environment source search query
 *
 * @author N7ghtm4r3 - Tecknobit
 */
data class EnvUiWindowState(
    val sources: List<EnvSource>? = null,
    val query: MutableState<String> = mutableStateOf(""),
)
