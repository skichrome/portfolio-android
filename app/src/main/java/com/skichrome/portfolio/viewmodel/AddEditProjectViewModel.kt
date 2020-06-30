package com.skichrome.portfolio.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.ProjectsRepository
import com.skichrome.portfolio.model.remote.util.ParagraphContent
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.util.Event
import com.skichrome.portfolio.util.RequestResults.Error
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class AddEditProjectViewModel(private val repository: ProjectsRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project> = _project

    private val _paragraphs = MutableLiveData<MutableList<ParagraphContent>>()
    val paragraphs: LiveData<List<ParagraphContent>> = _paragraphs.map { it.toList() }

    private val _paragraphPictureClickEvent = MutableLiveData<Event<Int>>()
    val paragraphPictureClickEvent: LiveData<Event<Int>> = _paragraphPictureClickEvent

    private val _paragraphLongClickEvent = MutableLiveData<Event<Int>>()
    val paragraphLongClickEvent: LiveData<Event<Int>> = _paragraphLongClickEvent

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // =================================
    //              Methods
    // =================================

    fun onClickParagraphPicture(index: Int)
    {
        _paragraphPictureClickEvent.value = Event(index)
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
            {
                _project.value = fetchedProject.data
                _paragraphs.value = fetchedProject.data.content
            }
            else
                showMessage(R.string.add_edit_project_view_model_project_fetch_error)
        }
    }

    fun initNewProject(project: Project)
    {
        _project.value = project
        _paragraphs.value = project.content
    }

    fun addNewParagraph()
    {
        _paragraphs.value = _paragraphs.value?.apply {
            add(ParagraphContent())
        } ?: mutableListOf(ParagraphContent())
    }

    fun saveProject(themeId: String, categoryId: String, projectToUpdateId: String?, newProject: Project)
    {
        viewModelScope.launch {
            _loading.value = true
            newProject.content = _project.value?.content ?: mutableListOf()

            val result = repository.saveProject(themeId, categoryId, projectToUpdateId, newProject)
            if (result is Success)
            {
                newProject.localMainPicture?.let { photoRef ->
                    val photoResult = repository.uploadProjectImage(themeId, categoryId, result.data, photoRef)
                    if (photoResult is Error)
                        showMessage(R.string.add_edit_project_view_model_project_upload_img_error)
                }

                newProject.content.forEach { content ->
                    content.localPostImage?.let { imgRef ->
                        val contentImgResult = repository.uploadContentImage(themeId, categoryId, result.data, content.index, imgRef)
                        if (contentImgResult is Error)
                        {
                            showMessage(R.string.add_edit_project_view_model_project_upload_img_content_error)
                            return@forEach
                        }
                    }
                }
            }
            else
                showMessage(R.string.add_edit_project_view_model_project_insert_error)
            _loading.value = false
        }
    }

    fun updateParagraphPicture(index: Int, photoRef: Uri)
    {
        _paragraphs.value?.let {
            val newParagraphContent = it[index].copy(
                postTitle = it[index].postTitle,
                postContentText = it[index].postContentText,
                postImage = it[index].postImage,
                index = index,
                localPostImage = photoRef
            )
            it[index] = newParagraphContent
            _paragraphs.value = it
        }
    }
}

class AddEditProjectViewModelFactory(private val repository: ProjectsRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = AddEditProjectViewModel(repository) as T
}