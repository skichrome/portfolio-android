package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.ProjectsRepository
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.util.Event
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class ProjectsViewModel(private val repository: ProjectsRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _projects = MutableLiveData<List<Project>>()
    val projects: LiveData<List<Project>> = _projects

    private val _projectClickEvent = MutableLiveData<Event<String>>()
    val projectClickEvent: LiveData<Event<String>> = _projectClickEvent

    private val _projectLongClickEvent = MutableLiveData<Event<String>>()
    val projectLongClickEvent: LiveData<Event<String>> = _projectLongClickEvent

    // =================================
    //              Methods
    // =================================

    fun onClick(categoryId: String)
    {
        _projectClickEvent.value = Event(categoryId)
    }

    fun onLongClick(categoryId: String)
    {
        _projectLongClickEvent.value = Event(categoryId)
    }

    fun loadCategories(themeId: String, categoryId: String)
    {
        viewModelScope.launch {
            val projectsResults = repository.getAllProjectsFromCategoryFromTheme(themeId, categoryId)
            if (projectsResults is Success)
            {
                showMessage(R.string.categories_fragment_projects_fetch_success)
                _projects.value = projectsResults.data
            }
            else
                showMessage(R.string.categories_fragment_projects_fetch_error)
        }
    }
}

class ProjectsViewModelFactory(private val repository: ProjectsRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ProjectsViewModel(repository) as T
}