package com.tecknobit.envui.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.intellij.openapi.module.Module
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.env_source_module
import com.tecknobit.envui.ui.theme.EnvUiTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.icons.AllIconsKeys

/**
 * Badge used to display the name of a project module
 *
 * @param modifier The modifier to apply to the badge
 * @param module The module whose name is displayed
 * @param onClick The optional callback invoked when the badge is clicked
 */
@Composable
fun ModuleBadge(
    modifier: Modifier = Modifier,
    module: Module,
    onClick: (() -> Unit)? = null
) {
    ModuleBadge(
        modifier = modifier,
        module = module.name,
        onClick = onClick
    )
}

/**
 * Badge used to display a module name
 *
 * @param modifier The modifier to apply to the badge
 * @param module The module name to display
 * @param onClick The optional callback invoked when the badge is clicked
 */
@Composable
fun ModuleBadge(
    modifier: Modifier = Modifier,
    module: String,
    onClick: (() -> Unit)? = null
) {
    Badge(
        modifier = modifier,
        icon = {
            Icon(
                key = AllIconsKeys.Nodes.Module,
                contentDescription = stringResource(Res.string.env_source_module)
            )
        },
        text = module,
        textSize = 12.sp,
        color = EnvUiTheme.primary,
        onClick = onClick
    )
}