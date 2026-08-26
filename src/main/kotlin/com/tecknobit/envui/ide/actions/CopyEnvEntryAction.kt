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
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.ide.envfile.Property
import java.awt.datatransfer.StringSelection

/**
 * The `CopyEnvEntryAction` class is useful to copy the value of the environment property under the editor caret
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class CopyEnvEntryAction : AnAction() {

    /**
     * Method used to copy the selected environment property value
     *
     * @param event The action event containing the editor selection
     */
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

    /**
     * Method used to copy a property value and display a confirmation hint
     *
     * @param editor The editor where the confirmation is displayed
     * @param value The optional value copied to the clipboard
     */
    private fun copyPropertyValue(
        editor: Editor,
        value: String?
    ) {
        val hintManager = HintManager.getInstance()
        val manager = CopyPasteManager.getInstance()

        manager.setContents(StringSelection(value))
        hintManager.showInformationHint(
            editor,
            I18nMessageBundle.message(
                key = "copied"
            )
        )
    }

    /**
     * Method used to enable the action when the caret selects an environment property
     *
     * @param event The action event to update
     */
    override fun update(
        event: AnActionEvent
    ) {
        val property = event.resolvePropertyEntry()
        val presentation = event.presentation

        presentation.isEnabled = property != null
        presentation.icon = AllIcons.Actions.Copy
    }

    /**
     * Method used to resolve the environment property under the caret of this action event
     *
     * @return the selected environment property, if available, as [Property]
     */
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

    /**
     * Method used to resolve the editor associated with this action event
     *
     * @return the associated editor, if available, as [Editor]
     */
    private fun AnActionEvent.resolveEditor(): Editor? {
        return getData(CommonDataKeys.EDITOR)
    }

    /**
     * Method used to retrieve the thread used to update the action
     *
     * @return the background update thread as [ActionUpdateThread]
     */
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}