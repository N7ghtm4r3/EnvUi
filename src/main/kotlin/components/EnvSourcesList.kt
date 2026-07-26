package com.tecknobit.envui.components

import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBList
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.data.EnvSource
import javax.swing.DefaultListModel

class EnvSourcesList(
    private val sources: List<EnvSource>,
) : JBList<EnvSource>() {

    private val listModel = DefaultListModel<EnvSource>()

    init {
        configComponent()

        attachModel()
    }

    private fun configComponent() {
        emptyText.text = I18nMessageBundle.message("envui.no.sources.message")

        emptyText.secondaryComponent.append(
            I18nMessageBundle.message("envui.no.sources.action.message"),
            SimpleTextAttributes.LINK_ATTRIBUTES
        ) {
            //TODO: TO ADD REAL ACTION
        }
    }

    private fun attachModel() {
        listModel.addAll(sources)

        model = listModel
    }

}