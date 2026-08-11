package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.critical_env_sources_changed
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ui.components.Card
import com.tecknobit.envui.ui.components.EmptyState
import com.tecknobit.envui.ui.components.LazyListScaffold
import com.tecknobit.envui.ui.components.ModuleBadge
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys

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
                    val sourcePath = criticalEnvSource.sourcePath
                    val localFileSystem = VirtualFileManager.getInstance()
                    val source = localFileSystem.refreshAndFindFileByUrl(sourcePath)
                    val module = ModuleUtil.findModuleForFile(source!!, project)
                    stickyHeader {
                        ModuleBadge(
                            module = module!!
                        )
                    }

                    items(
                        items = criticalEnvSource.properties.values.toList(),
                        key = { it.key }
                    ) { criticalEnvSourceProperty ->
                        Text(
                            modifier = Modifier
                                .fillMaxHeight(),
                            text = "gewgwe"
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun CriticalEnvSource(
    modifier: Modifier = Modifier,
    criticalEnvSource: EnvSourcePreferences
) {
    Card(
        modifier = modifier
    ) {
        Text(
            text = "gege"
        )
    }
}