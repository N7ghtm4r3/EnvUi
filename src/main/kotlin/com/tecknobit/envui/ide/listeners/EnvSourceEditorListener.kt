package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.helpers.EnvSourceHighlightedPropertiesRegistry
import com.tecknobit.envui.ide.highlighters.addCriticalEnvMark
import com.tecknobit.envui.ide.highlighters.addResetOnCloseMark
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.isNotEnvFile
import com.tecknobit.envui.utils.isNotEnvSourceFile
import com.tecknobit.envui.utils.toEnvSource

/**
 * The `EnvSourceEditorListener` class is useful to configure opened environment source editors and their markers
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class EnvSourceEditorListener : FileEditorManagerListener {

    /**
     * Method used to make an opened environment file read-only and restore its property markers
     *
     * @param source The file editor manager opening the file
     * @param file The opened virtual file
     */
    override fun fileOpened(
        source: FileEditorManager,
        file: VirtualFile
    ) {
        if (file.isNotEnvSourceFile())
            return

        disableEditing(
            source = source,
            file = file
        )

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

    /**
     * Method used to configure every text editor of a virtual file as a viewer
     *
     * @param source The file editor manager containing the editors
     * @param file The virtual file whose editors are configured
     */
    private fun disableEditing(
        source: FileEditorManager,
        file: VirtualFile,
    ) {
        val allEditors = source.getAllEditors(file)
        val textEditors = allEditors.filterIsInstance<TextEditor>()

        textEditors.forEach { textEditor ->
            val editor = textEditor.editor
            if (editor !is EditorEx)
                return@forEach

            editor.isViewer = true
        }
    }

    /**
     * Method used to restore the persisted preference markers of an environment source
     *
     * @param project The project containing the source
     * @param envSource The environment source to highlight
     */
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