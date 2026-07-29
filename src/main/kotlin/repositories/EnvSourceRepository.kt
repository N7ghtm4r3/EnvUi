package com.tecknobit.envui.repositories

import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.tecknobit.envui.data.EnvSource

class EnvSourceRepository(
    private val project: Project,
) {

    private companion object {

        const val ENV_EXT = ".env"

    }

    fun retrieveEnvs(): List<EnvSource> {
        val virtualFiles = FilenameIndex.getVirtualFilesByName(
            ENV_EXT,
            GlobalSearchScope.projectScope(project)
        )

        return virtualFiles.map {
            EnvSource(
                project = project,
                source = it
            )
        }
    }

}