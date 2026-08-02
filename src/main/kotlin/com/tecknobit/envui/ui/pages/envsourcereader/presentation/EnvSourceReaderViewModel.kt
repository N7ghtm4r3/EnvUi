package com.tecknobit.envui.ui.pages.envsourcereader.presentation

import androidx.lifecycle.ViewModel
import com.tecknobit.envui.ui.enums.EnvFieldType
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.ui.pages.envsourcereader.states.EnvSourceReaderState
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EnvSourceReaderViewModel(
    private val envSource: EnvSource,
) : ViewModel() {

    private val _dialogState = MutableStateFlow(
        value = EnvSourceReaderState()
    )
    val dialogState = _dialogState.asStateFlow()

    fun mapSourceTemplate() {
        val template = envSource.psiEnvTemplateSource
        if (template == null) {
            TODO("TO CREATE")
        }

        val properties = template.properties()
        val mappedProperties = mutableListOf<EnvTemplateField>()
        properties.forEach { property ->
            mappedProperties.add(
                EnvTemplateField(
                    key = property.keyEntry.text,
                    type = EnvFieldType.STRING
                )
            )
        }

        _dialogState.update {
            it.copy(
                template = EnvSourceTemplate(
                    fields = mappedProperties
                )
            )
        }
    }

    fun saveNewTemplate(
        template: EnvSourceTemplate,
    ) {
        _dialogState.update {
            it.copy(
                template = template
            )
        }
    }

}
