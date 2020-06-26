package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.CategoriesRepository
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.util.Event
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: CategoriesRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _errorMsgReference = MutableLiveData<Event<Int>>()
    val errorMsgReference: LiveData<Event<Int>> = _errorMsgReference

    private val _categoryClickEvent = MutableLiveData<Event<String>>()
    val categoryClickEvent: LiveData<Event<String>> = _categoryClickEvent

    private val _categoryLongClickEvent = MutableLiveData<Event<String>>()
    val categoryLongClickEvent: LiveData<Event<String>> = _categoryLongClickEvent

    // =================================
    //              Methods
    // =================================

    fun onClick(categoryId: String)
    {
        _categoryClickEvent.value = Event(categoryId)
    }

    fun onLongClick(categoryId: String)
    {
        _categoryLongClickEvent.value = Event(categoryId)
    }

    private fun showMessage(msgRef: Int)
    {
        _errorMsgReference.value = Event(msgRef)
    }

    fun loadCategories(themeId: String)
    {
        viewModelScope.launch {
            val categoriesResults = repository.getAllCategoryFromThemeId(themeId)
            if (categoriesResults is Success)
            {
                showMessage(R.string.categories_fragment_themes_fetch_success)
                _categories.value = categoriesResults.data
            }
            else
                showMessage(R.string.categories_fragment_themes_fetch_error)
        }
    }
}

class CategoriesViewModelFactory(private val repository: CategoriesRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = CategoriesViewModel(repository) as T
}