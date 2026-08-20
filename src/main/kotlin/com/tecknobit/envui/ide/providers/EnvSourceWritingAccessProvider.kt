package com.tecknobit.envui.ide.providers

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.WritingAccessProvider
import com.tecknobit.envui.ide.languages.envfile.dEnvFile.Companion.ENV_FILENAME
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile.Companion.ENV_TEMPLATE_FILENAME
import org.jetbrains.annotations.Unmodifiable

class EnvSourceWritingAccessProvider : WritingAccessProvider() {

    override fun requestWriting(
        files: Collection<VirtualFile?>,
    ): @Unmodifiable Collection<VirtualFile?> {
        return files.filter { file ->
            val extension = file!!.extension

            extension != ENV_FILENAME && extension != ENV_TEMPLATE_FILENAME
        }
    }

}