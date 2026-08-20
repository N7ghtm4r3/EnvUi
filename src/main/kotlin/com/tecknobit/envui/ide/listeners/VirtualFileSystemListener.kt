package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.utils.isEnvFile
import com.tecknobit.envui.utils.resolveProject

class VirtualFileSystemListener : BulkFileListener {

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