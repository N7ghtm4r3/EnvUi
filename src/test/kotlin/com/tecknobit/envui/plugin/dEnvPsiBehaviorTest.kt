package com.tecknobit.envui.plugin

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFileType
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.utils.isEnvFile
import com.tecknobit.envui.utils.isEnvSourceFile
import com.tecknobit.envui.utils.isEnvTemplateFile
import com.tecknobit.envui.utils.updateKeysFromTemplate
import com.tecknobit.envui.utils.writeKeys

class dEnvPsiBehaviorTest : BasePlatformTestCase() {

    fun `test parser exposes shell compatible properties and values`() {
        val file = envFile(
            """
            # service configuration
            export API_URL=https://example.test/v1?token=a=b
            EMPTY=
            HASH=escaped\#value # trailing comment
            JSON={"enabled":true}
            QUOTED="line one
            line two"
            """.trimIndent()
        )

        assertEquals(
            listOf("API_URL", "EMPTY", "HASH", "JSON", "QUOTED"),
            file.keys().map { it.text }
        )
        assertEquals("https://example.test/v1?token=a=b", file.findPropertyByKey("API_URL")!!.valueEntry!!.text)
        assertNull(file.findPropertyByKey("EMPTY")!!.valueEntry)
        assertEquals("escaped\\#value ", file.findPropertyByKey("HASH")!!.valueEntry!!.text)
        assertEquals("\"line one\nline two\"", file.findPropertyByKey("QUOTED")!!.valueEntry!!.text)
    }

    fun `test lookup reports missing keys and finds document lines`() {
        val file = envFile("FIRST=1\n\n# note\nSECOND=2")

        assertEquals(0, file.findPropertyLine("FIRST"))
        assertEquals(3, file.findPropertyLine("SECOND"))
        assertNull(file.findPropertyByKey("UNKNOWN", throwOnNull = false))

        try {
            file.findPropertyByKey("UNKNOWN")
            fail("Expected a missing property lookup to throw")
        } catch (exception: NullPointerException) {
            assertEquals("No property associated with that key", exception.message)
        }
    }

    fun `test synchronous value updates insert replace and keep psi committed`() {
        val file = envFile("TOKEN=old\nEMPTY=")

        file.updateValueForKey("TOKEN", "new", synchronously = true)
        file.updateValueForKey("EMPTY", "created", synchronously = true)

        PsiDocumentManager.getInstance(project).commitAllDocuments()
        assertEquals("TOKEN=new\nEMPTY=created", file.text)
        assertEquals("new", file.findPropertyByKey("TOKEN")!!.valueEntry!!.text)
        assertEquals("created", file.findPropertyByKey("EMPTY")!!.valueEntry!!.text)
    }

    fun `test updating an unknown key reports the missing property`() {
        val file = envFile("TOKEN=old")

        try {
            file.updateValueForKey("UNKNOWN", "ignored", synchronously = true)
            fail("Expected an update for a missing property to throw")
        } catch (exception: NullPointerException) {
            assertEquals("No property associated with that key", exception.message)
        }

        assertEquals("TOKEN=old", file.text)
    }

    fun `test write keys removes values and preserves declaration order`() {
        val file = envFile("FIRST=one\nSECOND=two\nTHIRD=")

        file.writeKeys()

        assertEquals("FIRST=\nSECOND=\nTHIRD=", file.text)
        assertEquals(listOf("FIRST", "SECOND", "THIRD"), file.keys().map { it.text })
        assertTrue(file.values().isEmpty())
    }

    fun `test template keys are replaced by the editable template order`() {
        myFixture.configureByText(
            dEnvTemplateFileType,
            "OLD=value"
        )
        val file = myFixture.file as dEnvTemplateFile

        file.updateKeysFromTemplate(
            listOf(
                EnvTemplateField(key = "HOST"),
                EnvTemplateField(key = "PORT"),
                EnvTemplateField(key = "FEATURE_FLAG")
            )
        )

        assertEquals("HOST=\nPORT=\nFEATURE_FLAG=", file.text)
    }

    fun `test environment file classification distinguishes sources and templates`() {
        val source = envFile("KEY=value").virtualFile
        myFixture.configureByText(dEnvTemplateFileType, "KEY=")
        val template = myFixture.file.virtualFile
        myFixture.configureByText("notes.txt", "KEY=value")
        val text = myFixture.file.virtualFile

        assertTrue(source.isEnvFile())
        assertTrue(source.isEnvSourceFile())
        assertFalse(source.isEnvTemplateFile())
        assertTrue(template.isEnvTemplateFile())
        assertTrue(template.isEnvSourceFile())
        assertFalse(template.isEnvFile())
        assertFalse(text.isEnvSourceFile())
        assertFalse(null.isEnvSourceFile())
    }

    private fun envFile(content: String): dEnvFile {
        myFixture.configureByText(dEnvFileType, content)
        return myFixture.file as dEnvFile
    }

}
