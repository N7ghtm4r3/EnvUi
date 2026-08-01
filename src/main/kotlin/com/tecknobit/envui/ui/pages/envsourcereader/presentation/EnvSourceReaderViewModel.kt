package com.tecknobit.envui.ui.pages.envsourcereader.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecknobit.envui.ui.pages.envsourcereader.states.EnvSourceReaderState
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EnvSourceReaderViewModel(
    private val envSource: EnvSource,
) : ViewModel() {

    private val _dialogState = MutableStateFlow(
        value = EnvSourceReaderState()
    )
    val dialogState = _dialogState.asStateFlow()

    fun mapSourceTemplate() {
        viewModelScope.launch {
            println(envSource.psiEnvSource.properties())
        }
    }

}
