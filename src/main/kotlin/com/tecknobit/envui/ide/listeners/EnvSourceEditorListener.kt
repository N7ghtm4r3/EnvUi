package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.helpers.EnvSourceHighlightedPropertiesRegistry
import com.tecknobit.envui.ide.highlighters.addCriticalEnvMark
import com.tecknobit.envui.ide.highlighters.addResetOnCloseMark
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.isNotEnvFile
import com.tecknobit.envui.utils.toEnvSource

class EnvSourceEditorListener : FileEditorManagerListener {

    override fun fileOpened(
        source: FileEditorManager,
        file: VirtualFile
    ) {
        if (file.isNotEnvFile())
            return

        val project = source.project
        val envSource = file.toEnvSource(
            project = project,
            resolveModule = false
        )

        highlightProperties(
            project = project,
            envSource = envSource
        )
    }

    private fun highlightProperties(
        project: Project,
        envSource: EnvSource,
    ) {
        val registry = EnvSourceHighlightedPropertiesRegistry

        envSource.useEnvSourcePreferencesManager {
            val propertyPreferences = retrieveEnvSourcePreferences(
                source = envSource.source
            )

            propertyPreferences?.properties?.values?.forEach { property ->
                val key = property.key
                val propertyLine = envSource.psiEnvSource.findPropertyLine(
                    key = key
                )

                envSource.psiEnvSource.workWithDocument { document ->
                    if (property.isCritical) {
                        val highlighter = addCriticalEnvMark(
                            document = document,
                            project = project,
                            line = propertyLine
                        )

                        registry.markPropertyAsCritical(
                            envSource = envSource,
                            key = key,
                            highlighter = highlighter
                        )
                    }

                    if (property.requireResetOnClose) {
                        val highlighter = addResetOnCloseMark(
                            document = document,
                            project = project,
                            line = propertyLine
                        )

                        registry.markPropertyAsResettableOnClose(
                            envSource = envSource,
                            key = key,
                            highlighter = highlighter
                        )
                    }
                }
            }
        }
    }

}