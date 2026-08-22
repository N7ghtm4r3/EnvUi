package com.tecknobit.envui.plugin

import androidx.compose.ui.text.input.KeyboardType
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile
import com.tecknobit.envui.ide.listeners.IdeLifecycleListener
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ide.services.EnvSourcePropertyPreferences
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presentation.EnvSourceReaderViewModel
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.states.EnvSourceReaderState
import com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.presentation.CriticalEnvSourcesWarningViewModel
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.ui.pages.envuiwindow.presentation.EnvUiWindowViewModel
import com.tecknobit.envui.ui.pages.envuiwindow.states.EnvUiWindowState
import com.tecknobit.envui.utils.converters.toKeyboardType
import java.io.File
import java.nio.file.Files

class EnvUiPresentationTest : BasePlatformTestCase() {

    private val physicalSources = mutableListOf<Pair<VirtualFile, File>>()

    override fun tearDown() {
        try {
            physicalSources.forEach { (source, directory) ->
                project.useEnvSourcePreferencesManager {
                    deleteAllSourcePreferences(source)
                }
                FileUtil.delete(directory)
            }
        } finally {
            super.tearDown()
        }
    }

    fun `test window and reader states expose stable empty defaults`() {
        val windowState = EnvUiWindowState()
        val readerState = EnvSourceReaderState()

        assertNull(windowState.sources)
        assertEquals("", windowState.query.value)
        windowState.query.value = "backend"
        assertEquals("backend", windowState.query.value)
        assertTrue(readerState.template.fields.isEmpty())
        assertTrue(readerState.template.removedFields.isEmpty())
    }

    fun `test field types select the keyboard expected by the property editor`() {
        assertEquals(KeyboardType.Text, EnvFieldType.STRING.toKeyboardType())
        assertEquals(KeyboardType.Number, EnvFieldType.INTEGER.toKeyboardType())
        assertEquals(KeyboardType.Number, EnvFieldType.LONG.toKeyboardType())
        assertEquals(KeyboardType.Decimal, EnvFieldType.FLOAT.toKeyboardType())
        assertEquals(KeyboardType.Decimal, EnvFieldType.DOUBLE.toKeyboardType())
        assertEquals(KeyboardType.Text, EnvFieldType.JSON.toKeyboardType())
        assertEquals(KeyboardType.Unspecified, EnvFieldType.ANY.toKeyboardType())
    }

    fun `test window view model applies its query and publishes filtered sources`() {
        myFixture.addFileToProject("backend/.env", "HOST=backend")
        myFixture.addFileToProject("backend/.env.template", "HOST=")
        myFixture.addFileToProject("frontend/.env", "HOST=frontend")
        myFixture.addFileToProject("frontend/.env.template", "HOST=")
        val viewModel = EnvUiWindowViewModel(project)

        viewModel.filterSources("backend")
        PlatformTestUtil.waitWithEventsDispatching(
            "EnvUi sources to be retrieved",
            { viewModel.windowState.value.sources != null },
            5
        )

        assertEquals("backend", viewModel.windowState.value.query.value)
        assertEquals(
            listOf("backend"),
            viewModel.windowState.value.sources!!.map { it.containerFolder!!.name }
        )
    }

    fun `test critical warning view model publishes the requested source diffs`() {
        val criticalProperty = EnvSourcePropertyPreferences(
            key = "TOKEN",
            isCritical = true,
            initialValue = "old",
            currentValue = "new"
        )
        val sourcePreferences = EnvSourcePreferences(
            sourcePath = "/project/.env",
            properties = mapOf("TOKEN" to criticalProperty)
        )

        val viewModel = CriticalEnvSourcesWarningViewModel(
            project = project,
            criticalEnvSources = listOf(sourcePreferences)
        )

        assertEquals(listOf(sourcePreferences), viewModel.uiState.value.criticalEnvSources)
    }

    fun `test reader view model maps template keys into dialog state`() {
        val envSource = envSource(
            sourceContent = "HOST=localhost\nPORT=8080",
            templateContent = "HOST=\nPORT="
        )
        val viewModel = EnvSourceReaderViewModel(envSource)

        viewModel.mapSourceTemplate()

        assertEquals(
            listOf(
                EnvTemplateField("HOST", EnvFieldType.ANY),
                EnvTemplateField("PORT", EnvFieldType.ANY)
            ),
            viewModel.dialogState.value.template.fields
        )
    }

    fun `test reader view model creates an empty template when it is missing`() {
        val source = myFixture.addFileToProject(
            "without-template/.env",
            "HOST=localhost\nPORT=8080"
        ) as dEnvFile
        val envSource = EnvSource(
            project = project,
            source = source.virtualFile,
            module = null,
            _psiSource = source,
            isResolvedFromTemplate = false
        )
        val viewModel = EnvSourceReaderViewModel(envSource)

        viewModel.mapSourceTemplate()

        val createdTemplate = envSource.psiEnvTemplateSource
        assertNotNull(createdTemplate)
        assertEquals(".env.template", createdTemplate!!.name)
        assertEquals("", createdTemplate.text)
        assertTrue(viewModel.dialogState.value.template.fields.isEmpty())
    }

