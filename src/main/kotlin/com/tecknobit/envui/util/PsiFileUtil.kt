package com.tecknobit.envui.util

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

fun PsiFile.appendLast(
    element: PsiElement,
) {
    addAfter(
        lastChild,
        element
    )
}

fun PsiFile.clear() {
    deleteChildRange(
        firstChild,
        lastChild
    )
}

fun PsiFile.writeContent(
    content: String
) {
    val documentManager = PsiDocumentManager.getInstance(project)
    val document = documentManager.getDocument(this)

    document?.let {
        WriteCommandAction.runWriteCommandAction(project) {
            document.setText(content)
            documentManager.commitDocument(document)
        }
    }
}