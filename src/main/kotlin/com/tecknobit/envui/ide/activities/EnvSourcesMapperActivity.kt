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

/**
 * The `EnvSourcesMapperActivity` class is useful to refresh editor notifications after environment file changes
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see ProjectActivity
 *
 * @since 1.0.1
 */
class EnvSourcesMapperActivity : ProjectActivity {

    /**
     * Method used to subscribe the project to virtual file changes affecting environment files
     *
     * @param project The initialized project
     */
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

    /**
     * The `VfsEnvSourcesMapper` class is useful to refresh editor notifications after tracked environment file events
     *
     * @property project The project whose editor notifications are refreshed
     *
     * @author N7ghtm4r3 - Tecknobit
     *
     * @see BulkFileListener
     *
     * @since 1.0.1
     */
    private class VfsEnvSourcesMapper(
        private val project: Project,
    ) : BulkFileListener {

        /**
         * Method used to refresh editor notifications after tracked environment file events complete
         *
         * @param events The completed virtual file events
         */
        override fun after(events: List<VFileEvent>) {
            val sourceEvents = events.filter { event ->
                val isEnvFile = event.file.isEnvFile()

                isEnvFile && event.isTrackedEvent()
            }
            if (sourceEvents.isEmpty())
                return

            updateEditorNotifications()
        }

        /**
         * Method used to refresh all editor notifications of the project
         */
        private fun updateEditorNotifications() {
            val editorNotifications = EditorNotifications.getInstance(project)

            editorNotifications.updateAllNotifications()
        }

        /**
         * Method used to check whether this virtual file event can change the available environment sources
         *
         * @receiver The virtual file event to check
         *
         * @return whether the event is tracked as [Boolean]
         */
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