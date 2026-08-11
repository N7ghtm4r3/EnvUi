package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalproperty.presenter

import androidx.compose.runtime.Composable
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.ui.components.EnvUiDialog
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalproperty.presentation.CriticalPropertyWarningViewModel

class CriticalPropertyWarningDialog : EnvUiDialog<CriticalPropertyWarningViewModel>(
    viewModel = CriticalPropertyWarningViewModel(),
    title = I18nMessageBundle.message("critical.properties.changed", 1)
) {

    @Composable
    override fun DialogContent() {
    }

}