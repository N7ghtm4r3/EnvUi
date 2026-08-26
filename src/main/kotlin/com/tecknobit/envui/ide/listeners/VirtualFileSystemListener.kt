package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.utils.isEnvFile
import com.tecknobit.envui.utils.resolveProject

/**
 * The `VirtualFileSystemListener` class is useful to delete persisted preferences when environment sources are deleted
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class VirtualFileSystemListener : BulkFileListener {

    /**
     * Method used to process environment source deletion events after virtual file changes
     *
     * @param events The completed virtual file events
     */
    override fun after(
        events: List<VFileEvent>
    ) {
        super.after(events)
        val envSourceRelatedEvents = events.filter { event ->
            event.file.isEnvFile() && event is VFileDeleteEvent
        }

        envSourceRelatedEvents.forEach { event ->
            val virtualFile = event.file ?: return@forEach
            val project = virtualFile.resolveProject() ?: return@forEach

            project.useEnvSourcePreferencesManager {
                deleteAllSourcePreferences(virtualFile)
            }
        }
    }

}