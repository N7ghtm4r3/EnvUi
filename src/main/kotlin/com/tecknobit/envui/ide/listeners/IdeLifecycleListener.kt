package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presenter.CriticalEnvSourcesWarningDialog

class IdeLifecycleListener : ProjectManagerListener {

    override fun projectClosingBeforeSave(
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

            super.projectClosingBeforeSave(project)
        }

    }

}