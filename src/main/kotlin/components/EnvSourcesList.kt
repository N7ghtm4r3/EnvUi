package com.tecknobit.envui.components

import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBList
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.data.EnvSource
import com.tecknobit.envui.theme.bordersWithInnerPadding
import javax.swing.DefaultListModel
import javax.swing.ListCellRenderer

class EnvSourcesList(
    private val sources: List<EnvSource>,
) : JBList<EnvSource>() {

    private val listModel = DefaultListModel<EnvSource>()

    init {
        setupTheme()

        configureComponent()

        attachModel()
    }

    private fun setupTheme() {
        border = bordersWithInnerPadding()
    }

    private fun configureComponent() {
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
        cellRenderer = obtainCellRender()
    }

    private fun obtainCellRender(): ListCellRenderer<EnvSource> {
        return ListCellRenderer { list, envSource, index, isSelected, cellHasFocus ->
            EnvSourceCard(
                envSource = envSource
            ).apply {
                accessibleContext.accessibleName = envSource.name
                accessibleContext.accessibleDescription = envSource.path
            }
        }
    }

}