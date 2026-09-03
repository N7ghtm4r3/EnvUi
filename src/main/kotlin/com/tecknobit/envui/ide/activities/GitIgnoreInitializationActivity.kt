package com.tecknobit.envui.ide.activities

import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.writeAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import kotlin.math.abs

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

        TAG_LINE(
            fileName = "# From EnvUi"
        ),

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
        val gitignoreRoot = ensureGitignoreRootFile(
            project = project
        )

        ensureToIgnoreEntryVersioning(
            project = project,
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
    private suspend fun ensureGitignoreRootFile(
        project: Project,
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
     * @param project The project whose ignore file is resolved
     * @param gitignoreFile The ignore file to update
     */
    private suspend fun ensureToIgnoreEntryVersioning(
        project: Project,
        gitignoreFile: VirtualFile,
    ) {
        val psiManager = PsiManager.getInstance(project)
        val psiFile = readAction {
            psiManager.findFile(gitignoreFile)
        } ?: throw IllegalStateException("Could not retrieve gitignore file")

        val gitIgnoreEntries = GitignoreFileEntry.entries
            .map { it.fileName }
            .toHashSet()
        val insertedEntries = hashSetOf<String>()

        readAction {
            psiFile.children.forEach {
                val text = it.text
                if (!gitIgnoreEntries.contains(text))
                    return@forEach

                insertedEntries.add(text)
            }
        }

        val diff = abs(insertedEntries.size - gitIgnoreEntries.size)

        WriteCommandAction.runWriteCommandAction(project) {
            var lineStarter = if (psiFile.text.isNotBlank() || diff == 1)
                "\n"
            else
                ""

            GitignoreFileEntry.entries.forEach { entry ->
                val fileName = entry.fileName
                if (insertedEntries.contains(fileName))
                    return@forEach

                val entryChild = psiFile.createGitIgnoreEntry(
                    content = "$lineStarter$fileName\n"
                )

                psiFile.addAfter(
                    entryChild,
                    psiFile.lastChild
                )

                lineStarter = ""
            }
        }
    }

    private fun PsiFile.createGitIgnoreEntry(
        content: String,
    ): PsiElement {
        val psiFileFactory = PsiFileFactory.getInstance(project)

        return psiFileFactory.createFileFromText(
            this.name,
            this.fileType,
            content
        )
    }

}