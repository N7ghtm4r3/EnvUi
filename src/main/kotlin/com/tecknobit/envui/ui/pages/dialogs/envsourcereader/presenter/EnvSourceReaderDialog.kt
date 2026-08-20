package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.generated.resources.*
import com.tecknobit.envui.ui.components.EnvUiDialog
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.components.EnvSourceContent
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.components.EnvTemplateFieldsEditor
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presentation.EnvSourceReaderViewModel
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.states.EnvSourceReaderState
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.SegmentedControl
import org.jetbrains.jewel.ui.component.SegmentedControlButtonData
import org.jetbrains.jewel.ui.component.Text

class EnvSourceReaderDialog(
    private val envSource: EnvSource,
) : EnvUiDialog<EnvSourceReaderViewModel>(
    viewModel = EnvSourceReaderViewModel(
        envSource = envSource
    ),
    title = I18nMessageBundle.message(
        key = "envui.dialog.read.env",
        envSource.module!!.name
    )
) {

    @Composable
    override fun DialogContent() {
        val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
        val workingOnSource = remember { mutableStateOf(!envSource.isResolvedFromTemplate) }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TabControls(
                workingOnSource = workingOnSource
            )

            AnimatedContent(
                modifier = Modifier
                    .padding(
                        top = 16.dp
                    ),
                targetState = workingOnSource.value
            ) { isWorkingOnSource ->
                if (isWorkingOnSource) {
                    SourceContent(
                        workingOnSource = workingOnSource
                    )
                } else {
                    TemplateContent(
                        dialogState = dialogState
                    )
                }
            }
        }
    }

    @Composable
    private fun TabControls(
        workingOnSource: MutableState<Boolean>,
    ) {
        SegmentedControl(
            buttons = listOf(
                SegmentedControlButtonData(
                    selected = workingOnSource.value,
                    onSelect = { workingOnSource.value = true },
                    content = {
                        Text(
                            text = stringResource(Res.string.source)
                        )
                    }
                ),
                SegmentedControlButtonData(
                    selected = !workingOnSource.value,
                    onSelect = {
                        viewModel.mapSourceTemplate()

                        workingOnSource.value = false
                    },
                    content = {
                        Text(
                            text = stringResource(Res.string.template)
                        )
                    }
                )
            )
        )
    }

    @Composable
    private fun SourceContent(
        workingOnSource: MutableState<Boolean>
    ) {
        Column {
            Text(
                text = stringResource(Res.string.manage_source),
                fontWeight = FontWeight.Bold
            )

            EnvSourceContent(
                envSource = envSource,
                onEmptyAction = {
                    workingOnSource.value = false
                }
            )
        }
    }

    @Composable
    private fun TemplateContent(
        dialogState: EnvSourceReaderState
    ) {
        Column {
            Text(
                text = stringResource(Res.string.manage_template),
                fontWeight = FontWeight.Bold
            )

            EnvTemplateFieldsEditor(
                envSourceTemplate = dialogState.template,
                onSave = { newTemplate ->
                    viewModel.saveNewTemplate(
                        template = newTemplate
                    )
                }
            )
        }
    }

}
