package com.tecknobit.envui.ui.pages.envsourcereader.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvSourceTemplate

data class EnvSourceReaderState(
    val template: MutableState<EnvSourceTemplate> = mutableStateOf(EnvSourceTemplate()),
)
