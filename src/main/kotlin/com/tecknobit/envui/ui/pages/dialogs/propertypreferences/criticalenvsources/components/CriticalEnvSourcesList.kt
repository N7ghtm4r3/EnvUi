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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.tecknobit.envui.generated.resources.*
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ide.services.EnvSourcePropertyPreferences
import com.tecknobit.envui.ui.components.*
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presentation.CriticalEnvSourcesWarningViewModel
import com.tecknobit.envui.ui.theme.EnvUiTheme
import com.tecknobit.envui.ui.utils.toDateString
import com.tecknobit.envui.utils.toVirtualFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys

/**
 * Component used to display and resolve changed critical environment properties
 *
 * @param modifier The modifier to apply to the list
 * @param project The project containing the environment sources
 * @param viewModel The support viewmodel used to expose and resolve critical properties
 */
@Composable
fun CriticalEnvSourcesList(
    modifier: Modifier = Modifier,
    project: Project,
    viewModel: CriticalEnvSourcesWarningViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val criticalEnvSources = uiState.criticalEnvSources

    LazyListScaffold(
        items = criticalEnvSources,
        onEmpty = {
            EmptyState(
                iconColor = Color.Unspecified,
                iconSize = 100.dp,
                icon = AllIconsKeys.Status.Success,
                title = Res.string.all_diff_solved
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
                        key = { criticalEnvSource.sourcePath + it.key }
                    ) { criticalEnvSourceProperty ->
                        CriticalEnvSourceCard(
                            modifier = Modifier
                                .animateItem(),
                            criticalEnvSourceProperty = criticalEnvSourceProperty,
                            onRevert = {
                                viewModel.revertPropertyValue(
                                    sourcePath = criticalEnvSource.sourcePath,
                                    envSourcePreferences = criticalEnvSource,
                                    propertyPreferences = criticalEnvSourceProperty
                                )
                            },
                            onAccept = {
                                viewModel.acceptNewPropertyValue(
                                    sourcePath = criticalEnvSource.sourcePath,
                                    envSourcePreferences = criticalEnvSource,
                                    propertyPreferences = criticalEnvSourceProperty
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}

/**
 * Method used to add the containing module header of a critical environment source to this lazy list
 *
 * @param project The project containing the environment source
 * @param criticalEnvSource The preferences of the critical environment source
 */
private fun LazyListScope.propertiesHeader(
    project: Project,
    criticalEnvSource: EnvSourcePreferences,
) {
    stickyHeader {
        val sourcePath = criticalEnvSource.sourcePath
        val module by produceState<Module?>(
            initialValue = null,
            key1 = project,
            key2 = sourcePath,
            producer = {
                value = withContext(Dispatchers.IO) {
                    runReadAction {
                        val source = sourcePath.toVirtualFile()
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

/**
 * Component used to display one changed critical property and its resolution actions
 *
 * @param modifier The modifier to apply to the card
 * @param criticalEnvSourceProperty The preferences of the changed property
 * @param onRevert The callback invoked when the property must revert to its initial value
 * @param onAccept The callback invoked when the current value must be accepted
 */
@Composable
private fun CriticalEnvSourceCard(
    modifier: Modifier = Modifier,
    criticalEnvSourceProperty: EnvSourcePropertyPreferences,
    onRevert: () -> Unit,
    onAccept: () -> Unit
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
                    modifier = Modifier
                        .weight(2.5f),
                    criticalEnvSourceProperty = criticalEnvSourceProperty
                )

                Column (
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Actions(
                        onRevert = onRevert,
                        onAccept = onAccept
                    )
                }
            }
        }
    }
}

/**
 * Section used to display the key and latest update time of a critical property
 *
 * @param modifier The modifier to apply to the header
 * @param criticalEnvSourceProperty The property preferences displayed by the header
 */
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

/**
 * Section used to display the initial and current values of a critical property
 *
 * @param modifier The modifier to apply to the value comparison
 * @param criticalEnvSourceProperty The property preferences containing the compared values
 */
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

/**
 * Badge used to display a critical property value
 *
 * @param modifier The modifier to apply to the badge
 * @param color The color representing the value state
 * @param value The property value to display
 */
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

/**
 * Section used to display the revert and accept actions of a critical property
 *
 * @param modifier The modifier to apply to the actions
 * @param onRevert The callback invoked when the initial value must be restored
 * @param onAccept The callback invoked when the current value must be accepted
 */
@Composable
private fun Actions(
    modifier: Modifier = Modifier,
    onRevert: () -> Unit,
    onAccept: () -> Unit
) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DefaultButton(
            onClick = onRevert
        ) {
            Text(
                text = stringResource(Res.string.revert)
            )
        }

        DestructiveButton(
            onClick = onAccept
        ) {
            Text(
                text = stringResource(Res.string.accept)
            )
        }
    }
}
