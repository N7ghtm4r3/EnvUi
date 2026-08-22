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
import com.tecknobit.envui.utils.toEnvSource
import com.tecknobit.envui.utils.toVirtualFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The **CriticalEnvSourcesWarningViewModel** class is the support class used to resolve changed critical properties
 *
 * @property project The project containing the critical environment sources
 * @param criticalEnvSources The source preferences containing the critical property changes to resolve
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class CriticalEnvSourcesWarningViewModel(
    private val project: Project,
    criticalEnvSources: List<EnvSourcePreferences>
) : ViewModel() {

    /**
     * `_uiState` the mutable state used to manage unresolved critical environment sources
     */
    private val _uiState = MutableStateFlow(
        value = CriticalEnvSourcesWarningState(
            criticalEnvSources = criticalEnvSources
        )
    )
    /**
     * `uiState` the read-only state exposed to the critical source warning dialog
     */
    val uiState = _uiState.asStateFlow()

    /**
     * Method used to accept the current value of a critical property and remove its source from the warning state
     *
     * @param sourcePath The path of the environment source
     * @param envSourcePreferences The preferences of the environment source
     * @param propertyPreferences The preferences of the critical property
     */
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

    /**
     * Method used to restore the initial value of a critical property and remove its source from the warning state
     *
     * @param sourcePath The path of the environment source
     * @param envSourcePreferences The preferences of the environment source
     * @param propertyPreferences The preferences of the critical property
     */
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

    /**
     * Method used to resolve a critical property difference and update the warning state
     *
     * @param sourcePath The path of the environment source
     * @param envSourcePreferences The preferences of the environment source
     * @param propertyPreferences The preferences of the critical property
     * @param resolution The operation returning the value to write for the resolution
     */
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