package com.tecknobit.envui.components.envsource

import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBList
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.components.EnvUiComponent
import com.tecknobit.envui.data.EnvSource
import com.tecknobit.envui.theme.bordersWithInnerPadding
import javax.swing.DefaultListModel
import javax.swing.ListCellRenderer

class EnvSourcesList(
    private val sources: List<EnvSource>,
) : JBList<EnvSource>(), EnvUiComponent {

    private val listModel = DefaultListModel<EnvSource>()

    init {
        configureComponent()

        setupTheme()

        arrangeContent()
    }

    override fun configureComponent() {
        emptyText.text = I18nMessageBundle.message("envui.no.sources.message")

        emptyText.secondaryComponent.append(
            I18nMessageBundle.message("envui.no.sources.action.message"),
            SimpleTextAttributes.LINK_ATTRIBUTES
        ) {
            //TODO: TO ADD REAL ACTION
        }
    }

    override fun setupTheme() {
        border = bordersWithInnerPadding()
    }

    override fun arrangeContent() {
        attachModel()
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