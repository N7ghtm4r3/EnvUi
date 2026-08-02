package com.tecknobit.envui.util

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