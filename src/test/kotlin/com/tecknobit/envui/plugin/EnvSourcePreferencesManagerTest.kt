package com.tecknobit.envui.plugin

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ide.services.EnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.writeContent

class EnvSourcePreferencesManagerTest : BasePlatformTestCase() {

    private lateinit var manager: EnvSourcePreferencesManager
    private lateinit var file: dEnvFile
    private lateinit var sourceProperty: Property

    override fun setUp() {
        super.setUp()
        manager = EnvSourcePreferencesManager()
        myFixture.configureByText(dEnvFileType, "TOKEN=initial\nPORT=8080")
        file = myFixture.file as dEnvFile
        sourceProperty = file.findPropertyByKey("TOKEN")!!
    }

    fun `test missing preferences use the source value without persisting state`() {
        val preferences = manager.retrievePropertyPreferences(file.virtualFile, sourceProperty)

        assertEquals("TOKEN", preferences.key)
        assertEquals("initial", preferences.initialValue)
        assertEquals("initial", preferences.currentValue)
        assertEquals(EnvFieldType.ANY, preferences.type)
        assertNull(manager.retrieveEnvSourcePreferences(file.virtualFile))
    }

    fun `test value lifecycle tracks changes and accepts a new baseline`() {
        manager.setPropertyValue(file.virtualFile, sourceProperty, "initial")
        var preferences = manager.retrievePropertyPreferences(file.virtualFile, sourceProperty)
        assertEquals(-1L, preferences.lastUpdateAt)

        manager.setPropertyValue(file.virtualFile, sourceProperty, "changed")
        preferences = manager.retrievePropertyPreferences(file.virtualFile, sourceProperty)
        assertEquals("initial", preferences.initialValue)
        assertEquals("changed", preferences.currentValue)
        assertTrue(preferences.lastUpdateAt > 0L)

        manager.acceptNewPropertyValue(file.virtualFile, sourceProperty)
        preferences = manager.retrievePropertyPreferences(file.virtualFile, sourceProperty)
        assertEquals("changed", preferences.initialValue)
        assertEquals("changed", preferences.currentValue)
        assertEquals(-1L, preferences.lastUpdateAt)
    }

    fun `test critical and resettable queries filter flags and unchanged values`() {
        manager.setPropertyValue(file.virtualFile, sourceProperty, "changed")
        manager.setPropertyCriticality(file.virtualFile, sourceProperty, true)
        manager.setPropertyResetOnClose(file.virtualFile, sourceProperty, true)

        assertEquals(1, manager.retrieveAllCriticalEnvSourcePreferences().size)
        assertEquals(1, manager.retrieveAllResettableOnCloseEnvSourcePreferences().size)

        manager.acceptNewPropertyValue(file.virtualFile, sourceProperty)

        assertTrue(manager.retrieveAllCriticalEnvSourcePreferences().isEmpty())
        assertEquals(1, manager.retrieveAllCriticalEnvSourcePreferences(excludeUnchanged = false).size)
        assertTrue(manager.retrieveAllResettableOnCloseEnvSourcePreferences().isEmpty())
        assertEquals(1, manager.retrieveAllResettableOnCloseEnvSourcePreferences(excludeUnchanged = false).size)
    }

    fun `test template synchronization creates types and clears values on concrete type change`() {
        manager.setPropertyValue(file.virtualFile, sourceProperty, "secret")
        val changes = mutableListOf<Pair<String, String>>()

        manager.upsertFromTemplate(
            source = file.virtualFile,
            envSourceTemplate = EnvSourceTemplate(
                fields = listOf(
                    EnvTemplateField("TOKEN", EnvFieldType.STRING),
                    EnvTemplateField("PORT", EnvFieldType.INTEGER)
                )
            ),
            onPropertyTypeChange = { key, value -> changes += key to value }
        )

        val token = manager.retrievePropertyPreferences(file.virtualFile, "TOKEN")
        val port = manager.retrievePropertyPreferences(file.virtualFile, "PORT")
        assertEquals(EnvFieldType.STRING, token.type)
        assertEquals("", token.initialValue)
        assertEquals("", token.currentValue)
        assertEquals(EnvFieldType.INTEGER, port.type)
        assertEquals(listOf("TOKEN" to ""), changes)
    }

