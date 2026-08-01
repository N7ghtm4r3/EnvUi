package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.tecknobit.envui.com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.states.EnvUiWindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EnvUiWindowViewModel(
    private val project: Project,
) : ViewModel() {

    private val repository = EnvSourceRepository(
        project = project
    )

    private val _windowState = MutableStateFlow(
        value = EnvUiWindowState()
    )
    val windowState = _windowState.asStateFlow()

    fun retrieveSources() {
        viewModelScope.launch {
            val sources = repository.retrieveEnvs(
                filters = _windowState.value.query.value
            )

            _windowState.update {
                it.copy(
                    sources = sources
                )
            }
        }
    }

    fun monitorFileTreeChanges() {
        project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    super.after(events)

                    retrieveSources()
                }
            }
        )
    }

    fun filterSources(
        query: String,
    ) {
        _windowState.value.query.value = query

        retrieveSources()
    }

}