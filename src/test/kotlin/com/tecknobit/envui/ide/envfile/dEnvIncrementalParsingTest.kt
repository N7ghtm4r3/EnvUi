package com.tecknobit.envui.ide.envfile

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class dEnvIncrementalParsingTest : BasePlatformTestCase() {

    fun testInsertingNewLineAtEndKeepsPsiAndDocumentInSync() {
        val file = myFixture.configureByText(
            dEnvFileType,
            "ciao=\"aa\"<caret>",
        )

        myFixture.type('\n')
        PsiDocumentManager.getInstance(project).commitAllDocuments()

        val document = myFixture.editor.document
        assertEquals(document.text, file.text)
        assertEquals(document.textLength, file.textLength)
    }

}
