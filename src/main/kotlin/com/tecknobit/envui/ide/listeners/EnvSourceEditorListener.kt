package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.helpers.EnvSourceHighlightedPropertiesRegistry
import com.tecknobit.envui.helpers.EnvSourcePreferencesManager
import com.tecknobit.envui.ide.highlighters.addCriticalEnvMark
import com.tecknobit.envui.ide.highlighters.addResetOnCloseMark
import com.tecknobit.envui.util.toEnvSource

class EnvSourceEditorListener : FileEditorManagerListener {

    override fun fileOpened(
        source: FileEditorManager,
        file: VirtualFile
    ) {
        super.fileOpened(source, file)
        val editor = source.selectedTextEditor!!
        val registry = EnvSourceHighlightedPropertiesRegistry
        val envSource = file.toEnvSource(
            project = source.project
        )
        val preferences = EnvSourcePreferencesManager.retrieveEnvSourcePreferences(
            source = file
        )

        preferences.properties.forEach { property ->
            val key = property.key
            val propertyLine = envSource.psiEnvSource.findPropertyLine(
                key = key
            )

            if(property.isCritical) {
                val highlighter = addCriticalEnvMark(
                    editor = editor,
                    line = propertyLine
                )

                registry.markPropertyAsCritical(
                    envSource = envSource,
                    key = key,
                    highlighter = highlighter
                )
            }

            if(property.requireResetOnClose) {
                val highlighter = addResetOnCloseMark(
                    editor = editor,
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