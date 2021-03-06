package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.HomeRepository
import com.skichrome.portfolio.model.remote.util.User
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    // =================================
    //              Methods
    // =================================

    fun loadFirstUserAvailable()
    {
        viewModelScope.launch {
            val users = repository.getAllUsers()
            if (users is Success && users.data.isNotEmpty())
            {
                _user.value = users.data.first()
                showMessage(R.string.home_view_model_user_fetch_success)
            }
            else
                showMessage(R.string.home_view_model_user_fetch_error)
        }
    }
}

class HomeViewModelFactory(private val repository: HomeRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = HomeViewModel(repository) as T
}