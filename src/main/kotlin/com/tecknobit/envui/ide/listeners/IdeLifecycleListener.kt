package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presenter.CriticalEnvSourcesWarningDialog
import com.tecknobit.envui.utils.toEnvSource
import com.tecknobit.envui.utils.toVirtualFile

class IdeLifecycleListener : ProjectManagerListener {

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