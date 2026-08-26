package com.tecknobit.envui.ide.theme

import com.intellij.openapi.util.IconLoader

/**
 * The `EnvUiIcons` object allows to access the icons used by the EnvUi plugin
 *
 * @author N7ghtm4r3 - Tecknobit
 */
object EnvUiIcons {

    /**
     * `EnvSource` the icon representing an environment source file
     */
    @JvmField
    val EnvSource = IconLoader.getIcon("/icons/envSource.svg", EnvUiIcons::class.java)

    /**
     * `EnvSourceTemplate` the icon representing an environment template file
     */
    @JvmField
    val EnvSourceTemplate = IconLoader.getIcon("/icons/envSourceTemplate.svg", EnvUiIcons::class.java)

    /**
     * `CreateEnvSource` the icon representing the environment source creation action
     */
    @JvmField
    val CreateEnvSource = IconLoader.getIcon("/icons/createEnvSource.svg", EnvUiIcons::class.java)

}