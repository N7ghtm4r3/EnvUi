package com.tecknobit.envui.ui.pages.envuiwindow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.pages.envuiwindow.states.EnvUiWindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The **EnvUiWindowViewModel** class is the support class used to retrieve, filter, and refresh environment sources
 *
 * @property project The project whose environment sources are managed
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class EnvUiWindowViewModel(
    private val project: Project,
) : ViewModel() {

    /**
     * `repository` the repository used to retrieve project environment sources
     */
    private val repository = EnvSourceRepository(
        project = project
    )

    /**
     * `_windowState` the mutable state used to manage the source list and search query
     */
    private val _windowState = MutableStateFlow(
        value = EnvUiWindowState()
    )
    /**
     * `windowState` the read-only state exposed to the environment source window
     */
    val windowState = _windowState.asStateFlow()

    /**
     * Method used to retrieve the filtered project environment sources and update the [windowState]
     */
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

    /**
     * Method used to refresh the environment sources after virtual file system changes
     */
    fun monitorFileTreeChanges() {
        project.messageBus
            .connect()
            .subscribe(
                topic = VirtualFileManager.VFS_CHANGES,
                handler = object : BulkFileListener {
                    /**
                     * Method used to refresh the environment sources after virtual file events complete
                     *
                     * @param events The completed virtual file events
                     */
                    override fun after(events: List<VFileEvent>) {
                        super.after(events)

                        retrieveSources()
                    }
                }
        )
    }

    /**
     * Method used to update the search query and retrieve matching environment sources
     *
     * @param query The query applied to source container and module names
     */
    fun filterSources(
        query: String,
    ) {
        _windowState.value.query.value = query

        retrieveSources()
    }

}
