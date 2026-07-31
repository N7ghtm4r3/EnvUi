package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intellij.openapi.project.Project
import com.tecknobit.envui.com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.states.EnvUiWindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EnvUiWindowViewModel(
    project: Project,
) : ViewModel() {

    private val repository = EnvSourceRepository(
        project = project
    )

    private val _windowState = MutableStateFlow(
        value = EnvUiWindowState()
    )
    val windowState = _windowState.asStateFlow()

    fun retrieveSources() {
        viewModelScope.launch {
            val sources = repository.retrieveEnvs(
                filters = _windowState.value.query.value
            )

            _windowState.update {
                it.copy(
                    sources = sources
                )
            }
        }
    }

    fun filterSources(
        query: String,
    ) {
        _windowState.value.query.value = query

        retrieveSources()
    }

}