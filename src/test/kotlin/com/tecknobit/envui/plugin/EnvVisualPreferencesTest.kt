package com.tecknobit.envui.plugin

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tecknobit.envui.helpers.EnvSourceHighlightedPropertiesRegistry
import com.tecknobit.envui.helpers.EnvSourcePreferenceType
import com.tecknobit.envui.ide.highlighters.addCriticalEnvMark
import com.tecknobit.envui.ide.highlighters.addResetOnCloseMark
import com.tecknobit.envui.ide.highlighters.removeEnvMark
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

class EnvVisualPreferencesTest : BasePlatformTestCase() {

    fun `test visual preference types declare their mutual conflict`() {
        assertEquals(
            setOf(EnvSourcePreferenceType.RESET_ON_CLOSE),
            EnvSourcePreferenceType.CRITICAL.conflictualPreferences
        )
        assertEquals(
            setOf(EnvSourcePreferenceType.CRITICAL),
            EnvSourcePreferenceType.RESET_ON_CLOSE.conflictualPreferences
        )
    }

    fun `test gutter highlighters are reused by line and preference type`() {
        val file = envFile("TOKEN=value")
        val document = PsiDocumentManager.getInstance(project).getDocument(file)!!

        val critical = addCriticalEnvMark(document, project, line = 0)
        val repeatedCritical = addCriticalEnvMark(document, project, line = 0)
        val resettable = addResetOnCloseMark(document, project, line = 0)

        assertSame(critical, repeatedCritical)
        assertNotSame(critical, resettable)
        assertNotNull(critical.gutterIconRenderer)
        assertNotNull(resettable.gutterIconRenderer)

        removeEnvMark(critical)
        removeEnvMark(resettable)
        assertFalse(critical.isValid)
        assertFalse(resettable.isValid)
    }

    fun `test highlighted property registry stores retrieves and removes markers`() {
        val file = envFile("TOKEN=value")
        val envSource = envSource(file)
        val document = PsiDocumentManager.getInstance(project).getDocument(file)!!
        val highlighter = addCriticalEnvMark(document, project, line = 0)

        EnvSourceHighlightedPropertiesRegistry.markPropertyAsCritical(
            envSource = envSource,
            key = "TOKEN",
            highlighter = highlighter
        )

        assertSame(
            highlighter,
            EnvSourceHighlightedPropertiesRegistry.getPropertyHighlighter(
                envSource = envSource,
                key = "TOKEN",
                type = EnvSourcePreferenceType.CRITICAL
            )
        )

        EnvSourceHighlightedPropertiesRegistry.unmarkPropertyAsPrefType(
            envSource = envSource,
            key = "TOKEN",
            type = EnvSourcePreferenceType.CRITICAL
        )
        assertNull(
            EnvSourceHighlightedPropertiesRegistry.getPropertyHighlighter(
                envSource = envSource,
                key = "TOKEN",
                type = EnvSourcePreferenceType.CRITICAL
            )
        )
        removeEnvMark(highlighter)
    }

    fun `test toggling critical preference updates its marker and persisted flag`() {
        val file = envFile("TOKEN=value")
        val envSource = envSource(file)

        file.toggleMarkAsCritical("TOKEN", envSource)
        project.useEnvSourcePreferencesManager {
            assertTrue(retrievePropertyPreferences(file.virtualFile, "TOKEN").isCritical)
        }
        assertNotNull(
            EnvSourceHighlightedPropertiesRegistry.getPropertyHighlighter(
                envSource,
                "TOKEN",
                EnvSourcePreferenceType.CRITICAL
            )
        )

        file.toggleMarkAsCritical("TOKEN", envSource)
        project.useEnvSourcePreferencesManager {
            assertFalse(retrievePropertyPreferences(file.virtualFile, "TOKEN").isCritical)
            deleteAllSourcePreferences(file.virtualFile)
        }
        assertNull(
            EnvSourceHighlightedPropertiesRegistry.getPropertyHighlighter(
                envSource,
                "TOKEN",
                EnvSourcePreferenceType.CRITICAL
            )
        )
    }

    fun `test activating reset on close replaces a conflicting critical preference`() {
        val file = envFile("TOKEN=value")
        val envSource = envSource(file)

        file.toggleMarkAsCritical("TOKEN", envSource)
        file.toggleResetOnClose("TOKEN", envSource)

        project.useEnvSourcePreferencesManager {
            val preferences = retrievePropertyPreferences(file.virtualFile, "TOKEN")
            assertFalse(preferences.isCritical)
            assertTrue(preferences.requireResetOnClose)
        }
        assertNull(
            EnvSourceHighlightedPropertiesRegistry.getPropertyHighlighter(
                envSource,
                "TOKEN",
                EnvSourcePreferenceType.CRITICAL
            )
        )
        assertNotNull(
            EnvSourceHighlightedPropertiesRegistry.getPropertyHighlighter(
                envSource,
                "TOKEN",
                EnvSourcePreferenceType.RESET_ON_CLOSE
            )
        )

        file.toggleResetOnClose("TOKEN", envSource)
        project.useEnvSourcePreferencesManager {
            deleteAllSourcePreferences(file.virtualFile)
        }
    }

    private fun envFile(content: String): dEnvFile {
        myFixture.configureByText(dEnvFileType, content)
        return myFixture.file as dEnvFile
    }

    private fun envSource(file: dEnvFile): EnvSource {
        return EnvSource(
            project = project,
            source = file.virtualFile,
            module = null,
            _psiSource = file,
            isResolvedFromTemplate = false
        )
    }

}