    fun `test reader view model saves template state and applies it to both files`() {
        val envSource = envSource(
            sourceContent = "HOST=localhost\nPORT=8080",
            templateContent = "HOST=\nPORT="
        )
        val viewModel = EnvSourceReaderViewModel(envSource)
        val updatedTemplate = EnvSourceTemplate(
            fields = listOf(
                EnvTemplateField("HOST", EnvFieldType.STRING),
                EnvTemplateField("PORT", EnvFieldType.INTEGER),
                EnvTemplateField("FEATURE_FLAG", EnvFieldType.STRING)
            )
        )

        viewModel.saveNewTemplate(updatedTemplate)

        assertEquals(updatedTemplate, viewModel.dialogState.value.template)
        assertEquals("HOST=\nPORT=\nFEATURE_FLAG=", envSource.psiEnvTemplateSource!!.text)
        assertEquals("HOST=localhost\nPORT=8080\nFEATURE_FLAG=\n", envSource.psiEnvSource.text)
    }

    fun `test critical warning view model accepts the current value as baseline`() {
        val source = physicalEnvFile("TOKEN=old")
        val property = source.findPropertyByKey("TOKEN")!!
        val sourcePreferences = project.useEnvSourcePreferencesManager {
            setPropertyPreference(source.virtualFile, property) {
                it.copy(
                    isCritical = true,
                    initialValue = "old",
                    currentValue = "new"
                )
            }
            retrieveEnvSourcePreferences(source.virtualFile)!!
        }
        val propertyPreferences = sourcePreferences.properties.getValue("TOKEN")
        val viewModel = CriticalEnvSourcesWarningViewModel(
            project = project,
            criticalEnvSources = listOf(sourcePreferences)
        )

        viewModel.acceptNewPropertyValue(
            sourcePath = source.virtualFile.path,
            envSourcePreferences = sourcePreferences,
            propertyPreferences = propertyPreferences
        )
        PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()

        assertTrue(viewModel.uiState.value.criticalEnvSources.isEmpty())
        project.useEnvSourcePreferencesManager {
            val accepted = retrievePropertyPreferences(source.virtualFile, "TOKEN")
            assertEquals("new", accepted.initialValue)
            assertEquals("new", accepted.currentValue)
            assertEquals(-1L, accepted.lastUpdateAt)
        }
        assertEquals("TOKEN=new", source.text)
    }

    fun `test critical warning view model restores the initial value`() {
        val source = physicalEnvFile("TOKEN=new")
        val property = source.findPropertyByKey("TOKEN")!!
        val sourcePreferences = project.useEnvSourcePreferencesManager {
            setPropertyPreference(source.virtualFile, property) {
                it.copy(
                    isCritical = true,
                    initialValue = "old",
                    currentValue = "new"
                )
            }
            retrieveEnvSourcePreferences(source.virtualFile)!!
        }
        val propertyPreferences = sourcePreferences.properties.getValue("TOKEN")
        val viewModel = CriticalEnvSourcesWarningViewModel(
            project = project,
            criticalEnvSources = listOf(sourcePreferences)
        )

        viewModel.revertPropertyValue(
            sourcePath = source.virtualFile.path,
            envSourcePreferences = sourcePreferences,
            propertyPreferences = propertyPreferences
        )
        PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()

        assertTrue(viewModel.uiState.value.criticalEnvSources.isEmpty())
        project.useEnvSourcePreferencesManager {
            val reverted = retrievePropertyPreferences(source.virtualFile, "TOKEN")
            assertEquals("old", reverted.initialValue)
            assertEquals("old", reverted.currentValue)
            assertEquals(-1L, reverted.lastUpdateAt)
        }
        assertEquals("TOKEN=old", source.text)
    }

    fun `test project closing restores changed resettable properties`() {
        val source = physicalEnvFile("TOKEN=new")
        val property = source.findPropertyByKey("TOKEN")!!
        project.useEnvSourcePreferencesManager {
            setPropertyPreference(source.virtualFile, property) {
                it.copy(
                    requireResetOnClose = true,
                    initialValue = "old",
                    currentValue = "new"
                )
            }
        }

        IdeLifecycleListener().projectClosingBeforeSave(project)

        assertEquals("TOKEN=old", source.text)
        project.useEnvSourcePreferencesManager {
            val restored = retrievePropertyPreferences(source.virtualFile, "TOKEN")
            assertEquals("old", restored.initialValue)
            assertEquals("old", restored.currentValue)
            assertEquals(-1L, restored.lastUpdateAt)
            assertTrue(retrieveAllResettableOnCloseEnvSourcePreferences().isEmpty())
        }
    }

    private fun envSource(
        sourceContent: String,
        templateContent: String,
    ): EnvSource {
        val source = myFixture.addFileToProject(".env", sourceContent) as dEnvFile
        val template = myFixture.addFileToProject(".env.template", templateContent) as dEnvTemplateFile

        return EnvSource(
            project = project,
            source = source.virtualFile,
            module = null,
            _psiSource = source,
            _templateSource = template,
            isResolvedFromTemplate = false
        )
    }

    private fun physicalEnvFile(content: String): dEnvFile {
        val directory = Files.createTempDirectory("envui-test-").toFile()
        val path = directory.toPath().resolve(".env")
        Files.writeString(path, content)
        val virtualFile = VfsUtil.findFile(path, true)!!
        physicalSources.add(virtualFile to directory)

        return PsiManager.getInstance(project).findFile(virtualFile) as dEnvFile
    }

}
