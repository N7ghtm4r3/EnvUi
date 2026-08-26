package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presentation

import androidx.lifecycle.ViewModel
import com.intellij.openapi.application.runWriteAction
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile.Companion.ENV_TEMPLATE_FILENAME
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.states.EnvSourceReaderState
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.updateKeysFromTemplate
import com.tecknobit.envui.utils.updateSourceFromTemplate
import com.tecknobit.envui.utils.writeKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The **EnvSourceReaderViewModel** class is the support class used to map, create, and save environment templates
 *
 * @property envSource The environment source managed by the reader
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class EnvSourceReaderViewModel(
    private val envSource: EnvSource,
) : ViewModel() {

    /**
     * `_dialogState` the mutable state used to manage the environment source template
     */
    private val _dialogState = MutableStateFlow(
        value = EnvSourceReaderState()
    )
    /**
     * `dialogState` the read-only state exposed to the environment source reader dialog
     */
    val dialogState = _dialogState.asStateFlow()

    init {
        if (envSource.isResolvedFromTemplate)
            mapSourceTemplate()
    }

    /**
     * Method used to create or read the source template and map its fields into the dialog state
     */
    fun mapSourceTemplate() {
        var template = envSource.psiEnvTemplateSource
        if (template == null) {
            template = createTemplateFile()
            envSource.psiEnvTemplateSource = template
        }

        val properties = template.properties()
        val mappedProperties = mutableListOf<EnvTemplateField>()

        envSource.useEnvSourcePreferencesManager {
            properties.forEach { property ->
                val key = property.keyEntry.text
                val type = retrievePropertyType(
                    source = envSource.source,
                    key = key
                )

                mappedProperties.add(
                    EnvTemplateField(
                        key = key,
                        type = type
                    )
                )
            }
        }

        _dialogState.update {
            it.copy(
                template = EnvSourceTemplate(
                    fields = mappedProperties
                )
            )
        }
    }

    /**
     * Method used to retrieve or create the template file associated with the environment source
     *
     * @return the associated environment template `PSI` file as [dEnvTemplateFile]
     */
    private fun createTemplateFile(): dEnvTemplateFile {
        val envSource = envSource.psiEnvSource
        val directory = requireNotNull(envSource.containingDirectory)
        val existingTemplate = directory.findFile(ENV_TEMPLATE_FILENAME)
        if (existingTemplate != null)
            return existingTemplate as dEnvTemplateFile

        return runWriteAction {
            val template = directory.createFile(ENV_TEMPLATE_FILENAME) as dEnvTemplateFile
            template.writeKeys()

            template
        }
    }

    /**
     * Method used to save a new template and apply its structure to the environment source
     *
     * @param template The environment template to save
     */
    fun saveNewTemplate(
        template: EnvSourceTemplate,
    ) {
        _dialogState.update {
            it.copy(
                template = template
            )
        }

        envSource.psiEnvTemplateSource!!.updateKeysFromTemplate(
            templateKeys = template.fields
        )

        envSource.psiEnvSource.updateSourceFromTemplate(
            template = template
        )
    }

}
