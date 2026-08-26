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

/**
 * The `dEnvFileBase` class is useful to expose and edit the common `PSI` structure of environment files
 *
 * @param viewProvider The view provider of the `PSI` file
 * @param language The language represented by the file
 *
 * @author N7ghtm4r3 - Tecknobit
 */
abstract class dEnvFileBase(
    viewProvider: FileViewProvider,
    language: Language
) : PsiFileBase(
    viewProvider,
    language
) {

    /**
     * Method used to retrieve the properties declared in the environment file
     *
     * @return the declared properties as [Collection] of [Property]
     */
    fun properties(): @Unmodifiable Collection<Property> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            Property::class.java
        )
    }

    /**
     * Method used to retrieve the keys declared in the environment file
     *
     * @return the declared keys as [Collection] of [KeyEntry]
     */
    fun keys(): @Unmodifiable Collection<KeyEntry> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            KeyEntry::class.java
        )
    }

    /**
     * Method used to retrieve the values declared in the environment file
     *
     * @return the declared values as [Collection] of [ValueEntry]
     */
    fun values(): @Unmodifiable Collection<ValueEntry> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            ValueEntry::class.java
        )
    }

    /**
     * Method used to add or replace the comment associated with an environment property
     *
     * @param key The key of the property to comment
     * @param comment The comment content to write
     */
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

    /**
     * Method used to format this text as an environment file comment
     *
     * @return the formatted comment as [String]
     */
    private fun String.resolveCommentText(): String {
        var rawComment = this
        if(!rawComment.startsWith("#"))
            rawComment = "# $rawComment"

        return rawComment + "\n"
    }

    /**
     * Method used to find an environment property by its key
     *
     * @param key The key of the property to find
     * @param throwOnNull Whether an exception must be thrown when the property is unavailable
     *
     * @return the matching property, if available, as [Property]
     */
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

    /**
     * Method used to find the document line of an environment property
     *
     * @param key The key of the property to locate
     *
     * @return the zero-based document line of the property as [Int]
     */
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

    /**
     * Method used to execute an operation with the document backing this `PSI` file
     *
     * @param onWork The operation to execute when the document is available
     */
    fun workWithDocument(
        onWork: (Document) -> Unit
    ) {
        provideDocument { _, document ->
            document?.let {
                onWork(document)
            }
        }
    }

    /**
     * Method used to edit and commit the document backing this `PSI` file
     *
     * @param synchronously Whether the document operation must run synchronously
     * @param onWork The document mutation to execute
     */
    fun commitOnDocument(
        synchronously: Boolean = false,
        onWork: (Document) -> Unit,
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

    /**
     * Method used to provide the document manager and backing document of this `PSI` file
     *
     * @param onDocumentProvided The operation to execute with the resolved document information
     */
    protected fun provideDocument(
        onDocumentProvided: (PsiDocumentManager, Document?) -> Unit
    ) {
        val documentManager = PsiDocumentManager.getInstance(project)
        val document = documentManager.getDocument(this)

        onDocumentProvided(documentManager, document)
    }

}
