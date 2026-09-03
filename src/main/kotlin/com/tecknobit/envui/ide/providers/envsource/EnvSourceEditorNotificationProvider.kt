package com.tecknobit.envui.ide.providers.envsource

import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.containsEnvTemplate
import com.tecknobit.envui.utils.isNotEnvFile

class EnvSourceEditorNotificationProvider : EnvEditorNotificationProviderFactory() {

//    /**
//     * Method used to create notification content for environment source editors
//     *
//     * @param project The project containing the file
//     * @param file The virtual file displayed by the editor
//     *
//     * @return the notification component provider, if the file is an environment source, as [Function]
//     */
//    override fun collectNotificationData(
//        project: Project,
//        file: VirtualFile,
//    ): Function<in FileEditor, out JComponent?>? {
//        if (file.isNotEnvFile())
//            return null
//
//        val envTemplateExists = file.parent.containsEnvTemplate()
//        return Function {
//            if (envTemplateExists) {
//                val envSource = file.toEnvSource(
//                    project = project
//                )
//
//                infoPanel(
//                    envSource = envSource
//                )
//            } else {
//                warningPanel(
//                    project = project,
//                    envTemplateFile = file
//                )
//            }
//        }
//    }

    override fun VirtualFile.a(): Boolean {
        return isNotEnvFile()
    }

    override fun VirtualFile.exi(): Boolean {
        return containsEnvTemplate()
    }

//    private fun infoPanel(
//        envSource: EnvSource,
//    ): EditorNotificationPanel {
//        return EditorNotificationPanel(Info).apply {
//            text = I18nMessageBundle.message(
//                key = "edit.env.in.dialog"
//            )
//
//            createActionLabel(
//                I18nMessageBundle.message(
//                    key = "open"
//                )
//            ) {
//                val dialog = EnvSourceReaderDialog(
//                    envSource = envSource
//                )
//
//                dialog.show()
//            }
//        }
//    }

    override fun warningPanelMessage(): @NlsSafe String {
        TODO("Not yet implemented")
    }

    override suspend fun warningPanelAction(repository: EnvSourceRepository): EnvSource {
        TODO("Not yet implemented")
    }
}