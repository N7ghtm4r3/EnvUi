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

    override suspend fun execute(
        project: Project
    ) {
        val gitignoreRoot = createGitignoreRootFile(
            project = project
        )

        guaranteeToIgnoreDEnvVersioning(
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

    private suspend fun guaranteeToIgnoreDEnvVersioning(
        gitignoreFile: VirtualFile
    ) {
        val currentContent = gitignoreFile.readText()
        val dEnvExtension = ".env"
        if(currentContent.contains(dEnvExtension + "\n"))
            return

        val content = buildString {
            append(currentContent)
            append("\n")
            append(dEnvExtension)
        }

        writeAction {
            gitignoreFile.writeText(
                content = content
            )
        }
    }

}