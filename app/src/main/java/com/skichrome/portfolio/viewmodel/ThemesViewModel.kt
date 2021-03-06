package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.ThemesRepository
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.util.Event
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class ThemesViewModel(private val repository: ThemesRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _themes = MutableLiveData<List<Theme>>()
    val themes: LiveData<List<Theme>> = _themes

    private val _themeClickEvent = MutableLiveData<Event<String>>()
    val themeClickEvent: LiveData<Event<String>> = _themeClickEvent

    private val _themeLongClickEvent = MutableLiveData<Event<String>>()
    val themeLongClickEvent: LiveData<Event<String>> = _themeLongClickEvent

    // =================================
    //              Methods
    // =================================

    fun onClick(themeId: String)
    {
        _themeClickEvent.value = Event(themeId)
    }

    fun onLongClick(themeId: String)
    {
        _themeLongClickEvent.value = Event(themeId)
    }

    fun loadThemes()
    {
        viewModelScope.launch {
            val themesResults = repository.getAllThemes()
            if (themesResults is Success)
            {
                showMessage(R.string.themes_fragment_themes_fetch_success)
                _themes.value = themesResults.data
            }
            else
                showMessage(R.string.themes_fragment_themes_fetch_error)
        }
    }
}

class ThemesViewModelFactory(private val repository: ThemesRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ThemesViewModel(repository) as T
}