package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presenter.CriticalEnvSourcesWarningDialog
import com.tecknobit.envui.utils.toEnvSource
import com.tecknobit.envui.utils.toVirtualFile

/**
 * The `IdeLifecycleListener` class is useful to handle environment property preferences before a project closes
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class IdeLifecycleListener : ProjectManagerListener {

    /**
     * Method used to reset configured values and warn about changed critical properties before project closing
     *
     * @param project The project being closed
     */
    override fun projectClosingBeforeSave(
        project: Project
    ) {
        super.projectClosingBeforeSave(project)
        handleResettableOnCloseProperties(
            project = project
        )

        handleCriticalProperties(
            project = project
        )
    }

    /**
     * Method used to restore the initial values of changed properties configured for reset on close
     *
     * @param project The project whose properties are restored
     */
    private fun handleResettableOnCloseProperties(
        project: Project
    ) {
        val resettableOnCloseSources = project.useEnvSourcePreferencesManager {
            retrieveAllResettableOnCloseEnvSourcePreferences()
        }

        project.useEnvSourcePreferencesManager {
            resettableOnCloseSources.forEach { preferences ->
                val source = preferences.sourcePath.toVirtualFile() ?: return@forEach
                val envSource = source.toEnvSource(
                    project = project,
                    resolveModule = false
                )
                val psiSource = envSource.psiEnvSource

                preferences.properties.forEach { (key, preferences) ->
                    val property = psiSource.findPropertyByKey(
                        key = key
                    )

                    property?.let {
                        psiSource.updateValueForKey(
                            key = key,
                            value = preferences.initialValue,
                            synchronously = true
                        )
                    }
                }
            }
        }
    }

    /**
     * Method used to warn about changed critical properties before the project closes
     *
     * @param project The project whose critical properties are checked
     */
    private fun handleCriticalProperties(
        project: Project
    ) {
        val criticalEnvSources = project.useEnvSourcePreferencesManager {
            retrieveAllCriticalEnvSourcePreferences()
        }

        if(criticalEnvSources.isNotEmpty()) {
            val criticalEnvSourcesWarningDialog = CriticalEnvSourcesWarningDialog(
                project = project,
                criticalEnvSources = criticalEnvSources
            )

            criticalEnvSourcesWarningDialog.show()
        }
    }

}