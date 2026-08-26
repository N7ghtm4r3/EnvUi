package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.states

import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvSourceTemplate

/**
 * The `EnvSourceReaderState` class is useful to expose the template edited by the environment source reader
 *
 * @property template The current environment source template
 *
 * @author N7ghtm4r3 - Tecknobit
 */
data class EnvSourceReaderState(
    val template: EnvSourceTemplate = EnvSourceTemplate(),
)
