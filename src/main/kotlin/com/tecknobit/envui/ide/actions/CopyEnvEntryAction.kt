package com.tecknobit.envui.ide.actions

import com.intellij.codeInsight.hint.HintManager
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.util.parentOfTypes
import com.tecknobit.envui.ide.envfile.Property
import java.awt.datatransfer.StringSelection

class CopyEnvEntryAction : AnAction() {

    override fun actionPerformed(
        event: AnActionEvent
    ) {
        val editor = event.resolveEditor() ?: return
        val property = event.resolvePropertyEntry() ?: return
        val value = property.valueEntry?.text

        copyPropertyValue(
            editor = editor,
            value = value
        )
    }

    private fun copyPropertyValue(
        editor: Editor,
        value: String?
    ) {
        val hintManager = HintManager.getInstance()
        val manager = CopyPasteManager.getInstance()

        manager.setContents(StringSelection(value))
        hintManager.showInformationHint(
            editor,
            "Copied!"
        )
    }

    override fun update(
        event: AnActionEvent
    ) {
        val property = event.resolvePropertyEntry()
        val presentation = event.presentation

        presentation.isEnabled = property != null
        presentation.icon = AllIcons.Actions.Copy
    }

    private fun AnActionEvent.resolvePropertyEntry(): Property? {
        val editor = resolveEditor()
        val envSource = resolveEnvSource()
        if(editor == null || envSource == null)
            return null

        val caretModel = editor.caretModel
        val lineEntryRaw = envSource.psiEnvSource
            .findElementAt(caretModel.offset)
        if(lineEntryRaw == null)
            return null

        return lineEntryRaw.parentOfTypes(Property::class)
    }

    private fun AnActionEvent.resolveEditor(): Editor? {
        return getData(CommonDataKeys.EDITOR)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}