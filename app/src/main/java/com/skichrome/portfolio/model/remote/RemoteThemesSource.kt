package com.skichrome.portfolio.model.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.skichrome.portfolio.model.base.ThemesSource
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteThemesSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ThemesSource
{
    // =================================
    //              Fields
    // =================================

    private val themeReference = FirebaseFirestore.getInstance().collection(ROOT_COLLECTION)
        .document(CURRENT_VERSION)
        .collection(THEMES_COLLECTION)

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllThemes(): RequestResults<List<Theme>> = withContext(dispatcher) {
        return@withContext try
        {
            val query = themeReference.get()
                .await()

            val result = query.documents.map { document ->
                document.toObject(Theme::class.java)!!.withId<Theme>(document.id)
            }

            return@withContext Success(result)
        }
        catch (e: Exception)
        {
            Log.e("RemoteThemesSrc", "An Error occurred when fetching all themes", e)
            RequestResults.Error(e)
        }
    }
}