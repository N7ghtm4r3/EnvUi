package com.tecknobit.envui.ide.highlighters.icons

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.tecknobit.envui.I18nMessageBundle
import javax.swing.Icon

/**
 * The `CriticalEnvGutterIcon` class is useful to render the gutter marker of a critical environment property
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class CriticalEnvGutterIcon : GutterIconRenderer() {

    /**
     * Method used to retrieve the warning icon
     *
     * @return the warning icon as [Icon]
     */
    override fun getIcon(): Icon {
        return AllIcons.General.Warning
    }

    /**
     * Method used to retrieve the localized marker tooltip
     *
     * @return the localized marker tooltip as [String]
     */
    override fun getTooltipText(): String {
        return I18nMessageBundle.message(
            key = "mark.as.critical.to.change"
        )
    }

    /**
     * Method used to retrieve the gutter alignment
     *
     * @return the left gutter alignment as [Alignment]
     */
    override fun getAlignment(): Alignment {
        return Alignment.LEFT
    }

    /**
     * Method used to check whether another value is this renderer instance
     *
     * @param other The value to compare
     *
     * @return whether the value is this instance as [Boolean]
     */
    override fun equals(other: Any?): Boolean {
        return this === other
    }

    /**
     * Method used to retrieve the identity hash code of this renderer
     *
     * @return the identity hash code as [Int]
     */
    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

}