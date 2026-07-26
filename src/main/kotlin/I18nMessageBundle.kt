package com.tecknobit.envui

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

private const val BUNDLE = "messages.I18nMessageBundle"

internal object I18nMessageBundle {
    private val instance = DynamicBundle(I18nMessageBundle::class.java, BUNDLE)

    @JvmStatic
    fun message(
        @PropertyKey(resourceBundle = BUNDLE)
        key: String,
        vararg params: Any?,
    ): @Nls String {
        return instance.getMessage(
            key = key,
            *params
        )
    }

    @JvmStatic
    fun lazyMessage(
        @PropertyKey(resourceBundle = BUNDLE)
        key: String,
        vararg params: Any?,
    ): Supplier<@Nls String> {
        return instance.getLazyMessage(
            key = key,
            *params
        )
    }
}
