package com.tecknobit.envui.components

import com.tecknobit.envui.theme.roundedBorder
import java.awt.Color
import javax.swing.JLabel

class Badge(
    private val text: String,
    private val color: Color,
    private val radius: Int = 8,
) : JLabel(), EnvUiComponent {

    init {
        configureComponent()

        setupTheme()

        arrangeContent()
    }

    override fun configureComponent() {
        isOpaque = true
    }

    override fun setupTheme() {
        border = roundedBorder(
            radius = radius,
            color = color
        )

        background = color.withAlpha(.1f)

        foreground = color
    }

    override fun arrangeContent() {
        super.text = text
    }

}