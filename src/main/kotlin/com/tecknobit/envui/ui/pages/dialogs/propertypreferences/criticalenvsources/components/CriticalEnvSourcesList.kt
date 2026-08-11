package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.critical_env_sources_changed
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ide.services.EnvSourcePropertyPreferences
import com.tecknobit.envui.ui.components.Card
import com.tecknobit.envui.ui.components.EmptyState
import com.tecknobit.envui.ui.components.LazyListScaffold
import com.tecknobit.envui.ui.components.ModuleBadge
import com.tecknobit.envui.ui.theme.EnvUiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import java.nio.file.Path

@Composable
fun CriticalEnvSourcesList(
    modifier: Modifier = Modifier,
    project: Project,
    criticalEnvSources: List<EnvSourcePreferences>
) {
    LazyListScaffold(
        items = criticalEnvSources,
        onEmpty = {
            EmptyState(
                icon = AllIconsKeys.Empty,
                title = Res.string.critical_env_sources_changed
            )
        },
        content = {
            LazyColumn(
                modifier = modifier
                    .animateContentSize(),
                contentPadding = PaddingValues(
                    vertical = 16.dp,
                    horizontal = 2.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                criticalEnvSources.forEach { criticalEnvSource ->
                    propertiesHeader(
                        project = project,
                        criticalEnvSource = criticalEnvSource
                    )

                    items(
                        items = criticalEnvSource.properties.values.toList(),
                        key = { it.key }
                    ) { criticalEnvSourceProperty ->
                        CriticalEnvSource(
                            modifier = Modifier
                                .animateItem(),
                            criticalEnvSourceProperty = criticalEnvSourceProperty
                        )
                    }
                }
            }
        }
    )
}

private fun LazyListScope.propertiesHeader(
    project: Project,
    criticalEnvSource: EnvSourcePreferences,
) {
    stickyHeader {
        val sourcePath = Path.of(criticalEnvSource.sourcePath)
        val module by produceState<Module?>(
            initialValue = null,
            key1 = project,
            key2 = sourcePath,
            producer = {
                value = withContext(Dispatchers.IO) {
                    runReadAction {
                        val source = VfsUtil.findFile(sourcePath, true)
                        source?.let {
                            ModuleUtil.findModuleForFile(source, project)
                        }
                    }
                }
            }
        )

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(EnvUiTheme.background)
        ) {
            module?.let {
                ModuleBadge(
                    module = it
                )
            }
        }
    }
}

@Composable
private fun CriticalEnvSource(
    modifier: Modifier = Modifier,
    criticalEnvSourceProperty: EnvSourcePropertyPreferences
) {
    Card(
        modifier = modifier
            .height(150.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = criticalEnvSourceProperty.key
        )
    }
}