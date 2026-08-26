package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.states

import com.tecknobit.envui.ide.services.EnvSourcePreferences

/**
 * The `CriticalEnvSourcesWarningState` class is useful to expose the environment sources containing critical values
 *
 * @property criticalEnvSources The preferences of the environment sources containing critical values
 *
 * @author N7ghtm4r3 - Tecknobit
 */
data class CriticalEnvSourcesWarningState(
    val criticalEnvSources: List<EnvSourcePreferences> = emptyList()
)