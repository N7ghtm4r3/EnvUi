package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intellij.openapi.project.Project
import com.tecknobit.envui.com.tecknobit.envui.ui.components.DebouncedInput
import com.tecknobit.envui.com.tecknobit.envui.ui.helpers.StringResourcesProvider
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.components.EnvSourcesList
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.presentation.EnvUiWindowViewModel
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.presenter.EnvUiWindow
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.search_by_folder_or_module

@Composable
fun EnvUiWindowContent(
    viewModel: EnvUiWindowViewModel,
    project: Project,
) {
    val windowState by viewModel.windowState.collectAsStateWithLifecycle()
    val sources = windowState.sources

    LaunchedEffect(project) {
        viewModel.retrieveSources()

        viewModel.monitorFileTreeChanges()
    }

    StringResourcesProvider(
        context = EnvUiWindow::class,
        content = {
            Column(
                modifier = Modifier
                    .padding(
                        all = 16.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = sources != null
                ) {
                    DebouncedInput(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        initialValue = windowState.query.value,
                        placeholder = Res.string.search_by_folder_or_module,
                        onDebounce = {
                            viewModel.filterSources(
                                query = it
                            )
                        }
                    )
                }

                EnvSourcesList(
                    modifier = Modifier
                        .fillMaxSize(),
                    sources = sources
                )
            }
        }
    )
}