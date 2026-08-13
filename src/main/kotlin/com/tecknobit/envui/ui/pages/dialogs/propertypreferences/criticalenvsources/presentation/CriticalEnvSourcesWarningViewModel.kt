package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presentation

import androidx.lifecycle.ViewModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ide.services.EnvSourcePreferencesManager
import com.tecknobit.envui.ide.services.EnvSourcePropertyPreferences
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.states.CriticalEnvSourcesWarningState
import com.tecknobit.envui.util.toEnvSource
import com.tecknobit.envui.util.toVirtualFile
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
        resolvePropertyDiff(
            sourcePath = sourcePath,
            envSourcePreferences = envSourcePreferences,
            propertyPreferences = propertyPreferences
        ) { source, _, property ->
            acceptNewPropertyValue(
                source = source,
                property = property
            )

            propertyPreferences.currentValue
        }
    }

    fun revertPropertyValue(
        sourcePath: String,
        envSourcePreferences: EnvSourcePreferences,
        propertyPreferences: EnvSourcePropertyPreferences
    ) {
        resolvePropertyDiff(
            sourcePath = sourcePath,
            envSourcePreferences = envSourcePreferences,
            propertyPreferences = propertyPreferences
        ) { _, _, _ ->
            propertyPreferences.initialValue
        }
    }

    private inline fun resolvePropertyDiff(
        sourcePath: String,
        envSourcePreferences: EnvSourcePreferences,
        propertyPreferences: EnvSourcePropertyPreferences,
        crossinline resolution:  EnvSourcePreferencesManager.(VirtualFile, dEnvFile, Property) -> String
    ) {
        val key = propertyPreferences.key

        project.useEnvSourcePreferencesManager {
            val source = sourcePath.toVirtualFile()
            source?.let {
                val envSource = source.toEnvSource(
                    project = project,
                    resolveModule = false
                )
                val psiSource = envSource.psiEnvSource
                val property = psiSource.findPropertyByKey(
                    key = key
                )

                property?.let {
                    val resolutionValue = resolution(source, psiSource, property)
                    psiSource.updateValueForKey(
                        key = key,
                        value = resolutionValue
                    )

                    _uiState.update {
                        val updatedDiffList = it.criticalEnvSources.minus(
                            element = envSourcePreferences
                        )

                        it.copy(
                            criticalEnvSources = updatedDiffList
                        )
                    }
                }
            }
        }
    }

}