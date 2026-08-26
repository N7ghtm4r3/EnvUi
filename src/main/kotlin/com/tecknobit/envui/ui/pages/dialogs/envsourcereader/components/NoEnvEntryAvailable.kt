package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.no_entry_available
import com.tecknobit.envui.ui.components.EmptyState
import org.jetbrains.jewel.ui.icons.AllIconsKeys

/**
 * Component used to display the empty state of an environment source or template
 *
 * @param modifier The modifier to apply to the empty state
 * @param action The optional action displayed below the empty-state message
 */
@Composable
fun NoEnvEntryAvailable(
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
) {
    EmptyState(
        modifier = modifier,
        icon = AllIconsKeys.Actions.Cancel,
        title = Res.string.no_entry_available,
        action = action
    )
}