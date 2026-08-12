package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presentation

import androidx.lifecycle.ViewModel
import com.intellij.openapi.project.Project
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ide.services.EnvSourcePropertyPreferences
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.states.CriticalEnvSourcesWarningState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CriticalEnvSourcesWarningViewModel(
    private val project: Project,
    criticalEnvSources: List<EnvSourcePreferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = CriticalEnvSourcesWarningState(
            criticalEnvSources = criticalEnvSources
        )
    )
    val uiState = _uiState.asStateFlow()

    fun acceptNewPropertyValue(
        sourcePath: String,
        envSourcePreferences: EnvSourcePreferences,
        propertyPreferences: EnvSourcePropertyPreferences
    ) {

    }

    fun revertPropertyValue(
        sourcePath: String,
        envSourcePreferences: EnvSourcePreferences,
        propertyPreferences: EnvSourcePropertyPreferences
    ) {
        _uiState.update {
            it.copy(
                criticalEnvSources = it.criticalEnvSources.minus(envSourcePreferences)
            )
        }
    }

    private fun resolveEnvSourcePreferencesBySourcePath(
        sourcePath: String
    ): EnvSourcePreferences {
        return _uiState.value.criticalEnvSources.first { criticalEnvSource ->
            criticalEnvSource.sourcePath == sourcePath
        }
    }

}