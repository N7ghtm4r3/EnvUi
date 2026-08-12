package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presenter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.intellij.openapi.project.Project
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ui.components.EnvUiDialog
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.components.CriticalEnvSourcesList
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presentation.CriticalEnvSourcesWarningViewModel

class CriticalEnvSourcesWarningDialog(
    private val project: Project,
    private val criticalEnvSources: List<EnvSourcePreferences>
) : EnvUiDialog<CriticalEnvSourcesWarningViewModel>(
    viewModel = CriticalEnvSourcesWarningViewModel(
        project = project,
        criticalProperties = criticalEnvSources
    ),
    title = I18nMessageBundle.message("critical.env_sources.changed", criticalEnvSources.size)
) {

    @Composable
    override fun DialogContent() {
        CriticalEnvSourcesList(
            modifier = Modifier
                .fillMaxSize(),
            project = project,
            criticalEnvSources = criticalEnvSources
        )
    }

}