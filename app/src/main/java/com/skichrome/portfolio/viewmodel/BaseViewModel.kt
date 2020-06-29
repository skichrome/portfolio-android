package com.skichrome.portfolio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.portfolio.util.Event

abstract class BaseViewModel : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    //private val logTag = javaClass.simpleName

    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>> = _message

    // =================================
    //              Methods
    // =================================

    protected fun showMessage(msgRef: Int)
    {
        _message.value = Event(msgRef)
    }

    //protected fun handleError(e: Error?)
    //    {
    //        e?.exception?.let {
    //            if (!BuildConfig.DEBUG)
    //                Crashlytics.logException(it)
    //        }
    //        when (e?.exception)
    //        {
    //            is NotImplementedException -> Log.e(logTag, "This method isn't implemented !", e.exception)
    //            is RemoteRepositoryException -> Log.e(logTag, "Remote repo error", e.exception)
    //            is NetworkException -> Log.e(logTag, "Repo Error, check your network", e.exception)
    //            is SQLiteConstraintException -> Log.e(logTag, "Check database data insertion !", e.exception)
    //            else -> Log.e(logTag, "An error happened when fetching data : ${e?.exception?.javaClass?.simpleName}")
    //        }
    //    }
}