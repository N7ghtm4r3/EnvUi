package com.tecknobit.envui.ui.pages.envuiwindow.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intellij.ui.JBColor.GRAY
import com.tecknobit.envui.generated.resources.*
import com.tecknobit.envui.ui.components.Badge
import com.tecknobit.envui.ui.components.Card
import com.tecknobit.envui.ui.components.CardShape
import com.tecknobit.envui.ui.components.ModuleBadge
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.ui.utils.resolveIcon
import com.tecknobit.envui.ui.utils.toComposeColor
import com.tecknobit.envui.ui.utils.toDateString
import com.tecknobit.envui.utils.revealInProjectView
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icon.IconKey
import org.jetbrains.jewel.ui.icons.AllIconsKeys

/**
 * Component used to display an environment source summary and its navigation actions
 *
 * @param modifier The modifier to apply to the card
 * @param envSource The environment source summarized by the card
 * @param shape The shape of the card
 * @param onClick The optional callback invoked when the card is clicked
 */
@Composable
fun EnvSourceCard(
    modifier: Modifier = Modifier,
    envSource: EnvSource,
    shape: Shape = CardShape,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = shape,
        onClick = onClick
    ) {
        CardHeader(
            envSource = envSource
        )

        CardContent(
            modifier = Modifier
                .padding(
                    top = 15.dp
                ),
            envSource = envSource
        )
    }
}

/**
 * Section used to display the source module and dialog preview action
 *
 * @param envSource The environment source displayed by the header
 */
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
                ModuleBadge(
                    module = it,
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
                    val envUiReaderDialog = EnvSourceReaderDialog(
                        envSource = envSource
                    )

                    envUiReaderDialog.show()
                }
            ) {
                Icon(
                    modifier = Modifier
                        .size(26.dp),
                    key = AllIconsKeys.Actions.Preview,
                    contentDescription = stringResource(Res.string.envui_card_open_source)
                )
            }
        }
    }
}

/**
 * Section used to display the source container folder and latest edit time
 *
 * @param modifier The modifier to apply to the content
 * @param envSource The environment source displayed by the content
 */
@Composable
private fun CardContent(
    modifier: Modifier = Modifier,
    envSource: EnvSource,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ParentFolder(
            envSource = envSource
        )

        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            LastEdit(
                envSource = envSource
            )
        }
    }
}

/**
 * Badge used to display and reveal the container folder of an environment source
 *
 * @param modifier The modifier to apply to the folder badge
 * @param envSource The environment source whose container is displayed
 */
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
        iconKey = parentFolder!!.resolveIcon(
            project = project
        )
    }

    Row(
        modifier = modifier
    ) {
        val name = parentFolder?.name

        Badge(
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
            textSize = 16.sp,
            color = GRAY.toComposeColor(),
            onClick = {
                parentFolder!!.revealInProjectView(
                    project = project
                )
            },
        )
    }
}

/**
 * Section used to display the latest edit time of an environment source
 *
 * @param envSource The environment source whose timestamp is displayed
 */
@Composable
private fun LastEdit(
    envSource: EnvSource,
) {
    val source = envSource.source
    val lastEditText = stringResource(Res.string.last_edit)

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                key = AllIconsKeys.General.History,
                contentDescription = lastEditText
            )

            Text(
                text = lastEditText,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = source.timeStamp.toDateString(),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
