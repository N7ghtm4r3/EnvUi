package com.tecknobit.envui.ui.pages.envuiwindow.data

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.tecknobit.envui.helpers.EnvSourcePreferencesType
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.CRITICAL
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.RESET_ON_CLOSE
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile

data class EnvSource(
    val project: Project,
    val source: VirtualFile,
    val module: Module?,
    private val _psiSource: PsiFile,
    private val _templateSource: PsiFile? = null,
) {

    val name = source.name

    val path = source.path

    val containerFolder: VirtualFile? = source.parent

    val psiEnvSource = _psiSource as dEnvFile

    var psiEnvTemplateSource = _templateSource as dEnvTemplateFile?

    private val highlightedProperties = hashMapOf<String, HashMap<EnvSourcePreferencesType, RangeHighlighter>>()

    fun isPropertyMarkedAsCritical(
        key: String
    ): Boolean {
        return checkPropertyPrefType(
            key = key,
            type = CRITICAL
        )
    }

    fun isPropertyMarkedAsResettableOnClose(
        key: String
    ): Boolean {
        return checkPropertyPrefType(
            key = key,
            type = RESET_ON_CLOSE
        )
    }

    private fun checkPropertyPrefType(
        key: String,
        type: EnvSourcePreferencesType
    ): Boolean {
        val propertyPreferences = highlightedProperties[key] ?: return false

        return propertyPreferences[type] != null
    }

    fun markPropertyAsCritical(
        key: String,
        highlighter: RangeHighlighter
    ) {
        markPropertyAsPrefType(
            key = key,
            type = CRITICAL,
            highlighter = highlighter
        )
    }

    fun markPropertyAsResettableOnClose(
        key: String,
        highlighter: RangeHighlighter
    ) {
        markPropertyAsPrefType(
            key = key,
            type = RESET_ON_CLOSE,
            highlighter = highlighter
        )
    }

    private fun markPropertyAsPrefType(
        key: String,
        type: EnvSourcePreferencesType,
        highlighter: RangeHighlighter
    ) {
        highlightedProperties.putIfAbsent(key, hashMapOf())
        val propertyPreferences = highlightedProperties[key]!!

        propertyPreferences[type] = highlighter
    }
}
