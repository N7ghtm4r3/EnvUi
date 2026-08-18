package com.tecknobit.envui.utils

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile

fun PsiFile.writeContent(
    content: String
) {
    val documentManager = PsiDocumentManager.getInstance(project)
    val document = documentManager.getDocument(this)

    document?.let {
        WriteCommandAction.runWriteCommandAction(project) {
            document.replaceString(0, document.textLength, content)
            documentManager.commitDocument(document)
        }
    }
}