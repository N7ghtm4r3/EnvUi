package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presenter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intellij.openapi.project.Project
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ui.components.EnvUiDialog
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.components.CriticalEnvSourcesList
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presentation.CriticalEnvSourcesWarningViewModel
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.states.CriticalEnvSourcesWarningState
import javax.swing.Action

class CriticalEnvSourcesWarningDialog(
    private val project: Project,
    criticalEnvSources: List<EnvSourcePreferences>
) : EnvUiDialog<CriticalEnvSourcesWarningViewModel>(
    viewModel = CriticalEnvSourcesWarningViewModel(
        project = project,
        criticalEnvSources = criticalEnvSources
    ),
    title = I18nMessageBundle.message("critical.env_sources.changed", criticalEnvSources.size)
) {

    private lateinit var dialogState: State<CriticalEnvSourcesWarningState>

    @Composable
    override fun DialogContent() {
        dialogState = viewModel.uiState.collectAsStateWithLifecycle()

        CriticalEnvSourcesList(
            modifier = Modifier
                .fillMaxSize(),
            project = project,
            viewModel = viewModel
        )
    }

    override fun shouldCloseOnCross(): Boolean {
        return dialogState.value.criticalEnvSources.isEmpty()
    }

    override fun createActions(): Array<out Action?> {
        return emptyArray()
    }

}