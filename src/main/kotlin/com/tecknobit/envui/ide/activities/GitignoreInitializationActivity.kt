package com.tecknobit.envui.ide.activities

import com.intellij.openapi.application.writeAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.openapi.vfs.writeText

class GitignoreInitializationActivity : ProjectActivity {

    private companion object {

        const val GITIGNORE_FILENAME = ".gitignore"

    }

    private enum class GitignoreFileEntry(
        val fileName: String
    ) {

        ENV(
            fileName = ".env"
        ),

        WORKSPACE(
            fileName = "workspace.xml"
        )

    }

    override suspend fun execute(
        project: Project
    ) {
        val gitignoreRoot = createGitignoreRootFile(
            project = project
        )

        guaranteeToIgnoreEntryVersioning(
            gitignoreFile = gitignoreRoot
        )
    }

    private suspend fun createGitignoreRootFile(
        project: Project
    ): VirtualFile {
        val localFileSystem = LocalFileSystem.getInstance()
        val containerDirectory = localFileSystem.findFileByPath(project.basePath!!)!!

        return writeAction {
            containerDirectory.findOrCreateChildData(
                project,
                GITIGNORE_FILENAME
            )
        }
    }

    private suspend fun guaranteeToIgnoreEntryVersioning(
        gitignoreFile: VirtualFile
    ) {
        val currentContent = gitignoreFile.readText()

        val content = buildString {
            append(currentContent)
            if(currentContent.isNotBlank())
                append("\n")

            GitignoreFileEntry.entries.forEach { gitignoreFileEntry ->
                val filename = gitignoreFileEntry.fileName
                if(currentContent.contains(filename + "\n"))
                    return@forEach

                append(filename)
                append("\n")
            }
        }

        writeAction {
            gitignoreFile.writeText(
                content = content
            )
        }
    }

}