package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envsourcereader.presenter

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intellij.openapi.ui.DialogWrapper
import com.tecknobit.envui.com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.com.tecknobit.envui.ui.helpers.StringResourcesProvider
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envsourcereader.components.EnvTemplateFielsEditor
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envsourcereader.presentation.EnvSourceReaderViewModel
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envsourcereader.states.EnvSourceReaderState
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.source
import com.tecknobit.envui.generated.resources.template
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.bridge.compose
import org.jetbrains.jewel.ui.component.SegmentedControl
import org.jetbrains.jewel.ui.component.SegmentedControlButtonData
import org.jetbrains.jewel.ui.component.Text
import java.awt.Dimension
import javax.swing.JComponent

class EnvSourceReaderDialog(
    envSource: EnvSource,
) : DialogWrapper(
    true
) {

    private val viewModel = EnvSourceReaderViewModel(
        envSource = envSource
    )

    private lateinit var dialogState: EnvSourceReaderState

    init {
        title = I18nMessageBundle.message(
            key = "envui.dialog.read.env",
            envSource.module!!.name
        )

        super.init()
    }

    override fun createCenterPanel(): JComponent {
        return compose(
            focusOnClickInside = true,
            config = {
                preferredSize = Dimension(600, 500)
            }
        ) {
            StringResourcesProvider(
                context = EnvSourceReaderDialog::class,
                content = {
                    dialogState = viewModel.dialogState.collectAsStateWithLifecycle().value
                    val workingOnSource = remember { mutableStateOf(true) }

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
                                    all = 16.dp
                                ),
                            targetState = workingOnSource.value
                        ) { workingOnSource ->
                            if (workingOnSource)
                                SourceContent()
                            else
                                TemplateContent()
                        }
                    }
                }
            )
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
                    onSelect = { workingOnSource.value = false },
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
    private fun SourceContent() {
        Text(
            text = "g"
        )
    }

    @Composable
    private fun TemplateContent() {
        viewModel.mapSourceTemplate()
        val sourceTemplate = dialogState.template

        Column {
            Text(
                text = "Create template"
            )

            EnvTemplateFielsEditor(
                envSourceTemplate = sourceTemplate,
                onSave = { newTemplate ->

                }
            )
        }
    }

}