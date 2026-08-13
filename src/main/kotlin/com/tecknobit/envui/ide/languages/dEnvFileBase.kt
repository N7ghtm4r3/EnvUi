package com.tecknobit.envui.ide.languages

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Language
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.tecknobit.envui.ide.envfile.KeyEntry
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.envfile.ValueEntry
import org.jetbrains.annotations.Unmodifiable

abstract class dEnvFileBase(
    viewProvider: FileViewProvider,
    language: Language
) : PsiFileBase(
    viewProvider,
    language
) {

    fun properties(): @Unmodifiable Collection<Property> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            Property::class.java
        )
    }

    fun keys(): @Unmodifiable Collection<KeyEntry> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            KeyEntry::class.java
        )
    }

    fun values(): @Unmodifiable Collection<ValueEntry> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            ValueEntry::class.java
        )
    }

    fun addCommentOnProperty(
        key: String,
        comment: String
    ) {
        val property = findPropertyByKey(
            key = key
        )!!

        val commentEntry = property.commentEntry
        val commentContent = comment.resolveCommentText()

        commitOnDocument { document ->
            if(commentEntry == null) {
                val propertyLine = document.getLineNumber(
                    property.textOffset
                )
                val insertionOffset = document.getLineStartOffset(
                    propertyLine
                )

                document.insertString(
                    insertionOffset,
                    commentContent
                )
            } else {
                val commentTextRange = commentEntry.textRange

                document.replaceString(
                    commentTextRange.startOffset,
                    commentTextRange.endOffset,
                    commentContent
                )
            }
        }
    }

    private fun String.resolveCommentText(): String {
        var rawComment = this
        if(!rawComment.startsWith("#"))
            rawComment = "# $rawComment"

        return rawComment + "\n"
    }

    fun findPropertyByKey(
        key: String,
        throwOnNull: Boolean = true
    ): Property? {
        val property = properties().firstOrNull { property ->
            property.keyEntry.text == key
        }
        if(throwOnNull && property == null)
            throw NullPointerException("No property associated with that key")

        return property
    }

    fun findPropertyLine(
        key: String
    ): Int {
        var line = 0
        val property = findPropertyByKey(
            key = key
        )!!

        workWithDocument { document ->
            line = document.getLineNumber(property.textRange.startOffset)
        }

        return line
    }

    fun workWithDocument(
        onWork: (Document) -> Unit
    ) {
        provideDocument { _, document ->
            document?.let {
                onWork(document)
            }
        }
    }

    protected fun commitOnDocument(
        synchronously: Boolean = false,
        onWork: (Document) -> Unit
    ) {
        val documentRoutine = {
            provideDocument { manager, document ->
                document?.let {
                    WriteCommandAction.runWriteCommandAction(project) {
                        onWork(document)
                        manager.commitDocument(document)
                    }
                }
            }
        }

        if(synchronously)
            documentRoutine()
        else {
            invokeLater(
                modalityState = ModalityState.current(),
                runnable = {
                    documentRoutine()
                }
            )
        }
    }

    protected fun provideDocument(
        onDocumentProvided: (PsiDocumentManager, Document?) -> Unit
    ) {
        val documentManager = PsiDocumentManager.getInstance(project)
        val document = documentManager.getDocument(this)

        onDocumentProvided(documentManager, document)
    }

}
