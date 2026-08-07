package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
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

            if(property.isCritical && envSource.isPropertyMarkedAsCritical(key)) {
                val highlighter = addCriticalEnvMark(
                    editor = editor,
                    line = propertyLine
                )

                envSource.markPropertyAsCritical(
                    key = key,
                    highlighter = highlighter
                )
            }

            if(property.requireResetOnClose && !envSource.isPropertyMarkedAsCritical(key)) {
                val highlighter = addResetOnCloseMark(
                    editor = editor,
                    line = propertyLine
                )

                envSource.markPropertyAsResettableOnClose(
                    key = key,
                    highlighter = highlighter
                )
            }
        }
    }

}