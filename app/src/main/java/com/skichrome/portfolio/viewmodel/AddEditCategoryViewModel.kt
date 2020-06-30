package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.CategoriesRepository
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class AddEditCategoryViewModel(private val repository: CategoriesRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _category = MutableLiveData<Category>()
    val category: LiveData<Category> = _category

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // =================================
    //              Methods
    // =================================

    fun loadCategory(themeId: String, categoryId: String)
    {
        viewModelScope.launch {
            val fetchedCategory = repository.getCategory(themeId, categoryId)
            if (fetchedCategory is Success)
                _category.value = fetchedCategory.data
            else
                showMessage(R.string.add_edit_category_view_model_category_fetch_error)
        }
    }

    fun saveCategory(themeId: String, newCategory: Category, categoryToUpdateId: String?)
    {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.uploadCategory(themeId, newCategory, categoryToUpdateId)
            if (result is Success)
            {
                newCategory.localFileReference?.let { imgRef ->
                    val photoResult = repository.uploadCategoryImage(themeId, result.data, imgRef)
                    if (photoResult is Error)
                        showMessage(R.string.add_edit_category_view_model_category_upload_img_error)
                }
            }
            else
                showMessage(R.string.add_edit_category_view_model_category_insert_error)
            _loading.value = false
        }
    }
}

class AddEditCategoryViewModelFactory(private val repository: CategoriesRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = AddEditCategoryViewModel(repository) as T
}