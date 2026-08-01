package com.tecknobit.envui.ui.pages.envsourcereader.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecknobit.envui.ui.enums.EnvFieldType
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.ui.pages.envsourcereader.states.EnvSourceReaderState
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EnvSourceReaderViewModel(
    private val envSource: EnvSource,
) : ViewModel() {

    private val _dialogState = MutableStateFlow(
        value = EnvSourceReaderState()
    )
    val dialogState = _dialogState.asStateFlow()

    fun mapSourceTemplate() {
        viewModelScope.launch {
            val properties = envSource.psiEnvSource.properties()
            val mappedProperties = mutableListOf<EnvTemplateField>()
            properties.forEach { property ->
                mappedProperties.add(
                    EnvTemplateField(
                        key = property.keyEntry.text,
                        type = EnvFieldType.STRING
                    )
                )
            }

            _dialogState.value.template.value = EnvSourceTemplate(
                fields = mappedProperties
            )
        }
    }

}
