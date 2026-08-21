package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.intellij.ui.JBColor
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.manage_template
import com.tecknobit.envui.generated.resources.mark_as_critical_to_change
import com.tecknobit.envui.generated.resources.reset_value_on_close
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.services.EnvSourcePropertyPreferences
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.components.*
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.ui.theme.EnvUiTheme
import com.tecknobit.envui.ui.utils.toComposeColor
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EnvSourceContent(
    modifier: Modifier = Modifier,
    envSource: EnvSource,
    onEmptyAction: () -> Unit
) {
    val psiEnvSource = envSource.psiEnvSource
    val properties = psiEnvSource.properties().toList()

    LazyListScaffold(
        items = properties,
        onEmpty = {
            NoEnvEntryAvailable(
                modifier = Modifier
                    .fillMaxSize(),
                action = {
                    DefaultButton(
                        onClick = onEmptyAction
                    ) {
                        Text(
                            text = stringResource(Res.string.manage_template)
                        )
                    }
                }
            )
        },
    ) {
        LazyColumn(
            modifier = modifier
                .animateContentSize(),
            contentPadding = PaddingValues(
                vertical = 16.dp,
                horizontal = 2.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = properties,
                key = { property -> property.keyEntry.text }
            ) { property ->
                EnvSourceProperty(
                    modifier = Modifier
                        .animateItem(),
                    property = property,
                    envSource = envSource,
                    onPropertyChange = { key, value ->
                        envSource.psiEnvSource.updateValueForKey(
                            key = key,
                            value = value
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EnvSourceProperty(
    modifier: Modifier = Modifier,
    property: Property,
    envSource: EnvSource,
    onPropertyChange: (String, String) -> Unit
) {
    Column(
        modifier = modifier
            .clip(CardShape)
            .border(
                width = 2.dp,
                color = EnvUiTheme.border,
                shape = CardShape
            )
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column (
            modifier = Modifier
                .padding(
                    all = 12.dp
                )
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(3f)
                ) {
                    KeyText(
                        property = property
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Actions(
                        envSource = envSource,
                        property = property
                    )
                }
            }

            EnvSourceInput(
                envSource = envSource,
                property = property,
                onPropertyChange = onPropertyChange
            )
        }
    }
}

@Composable
private fun Actions(
    envSource: EnvSource,
    property: Property
) {
    var refresh by rememberSaveable { mutableStateOf(false) }
    var propertyPreferences by rememberSaveable {
        mutableStateOf(
            retrievePropertyPreference(
                envSource = envSource,
                property = property
            )
        )
    }

    LaunchedEffect(refresh) {
        if(refresh) {
            propertyPreferences = retrievePropertyPreference(
                envSource = envSource,
                property = property
            )

            refresh = false
        }
    }

    Row (
        modifier = Modifier
            .padding(
                end = 1.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Chip(
            icon = AllIconsKeys.General.InspectionsWarning,
            text = Res.string.mark_as_critical_to_change,
            color = EnvUiTheme.error,
            isClicked = propertyPreferences.isCritical,
            onClick = {
                envSource.psiEnvSource.toggleMarkAsCritical(
                    key = property.keyEntry.text,
                    envSource = envSource
                )

                refresh = true
            }
        )

        Chip(
            icon = AllIconsKeys.General.Reset,
            text = Res.string.reset_value_on_close,
            color = JBColor.gray.toComposeColor(),
            isClicked = propertyPreferences.requireResetOnClose,
            onClick = {
                envSource.psiEnvSource.toggleResetOnClose(
                    key = property.keyEntry.text,
                    envSource = envSource
                )

                refresh = true
            }
        )
    }
}

private fun retrievePropertyPreference(
    envSource: EnvSource,
    property: Property
): EnvSourcePropertyPreferences {
    return envSource.useEnvSourcePreferencesManager {
        retrievePropertyPreferences(
            source = envSource.source,
            property = property
        )
    }
}

@Composable
private fun EnvSourceInput(
    envSource: EnvSource,
    property: Property,
    onPropertyChange: (String, String) -> Unit,
) {
    val key = property.keyEntry.text
    val type: EnvFieldType = envSource.useEnvSourcePreferencesManager {
        retrievePropertyType(
            source = envSource.source,
            key = key
        )
    }

    EnvPropertyDebounceInput(
        modifier = Modifier
            .fillMaxWidth(),
        delay = 200.milliseconds,
        property = property,
        type = type,
        onDebounce = { onPropertyChange(key, it) }
    )
}
