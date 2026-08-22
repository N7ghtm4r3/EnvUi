package com.tecknobit.envui.plugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile
import com.tecknobit.envui.ide.listeners.document.EnvSourceDocumentListener
import com.tecknobit.envui.ide.listeners.document.dEnvDocumentListener
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.utils.toEnvSource
import com.tecknobit.envui.utils.updateSourceFromTemplate
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable

class EnvSourceWorkflowTest : BasePlatformTestCase() {

    fun `test source resolution discovers its sibling template`() {
        val source = myFixture.addFileToProject("backend/.env", "HOST=localhost") as dEnvFile
        val template = myFixture.addFileToProject("backend/.env.template", "HOST=") as dEnvTemplateFile

        val resolved = source.virtualFile.toEnvSource(
            project = project,
            resolveModule = false
        )

        assertEquals(source.virtualFile, resolved.source)
        assertEquals(source, resolved.psiEnvSource)
        assertEquals(template, resolved.psiEnvTemplateSource)
        assertFalse(resolved.isResolvedFromTemplate)
        assertEquals("backend", resolved.containerFolder!!.name)
    }

    fun `test template resolution discovers its sibling source`() {
        val source = myFixture.addFileToProject("service/.env", "PORT=8080") as dEnvFile
        val template = myFixture.addFileToProject("service/.env.template", "PORT=") as dEnvTemplateFile

        val resolved = template.virtualFile.toEnvSource(
            project = project,
            resolveModule = false
        )

        assertEquals(source.virtualFile, resolved.source)
        assertEquals(source, resolved.psiEnvSource)
        assertEquals(template, resolved.psiEnvTemplateSource)
        assertTrue(resolved.isResolvedFromTemplate)
    }

    fun `test source resolution supports an absent optional template`() {
        val source = myFixture.addFileToProject("standalone/.env", "TOKEN=value") as dEnvFile

        val resolved = source.virtualFile.toEnvSource(
            project = project,
            resolveModule = false
        )

        assertEquals(source, resolved.psiEnvSource)
        assertNull(resolved.psiEnvTemplateSource)
        assertFalse(resolved.isResolvedFromTemplate)
    }

    fun `test repository retrieves templates and filters sources by container`() {
        myFixture.addFileToProject("backend/.env", "HOST=backend")
        myFixture.addFileToProject("backend/.env.template", "HOST=")
        myFixture.addFileToProject("frontend/.env", "HOST=frontend")
        myFixture.addFileToProject("frontend/.env.template", "HOST=")
        val repository = EnvSourceRepository(project)

        val (templates, backendSources, allSources) = runSuspendWithEdtDispatching {
            Triple(
                repository.retrieveEnvTemplates(),
                repository.retrieveEnvs(filters = "backend"),
                repository.retrieveEnvs(filters = "")
            )
        }

        assertEquals(setOf("backend", "frontend"), templates.map { it.parent.name }.toSet())
        assertEquals(listOf("backend"), backendSources.map { it.containerFolder!!.name })
        assertEquals(setOf("backend", "frontend"), allSources.map { it.containerFolder!!.name }.toSet())
        assertNotNull(backendSources.single().psiEnvTemplateSource)
    }

    fun `test repository creates a paired empty source and template`() {
        val placeholder = myFixture.addFileToProject("generated/placeholder.txt", "")
        val directory = placeholder.containingDirectory!!
        val repository = EnvSourceRepository(project)

        val created = runSuspendWithEdtDispatching {
            repository.createNewEnvSource(
                project = project,
                containerDirectory = directory
            )
        }

        assertEquals(".env", created.source.name)
        assertNotNull(directory.findFile(".env"))
        assertNotNull(directory.findFile(".env.template"))
        assertNotNull(created.psiEnvTemplateSource)
        assertFalse(created.isResolvedFromTemplate)
    }

    fun `test template update preserves retained values adds fields and deletes removed preferences`() {
        val source = myFixture.addFileToProject(
            "editable/.env",
            "FIRST=one\nSECOND=two\nTHIRD=three"
        ) as dEnvFile
        val second = source.findPropertyByKey("SECOND")!!
        project.useEnvSourcePreferencesManager {
            setPropertyValue(source.virtualFile, second, "changed")
            setPropertyCriticality(source.virtualFile, second, true)
        }
        val template = EnvSourceTemplate(
            fields = listOf(
                EnvTemplateField("RENAMED_FIRST", EnvFieldType.STRING),
                EnvTemplateField("THIRD", EnvFieldType.STRING),
                EnvTemplateField("ADDED", EnvFieldType.ANY)
            ),
            removedFields = hashSetOf("SECOND")
        )

        source.updateSourceFromTemplate(template)

        assertEquals("RENAMED_FIRST=one\nTHIRD=three\nADDED=\n", source.text)
        project.useEnvSourcePreferencesManager {
            assertNull(retrieveEnvSourcePreferences(source.virtualFile)!!.properties["SECOND"])
        }
    }

    fun `test document listener helpers resolve the backing source model`() {
        val source = myFixture.addFileToProject("listener/.env", "KEY=value") as dEnvFile
        myFixture.openFileInEditor(source.virtualFile)
        val document = PsiDocumentManager.getInstance(project).getDocument(source)!!
        val listener = object : dEnvDocumentListener {}

        val resolvedFile = listener.resolveSource(document)
        val resolvedSource = listener.resolveEnvSource(resolvedFile, project)

        assertEquals(source.virtualFile, resolvedFile)
        assertEquals(source, resolvedSource!!.psiEnvSource)
        assertEquals(source.virtualFile, resolvedSource.source)
    }

    fun `test document saving listener synchronizes source preferences`() {
        val source = myFixture.addFileToProject(
            "saving/.env",
            "TOKEN=value\nPORT=8080"
        ) as dEnvFile
        myFixture.openFileInEditor(source.virtualFile)
        val document = PsiDocumentManager.getInstance(project).getDocument(source)!!

        EnvSourceDocumentListener().beforeDocumentSaving(document)

        project.useEnvSourcePreferencesManager {
            val properties = retrieveEnvSourcePreferences(source.virtualFile)!!.properties
            assertEquals(setOf("TOKEN", "PORT"), properties.keys)
            assertEquals("value", properties.getValue("TOKEN").currentValue)
            assertEquals("8080", properties.getValue("PORT").currentValue)
            deleteAllSourcePreferences(source.virtualFile)
        }
    }

    private fun <T> runSuspendWithEdtDispatching(action: suspend () -> T): T {
        val future = ApplicationManager.getApplication().executeOnPooledThread(
            Callable {
                runBlocking {
                    action()
                }
            }
        )
        PlatformTestUtil.waitWithEventsDispatching(
            "Suspend repository operation to complete",
            future::isDone,
            5
        )
        return future.get()
    }

}
