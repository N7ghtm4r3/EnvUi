package com.tecknobit.envui.ui.envuiwindow.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intellij.openapi.project.Project
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.envuiwindow.data.EnvSource
import org.jetbrains.jewel.ui.component.Text

@Composable
fun EnvUiWindowContent(
    project: Project,
) {
    val envSourceRepository = EnvSourceRepository(
        project = project
    )
    val envSources = retain { mutableStateListOf<EnvSource>() }

    LaunchedEffect(project) {
        envSources.addAll(envSourceRepository.retrieveEnvs())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                all = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = envSources,
            key = { envSource -> envSource.path }
        ) { envSource ->
            Text(
                text = envSource.name
            )
        }
    }
}