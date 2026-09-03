package com.tecknobit.envui.utils

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Method used to replace the content of this `PSI` file and commit the related document
 *
 * @param content The content to write
 */
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

fun PsiFile.containsEntry(
    psiElement: PsiElement,
): Boolean {
    return children.firstOrNull {
        it.text == psiElement.text
    } != null
}

fun PsiFile.addLast(
    psiElement: PsiElement,
) {
    psiElement.addAfter(
        this.lastChild,
        psiElement
    )
}