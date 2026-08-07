package com.tecknobit.envui.ide.highlighters.icons

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.tecknobit.envui.I18nMessageBundle
import javax.swing.Icon

class ResetOnCloseGutterIcon : GutterIconRenderer() {

    override fun getIcon(): Icon {
        return AllIcons.General.Reset
    }

    override fun getTooltipText(): String {
        return I18nMessageBundle.message(
            key = "reset.value.on.close"
        )
    }

    override fun getAlignment(): Alignment {
        return Alignment.LEFT
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

}