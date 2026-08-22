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

/**
 * The [CriticalEnvSourcesWarningDialog] displays changed critical properties and allows each difference to be resolved
 *
 * @property project The project containing the critical environment sources
 * @param criticalEnvSources The source preferences containing critical property changes
 *
 * @author N7ghtm4r3 - Tecknobit
 */
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

    /**
     * `dialogState` the collected state used to determine whether the dialog can close
     */
    private lateinit var dialogState: State<CriticalEnvSourcesWarningState>

    /**
     * The custom content displaying the unresolved critical property differences
     */
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

    /**
     * Method used to check whether the dialog can close from its window control
     *
     * @return whether every critical source difference is resolved as [Boolean]
     */
    override fun shouldCloseOnCross(): Boolean {
        return dialogState.value.criticalEnvSources.isEmpty()
    }

    /**
     * Method used to omit default dialog actions while differences require inline resolution
     *
     * @return the empty dialog action array as [Array] of [Action]
     */
    override fun createActions(): Array<out Action?> {
        return emptyArray()
    }

}