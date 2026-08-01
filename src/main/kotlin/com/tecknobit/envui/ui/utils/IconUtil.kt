package com.tecknobit.envui.ui.utils

import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jewel.ui.icon.IconKey
import org.jetbrains.jewel.ui.icons.AllIconsKeys

suspend fun VirtualFile.resolveIcon(
    project: Project,
): IconKey {
    val projectIndex = ProjectFileIndex.getInstance(project)

    return readAction {
        when {
            projectIndex.isExcluded(this) -> AllIconsKeys.Modules.ExcludeRoot
            projectIndex.isInGeneratedSources(this) -> AllIconsKeys.Modules.GeneratedFolder
            projectIndex.isInTestSourceContent(this) -> AllIconsKeys.Modules.TestRoot
            projectIndex.isInSourceContent(this) -> AllIconsKeys.Modules.SourceRoot
            projectIndex.isInLibraryClasses(this) -> AllIconsKeys.Nodes.PpLibFolder
            projectIndex.isInLibrarySource(this) -> AllIconsKeys.Nodes.PpLibFolder
            projectIndex.isInContent(this) -> AllIconsKeys.Nodes.Folder

            else -> AllIconsKeys.Nodes.Folder
        }
    }
}
