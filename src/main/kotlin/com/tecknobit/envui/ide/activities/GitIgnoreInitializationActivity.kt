package com.tecknobit.envui.ide.activities

import com.intellij.openapi.application.writeAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.openapi.vfs.writeText

/**
 * The `GitignoreInitializationActivity` class is useful to ensure sensitive environment and workspace files are ignored
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class GitIgnoreInitializationActivity : ProjectActivity {

    /**
     * The companion object allows to access the standard ignore filename
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    private companion object {

        /**
         * `GITIGNORE_FILENAME` the standard Git ignore filename
         */
        const val GITIGNORE_FILENAME = ".gitignore"

    }

    /**
     * The `GitignoreFileEntry` enum is useful to represent a file entry required in the project ignore file
     *
     * @property fileName The filename to ignore
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    private enum class GitignoreFileEntry(
        val fileName: String
    ) {

        /**
         * The environment source ignore entry
         */
        ENV(
            fileName = ".env"
        ),

        /**
         * The JetBrains IDE workspace ignore entry
         */
        WORKSPACE(
            fileName = "workspace.xml"
        )

    }

    /**
     * Method used to create the root ignore file and ensure the required entries are present
     *
     * @param project The initialized project
     */
    override suspend fun execute(
        project: Project
    ) {
        val gitignoreRoot = createGitignoreRootFile(
            project = project
        )

        ensureToIgnoreEntryVersioning(
            gitignoreFile = gitignoreRoot
        )
    }

    /**
     * Method used to find or create the ignore file in the project root
     *
     * @param project The project whose ignore file is resolved
     *
     * @return the root ignore file as [VirtualFile]
     */
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

    /**
     * Method used to append missing required entries to a project ignore file
     *
     * @param gitignoreFile The ignore file to update
     */
    private suspend fun ensureToIgnoreEntryVersioning(
        gitignoreFile: VirtualFile,
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