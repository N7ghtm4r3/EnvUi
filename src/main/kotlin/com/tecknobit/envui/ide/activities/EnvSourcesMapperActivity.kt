package com.tecknobit.envui.ide.activities

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.ui.EditorNotifications
import com.tecknobit.envui.utils.isEnvFile

class EnvSourcesMapperActivity : ProjectActivity {

    override suspend fun execute(
        project: Project,
    ) {
        project.messageBus.connect()
            .subscribe(
                topic = VirtualFileManager.VFS_CHANGES,
                handler = VfsEnvSourcesMapper(
                    project = project
                )
            )
    }

    private class VfsEnvSourcesMapper(
        private val project: Project,
    ) : BulkFileListener {

        override fun after(events: List<VFileEvent>) {
            val sourceEvents = events.filter { event ->
                val isEnvFile = event.file.isEnvFile()

                isEnvFile && event.isTrackedEvent()
            }
            if (sourceEvents.isEmpty())
                return

            updateEditorNotifications()
        }

        private fun updateEditorNotifications() {
            val editorNotifications = EditorNotifications.getInstance(project)

            editorNotifications.updateAllNotifications()
        }

        private fun VFileEvent.isTrackedEvent(): Boolean {
            return when (this) {
                is VFileCreateEvent -> true
                is VFileDeleteEvent -> true
                is VFilePropertyChangeEvent -> true

                else -> false
            }
        }

    }

}