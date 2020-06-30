package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.*
import com.skichrome.portfolio.R
import com.skichrome.portfolio.model.base.HomeRepository
import com.skichrome.portfolio.model.remote.util.User
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: HomeRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // =================================
    //              Methods
    // =================================

    fun loadUserInfo(userId: String)
    {
        viewModelScope.launch {
            val userResult = repository.getUserInfo(userId)
            if (userResult is Success)
            {
                _user.value = userResult.data
                showMessage(R.string.home_view_model_user_fetch_success)
            }
            else
                showMessage(R.string.home_view_model_user_fetch_error)
        }
    }

    fun uploadUser(user: User, userIdToUpdate: String?)
    {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.uploadProfile(user, userIdToUpdate)
            if (result is Success)
            {
                user.localPhotoReference?.let { imgRef ->
                    repository.uploadProfileImage(result.data, imgRef)
                }
            }
            _loading.value = false
        }
    }
}

class ProfileViewModelFactory(private val repository: HomeRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ProfileViewModel(repository) as T
}
