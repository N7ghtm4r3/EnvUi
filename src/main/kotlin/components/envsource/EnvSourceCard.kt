package com.tecknobit.envui.components.envsource

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.components.Badge
import com.tecknobit.envui.components.EnvUiComponent
import com.tecknobit.envui.components.addToPanel
import com.tecknobit.envui.data.EnvSource
import com.tecknobit.envui.theme.roundedBorder
import java.awt.BorderLayout
import javax.swing.JPanel


class EnvSourceCard(
    private val envSource: EnvSource,
) : JPanel(), EnvUiComponent {

    init {
        configureComponent()

        setupTheme()

        arrangeContent()
    }

    override fun configureComponent() {
        layout = BorderLayout()
    }

    override fun setupTheme() {
        border = roundedBorder()
    }

    override fun arrangeContent() {
        addToPanel {
            envSource.module?.let {
                JBLabel(it.name)
            }
        }

        addToPanel {
            if (envSource.isModuleRootLocated)
                JBLabel(I18nMessageBundle.lazyMessage("envui.card.module.root").get())
            else {
                Badge(
                    text = envSource.containerFolder!!.name,
                    color = JBColor.BLUE,
                    radius = 16
                )
            }
        }
    }

}