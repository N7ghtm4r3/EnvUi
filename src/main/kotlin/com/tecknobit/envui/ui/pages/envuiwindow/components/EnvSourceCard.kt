package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.intellij.ui.JBColor
import com.intellij.ui.JBColor.BLUE
import com.intellij.ui.JBColor.GRAY
import com.tecknobit.envui.com.tecknobit.envui.ui.components.BadgeLabel
import com.tecknobit.envui.com.tecknobit.envui.ui.components.BadgeTitle
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.com.tecknobit.envui.ui.utils.resolveIcon
import com.tecknobit.envui.com.tecknobit.envui.ui.utils.toComposeColor
import com.tecknobit.envui.com.tecknobit.envui.util.revealInProjectView
import com.tecknobit.envui.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.icon.IconKey
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@Composable
fun EnvSourceCard(
    modifier: Modifier = Modifier,
    envSource: EnvSource,
    shape: Shape = RoundedCornerShape(
        size = 12.dp
    ),
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .clip(
                shape = shape,
            )
            .border(
                width = 2.dp,
                color = JBColor
                    .border()
                    .toComposeColor(),
                shape = shape
            )
            .clickable(
                enabled = onClick != null,
                onClick = onClick ?: {}
            )
    ) {
        Column(
            modifier = Modifier
                .padding(
                    all = 10.dp
                )
        ) {
            CardHeader(
                envSource = envSource
            )

            ParentFolder(
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        start = 5.dp
                    ),
                envSource = envSource
            )
        }
    }
}

@Composable
private fun CardHeader(
    envSource: EnvSource,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(2f)
        ) {
            envSource.module?.let {
                BadgeTitle(
                    icon = {
                        Icon(
                            key = AllIconsKeys.Nodes.Module,
                            contentDescription = stringResource(Res.string.env_source_module)
                        )
                    },
                    text = it.name,
                    color = BLUE.toComposeColor(),
                    onClick = {
                        it.revealInProjectView(
                            project = envSource.project
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                onClick = {
                    // TODO: TO OPEN DIALOG
                }
            ) {
                Icon(
                    key = AllIconsKeys.Actions.Preview,
                    contentDescription = stringResource(Res.string.envui_card_open_source)
                )
            }
        }
    }
}

@Composable
private fun ParentFolder(
    modifier: Modifier = Modifier,
    envSource: EnvSource,
) {
    val project = envSource.project
    val moduleContainer = envSource.module?.name
    val parentFolder = envSource.containerFolder

    var iconKey: IconKey by remember { mutableStateOf(AllIconsKeys.Nodes.Folder) }
    LaunchedEffect(envSource.path) {
        iconKey = parentFolder.resolveIcon(
            project = project
        )
    }

    Row(
        modifier = modifier
    ) {
        val name = parentFolder?.name

        BadgeLabel(
            icon = {
                Icon(
                    key = iconKey,
                    contentDescription = stringResource(Res.string.env_source_parent_folder)
                )
            },
            text = if (parentFolder == null || moduleContainer == name)
                stringResource(Res.string.envui_card_module_root)
            else
                name!!,
            color = GRAY.toComposeColor(),
            onClick = {
                parentFolder.revealInProjectView(
                    project = project
                )
            }
        )
    }
}