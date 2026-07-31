package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

data class EnvUiWindowState(
    val sources: List<EnvSource> = emptyList(),
    val query: MutableState<String> = mutableStateOf(""),
)