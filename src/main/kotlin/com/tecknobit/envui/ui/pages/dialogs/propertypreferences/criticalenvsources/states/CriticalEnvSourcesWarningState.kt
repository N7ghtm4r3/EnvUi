package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.states

import com.tecknobit.envui.ide.services.EnvSourcePreferences

data class CriticalEnvSourcesWarningState(
    val criticalEnvSources: List<EnvSourcePreferences> = emptyList()
)