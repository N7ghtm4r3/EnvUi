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
        val envSourcePreferences = EnvSourcePreferencesManager.retrieveEnvSourcePreferences(
            source = file
        )

        envSourcePreferences.properties.forEach { property ->
            val propertyLine = envSource.psiEnvSource.findPropertyLine(
                key = property.key
            )

            if(property.isCritical) {
                addCriticalEnvMark(
                    editor = editor,
                    line = propertyLine
                )
            }

            if(property.requireResetOnClose) {
                addResetOnCloseMark(
                    editor = editor,
                    line = propertyLine
                )
            }
        }
    }

}