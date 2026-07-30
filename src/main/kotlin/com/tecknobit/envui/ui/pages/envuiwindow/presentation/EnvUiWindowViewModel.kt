package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intellij.openapi.project.Project
import com.tecknobit.envui.com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EnvUiWindowViewModel(
    project: Project,
) : ViewModel() {

    private val repository = EnvSourceRepository(
        project = project
    )

    private val _sources = MutableStateFlow(
        value = listOf<EnvSource>()
    )
    val sources = _sources.asStateFlow()

    fun retrieveSources() {
        viewModelScope.launch {
            val sources = repository.retrieveEnvs()

            _sources.value = sources
        }
    }

}