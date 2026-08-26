package com.tecknobit.envui.ide.envfile

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType

class dEnvIncrementalParsingTest : BasePlatformTestCase() {

    fun `test inserting new line at end keeps psi and document in sync`() {
        val file = configureEnvFile("ciao=\"aa\"")

        editDocument(file) { document ->
            document.insertString(document.textLength, "\n")
        }

        assertPsiAndDocumentAreInSync(file)
        assertEquals(listOf("ciao"), file.keys().map { it.text })
    }

    fun `test inserting a property updates the parsed keys and values`() {
        val file = configureEnvFile("FIRST=one\n")

        editDocument(file) { document ->
            document.insertString(document.textLength, "SECOND=two")
        }

        assertPsiAndDocumentAreInSync(file)
        assertEquals(listOf("FIRST", "SECOND"), file.keys().map { it.text })
        assertEquals(listOf("one", "two"), file.values().map { it.text })
    }

    fun `test replacing a value updates the property psi`() {
        val file = configureEnvFile("TOKEN=old")

        editDocument(file) { document ->
            document.replaceString("TOKEN=".length, document.textLength, "new")
        }

        assertPsiAndDocumentAreInSync(file)
        assertEquals("TOKEN=new", file.text)
        assertEquals("new", file.findPropertyByKey("TOKEN")!!.valueEntry!!.text)
    }

    fun `test deleting a value keeps an empty property valid`() {
        val file = configureEnvFile("TOKEN=temporary")

        editDocument(file) { document ->
            document.deleteString("TOKEN=".length, document.textLength)
        }

        assertPsiAndDocumentAreInSync(file)
        assertEquals("TOKEN=", file.text)
        assertNull(file.findPropertyByKey("TOKEN")!!.valueEntry)
    }

    fun `test inserting a property between existing lines preserves their order`() {
        val file = configureEnvFile("FIRST=1\nSECOND=2")

        editDocument(file) { document ->
            document.insertString("FIRST=1\n".length, "MIDDLE=3\n")
        }

        assertPsiAndDocumentAreInSync(file)
        assertEquals(
            listOf("FIRST", "MIDDLE", "SECOND"),
            file.keys().map { it.text }
        )
    }

    fun `test completing an unfinished quoted value creates a value entry`() {
        val file = configureEnvFile("TOKEN=\"unfinished")

        editDocument(file) { document ->
            document.insertString(document.textLength, "\"")
        }

        assertPsiAndDocumentAreInSync(file)
        assertEquals(
            "\"unfinished\"",
            file.findPropertyByKey("TOKEN")!!.valueEntry!!.text
        )
    }

    fun `test inserting a new line inside a quoted value preserves one property`() {
        val file = configureEnvFile("TOKEN=\"firstsecond\"")

        editDocument(file) { document ->
            document.insertString("TOKEN=\"first".length, "\n")
        }

        assertPsiAndDocumentAreInSync(file)
        assertEquals(listOf("TOKEN"), file.keys().map { it.text })
        assertEquals(
            "\"first\nsecond\"",
            file.findPropertyByKey("TOKEN")!!.valueEntry!!.text
        )
    }

    private fun configureEnvFile(content: String): dEnvFile {
        myFixture.configureByText(
            dEnvFileType,
            content,
        )

        return myFixture.file as dEnvFile
    }

    private fun editDocument(
        file: dEnvFile,
        edit: (Document) -> Unit,
    ) {
        val documentManager = PsiDocumentManager.getInstance(project)
        val document = documentManager.getDocument(file)!!

        WriteCommandAction.runWriteCommandAction(project) {
            edit(document)
            documentManager.commitDocument(document)
        }
    }

    private fun assertPsiAndDocumentAreInSync(file: dEnvFile) {
        PsiDocumentManager.getInstance(project).commitAllDocuments()

        val document = myFixture.editor.document
        assertEquals(document.text, file.text)
        assertEquals(document.textLength, file.textLength)
    }

}
