package com.tecknobit.envui.ide.activities

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.LocalFileSystem

class GitignoreInitializationActivity : ProjectActivity {

    private companion object {

        const val GITIGNORE_FILENAME = ".gitignore"

    }

    override suspend fun execute(
        project: Project
    ) {
        val basePath = project.basePath
        val localFileSystem = LocalFileSystem.getInstance()

        val gitignoreRoot = localFileSystem
            .findFileByPath(
                "${basePath}/$GITIGNORE_FILENAME"
            )?.findOrCreateChildData(
                project,
                GITIGNORE_FILENAME
            )

        println(gitignoreRoot)

        guaranteeToIgnoreDEnvVersioning()
    }

    private suspend fun createGitignoreRootFile(
        project: Project
    ) {

    }

    private fun guaranteeToIgnoreDEnvVersioning() {

    }

}