    fun `test any template type preserves existing values`() {
        manager.setPropertyValue(file.virtualFile, sourceProperty, "changed")
        var callbackInvoked = false

        manager.upsertFromTemplate(
            source = file.virtualFile,
            envSourceTemplate = EnvSourceTemplate(
                fields = listOf(EnvTemplateField("TOKEN", EnvFieldType.ANY))
            ),
            onPropertyTypeChange = { _, _ -> callbackInvoked = true }
        )

        val preferences = manager.retrievePropertyPreferences(file.virtualFile, "TOKEN")
        assertEquals("initial", preferences.initialValue)
        assertEquals("changed", preferences.currentValue)
        assertFalse(callbackInvoked)
    }

    fun `test returned preference map is a deep defensive copy`() {
        manager.setPropertyValue(file.virtualFile, sourceProperty, "changed")

        val copy = manager.retrieveAllEnvSourcePreferences()
        copy.getValue(file.virtualFile.path).properties.getValue("TOKEN").currentValue = "tampered"

        assertEquals(
            "changed",
            manager.retrievePropertyPreferences(file.virtualFile, "TOKEN").currentValue
        )
    }

    fun `test deleting keys and sources only removes the requested state`() {
        val portProperty = file.findPropertyByKey("PORT")!!
        manager.setPropertyValue(file.virtualFile, sourceProperty, "changed")
        manager.setPropertyValue(file.virtualFile, portProperty, "9090")

        manager.deletePreferences(file.virtualFile, setOf("TOKEN", "UNKNOWN"))
        val remaining = manager.retrieveEnvSourcePreferences(file.virtualFile)!!
        assertEquals(setOf("PORT"), remaining.properties.keys)

        manager.deleteAllSourcePreferences(file.virtualFile)
        assertNull(manager.retrieveEnvSourcePreferences(file.virtualFile))
    }

    fun `test property type defaults to any and reflects template synchronization`() {
        assertEquals(
            EnvFieldType.ANY,
            manager.retrievePropertyType(file.virtualFile, "TOKEN")
        )

        manager.upsertFromTemplate(
            source = file.virtualFile,
            envSourceTemplate = EnvSourceTemplate(
                fields = listOf(EnvTemplateField("TOKEN", EnvFieldType.STRING))
            ),
            onPropertyTypeChange = { _, _ -> }
        )

        assertEquals(
            EnvFieldType.STRING,
            manager.retrievePropertyType(file.virtualFile, "TOKEN")
        )
        assertEquals(
            EnvFieldType.ANY,
            manager.retrievePropertyType(file.virtualFile, "UNKNOWN")
        )
    }

    fun `test template synchronization preserves preferences for fields outside the template`() {
        val portProperty = file.findPropertyByKey("PORT")!!
        manager.setPropertyValue(file.virtualFile, sourceProperty, "changed")
        manager.setPropertyValue(file.virtualFile, portProperty, "9090")
        manager.setPropertyCriticality(file.virtualFile, portProperty, true)

        manager.upsertFromTemplate(
            source = file.virtualFile,
            envSourceTemplate = EnvSourceTemplate(
                fields = listOf(EnvTemplateField("TOKEN", EnvFieldType.STRING))
            ),
            onPropertyTypeChange = { _, _ -> }
        )

        val portPreferences = manager.retrievePropertyPreferences(file.virtualFile, "PORT")
        assertEquals("8080", portPreferences.initialValue)
        assertEquals("9090", portPreferences.currentValue)
        assertTrue(portPreferences.isCritical)
    }

    fun `test source synchronization adds updates and removes stored properties`() {
        val envSource = EnvSource(
            project = project,
            source = file.virtualFile,
            module = null,
            _psiSource = file,
            isResolvedFromTemplate = false
        )

        manager.syncPreferencesFromSource(envSource)
        var properties = manager.retrieveEnvSourcePreferences(file.virtualFile)!!.properties
        assertEquals(setOf("TOKEN", "PORT"), properties.keys)
        assertEquals("initial", properties.getValue("TOKEN").initialValue)
        assertEquals("8080", properties.getValue("PORT").currentValue)

        file.writeContent("PORT=9090\nHOST=localhost")
        manager.syncPreferencesFromSource(envSource)

        properties = manager.retrieveEnvSourcePreferences(file.virtualFile)!!.properties
        assertEquals(setOf("PORT", "HOST"), properties.keys)
        assertEquals("8080", properties.getValue("PORT").initialValue)
        assertEquals("9090", properties.getValue("PORT").currentValue)
        assertEquals("localhost", properties.getValue("HOST").initialValue)
        assertEquals("localhost", properties.getValue("HOST").currentValue)
    }

}
