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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.tecknobit.envui.generated.resources.*
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ide.services.EnvSourcePropertyPreferences
import com.tecknobit.envui.ui.components.*
import com.tecknobit.envui.ui.theme.EnvUiTheme
import com.tecknobit.envui.ui.utils.toDateString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Icon
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
                        CriticalEnvSourceCard(
                            modifier = Modifier
                                .animateItem(),
                            project = project,
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
private fun CriticalEnvSourceCard(
    modifier: Modifier = Modifier,
    project: Project,
    criticalEnvSourceProperty: EnvSourcePropertyPreferences
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CriticalEnvSourceCardHeader(
                criticalEnvSourceProperty = criticalEnvSourceProperty
            )

            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                ChangesDiff(
                    criticalEnvSourceProperty = criticalEnvSourceProperty
                )

                Column (
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Actions(
                        project = project,
                        criticalEnvSourceProperty = criticalEnvSourceProperty
                    )
                }
            }
        }
    }
}

@Composable
private fun CriticalEnvSourceCardHeader(
    modifier: Modifier = Modifier,
    criticalEnvSourceProperty: EnvSourcePropertyPreferences
) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        KeyText(
            key = criticalEnvSourceProperty.key,
            fontSize = 22.sp
        )

        Text(
            text = stringResource(
                resource = Res.string.critical_env_property_changed_description,
                criticalEnvSourceProperty.lastUpdateAt.toDateString()
            ),
            fontSize = 12.sp,
            color = EnvUiTheme.mutedColor
        )
    }
}

@Composable
private fun ChangesDiff(
    modifier: Modifier = Modifier,
    criticalEnvSourceProperty: EnvSourcePropertyPreferences
) {
    Row(
        modifier = modifier
    ) {
        CriticalPropertyValueBadge(
            color = EnvUiTheme.primary,
            value = criticalEnvSourceProperty.initialValue
        )

        Icon(
            modifier = Modifier
                .size(26.dp),
            key = AllIconsKeys.Actions.ArrowExpand,
            contentDescription = ""
        )

        CriticalPropertyValueBadge(
            color = EnvUiTheme.error,
            value = criticalEnvSourceProperty.currentValue
        )
    }
}

@Composable
private fun CriticalPropertyValueBadge(
    modifier: Modifier = Modifier,
    color: Color,
    value: String
) {
    Badge(
        modifier = modifier,
        text = value,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp
        ),
        color = color
    )
}

@Composable
private fun Actions(
    modifier: Modifier = Modifier,
    project: Project,
    criticalEnvSourceProperty: EnvSourcePropertyPreferences
) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DestructiveButton(
            onClick = {

            }
        ) {
            Text(
                text = stringResource(Res.string.revert)
            )
        }

        DefaultButton(
            onClick = {

            }
        ) {
            Text(
                text = stringResource(Res.string.accept)
            )
        }
    }
}
