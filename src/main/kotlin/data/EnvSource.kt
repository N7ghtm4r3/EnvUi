package com.tecknobit.envui.data

import com.intellij.openapi.vfs.VirtualFile

data class EnvSource(
    val source: VirtualFile,
) {

    val name = source.name

    val path = source.path

}