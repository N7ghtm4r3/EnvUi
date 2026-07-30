@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intellij.openapi.project.Project
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.components.EnvSourcesList
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.presentation.EnvUiWindowViewModel
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.presenter.EnvUiWindow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.JvmResourceReader
import org.jetbrains.compose.resources.LocalResourceReader

@Composable
fun EnvUiWindowContent(
    viewModel: EnvUiWindowViewModel,
    project: Project,
) {
    val jvmResourceReaderClassLoader = JvmResourceReader(EnvUiWindow::class.java.classLoader)
    val sources by viewModel.sources.collectAsStateWithLifecycle()

    LaunchedEffect(project) {
        viewModel.retrieveSources()
    }

    CompositionLocalProvider(LocalResourceReader provides jvmResourceReaderClassLoader) {
        EnvSourcesList(
            sources = sources
        )
    }
}