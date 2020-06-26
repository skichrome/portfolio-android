package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.HomeRepository
import com.skichrome.portfolio.model.local.util.User
import com.skichrome.portfolio.util.Event
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _errorMsgReference = MutableLiveData<Event<Int>>()
    val errorMsgReference: LiveData<Event<Int>> = _errorMsgReference

    // =================================
    //              Methods
    // =================================

    private fun showMessage(msgRef: Int)
    {
        _errorMsgReference.value = Event(msgRef)
    }

    fun loadUserInfo()
    {
        viewModelScope.launch {
            val userResult = repository.getUserInfo()
            if (userResult is Success)
            {
                _user.value = userResult.data
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