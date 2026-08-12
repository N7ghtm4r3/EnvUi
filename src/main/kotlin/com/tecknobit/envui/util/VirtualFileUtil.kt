package com.tecknobit.envui.util

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

fun String.toVirtualFile(): VirtualFile? {
    val path = kotlin.io.path.Path(this)

    return runReadAction {
        VfsUtil.findFile(path, true)
    }
}