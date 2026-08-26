package com.tecknobit.envui.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.JvmResourceReader
import org.jetbrains.compose.resources.LocalResourceReader
import kotlin.reflect.KClass

/**
 * Component used to provide string resources through the class loader of the specified context
 *
 * @param context The class whose class loader is used to read the resources
 * @param content The content rendered with the configured resource reader
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun StringResourcesProvider(
    context: KClass<*>,
    content: @Composable () -> Unit,
) {
    val jvmResourceReaderClassLoader = JvmResourceReader(context.java.classLoader)

    CompositionLocalProvider(LocalResourceReader provides jvmResourceReaderClassLoader) {
        content()
    }
}
