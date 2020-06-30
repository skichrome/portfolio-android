package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.ThemesRepository
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class AddEditThemesViewModel(private val repository: ThemesRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _theme = MutableLiveData<Theme>()
    val theme: LiveData<Theme> = _theme

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // =================================
    //              Methods
    // =================================

    fun loadTheme(themeId: String)
    {
        viewModelScope.launch {
            val result = repository.loadTheme(themeId)
            if (result is Success)
                _theme.value = result.data
            else
                showMessage(R.string.add_edit_theme_view_model_theme_fetch_error)
        }
    }

    fun saveTheme(newTheme: Theme, themeToUpdateId: String?)
    {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.uploadTheme(newTheme, themeToUpdateId)
            if (result is Success)
            {
                newTheme.localImgReference?.let { imgRef ->
                    val photoResult = repository.uploadThemeImage(result.data, imgRef)
                    if (photoResult is Error)
                        showMessage(R.string.add_edit_theme_view_model_theme_upload_img_error)
                }
            }
            else
                showMessage(R.string.add_edit_theme_view_model_theme_insert_error)
            _loading.value = false
        }
    }
}

class AddEditThemesViewModelFactory(private val repository: ThemesRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = AddEditThemesViewModel(repository) as T
}