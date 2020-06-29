package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.ProjectsRepository
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.util.Event
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class AddEditProjectViewModel(private val repository: ProjectsRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project> = _project

    private val _paragraphClickEvent = MutableLiveData<Event<Int>>()
    val paragraphClickEvent: LiveData<Event<Int>> = _paragraphClickEvent

    private val _paragraphLongClickEvent = MutableLiveData<Event<Int>>()
    val paragraphLongClickEvent: LiveData<Event<Int>> = _paragraphLongClickEvent

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // =================================
    //              Methods
    // =================================

    fun onClickParagraphItem(index: Int)
    {
        _paragraphClickEvent.value = Event(index)
    }

    fun onLongClickParagraphItem(index: Int)
    {
        _paragraphLongClickEvent.value = Event(index)
    }

    fun loadProject(themeId: String, categoryId: String, projectId: String)
    {
        viewModelScope.launch {
            val fetchedProject = repository.getProject(themeId, categoryId, projectId)
            if (fetchedProject is Success)
                _project.value = fetchedProject.data
            else
                showMessage(R.string.add_edit_project_view_model_project_fetch_error)
        }
    }

    fun saveProject(themeId: String, categoryId: String, projectToUpdateId: String?, newProject: Project)
    {
        viewModelScope.launch {
            _loading.value = true
            newProject.content = _project.value?.content ?: emptyList()

            val result = repository.saveProject(themeId, categoryId, projectToUpdateId, newProject)
            if (result is Success)
                _loading.value = false
            else
                showMessage(R.string.add_edit_project_view_model_project_insert_error)
        }
    }
}

class AddEditProjectViewModelFactory(private val repository: ProjectsRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = AddEditProjectViewModel(repository) as T
}