package com.tecknobit.envui.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.JvmResourceReader
import org.jetbrains.compose.resources.LocalResourceReader
import kotlin.reflect.KClass

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
