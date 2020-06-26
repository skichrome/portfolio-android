package com.skichrome.portfolio.model.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.skichrome.portfolio.model.base.HomeSource
import com.skichrome.portfolio.model.remote.util.User
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.util.RequestResults.Error
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteHomeSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : HomeSource
{
    // =================================
    //              Fields
    // =================================

    private val userReference = FirebaseFirestore.getInstance()
        .collection(ROOT_COLLECTION)
        .document(CURRENT_VERSION)
        .collection(USER_COLLECTION)

    // =================================
    //              Methods
    // =================================

    override suspend fun getUserInfo(): RequestResults<User> = withContext(dispatcher) {
        return@withContext try
        {
            val result = userReference.get().await().first().toObject(User::class.java).withId<User>(userReference.id)
            Success(result)
        }
        catch (e: Exception)
        {
            Log.e("RemoteHomeSrc", "An Error occurred when fetching user info", e)
            Error(e)
        }
    }
}