package com.tecknobit.envui.utils.converters

import com.tecknobit.envui.ide.envfile.KeyEntry

fun Collection<KeyEntry>.formatToContent(): String {
    return this.joinToString(
        separator = "\n",
        transform = { entry ->
            val key = entry.text

            "$key="
        }
    )
}