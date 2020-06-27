package com.skichrome.portfolio.model.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.skichrome.portfolio.model.base.ProjectsSource
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.util.RequestResults.Error
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteProjectsSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ProjectsSource
{
    // =================================
    //              Fields
    // =================================

    private val themesReference = FirebaseFirestore.getInstance().collection(ROOT_COLLECTION)
        .document(CURRENT_VERSION)
        .collection(THEMES_COLLECTION)

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllProjectsFromCategoryFromTheme(themeId: String, categoryId: String): RequestResults<List<Project>> =
        withContext(dispatcher) {
            return@withContext try
            {
                val result = getProjectsReference(themeId, categoryId)
                    .get()
                    .await()
                    .map { it.toObject(Project::class.java).withId<Project>(it.id) }
                Success(result)
            }
            catch (e: Exception)
            {
                Log.e("RemoteProjectsSrc", "An Error occurred when fetching Projects", e)
                Error(e)
            }
        }

    // =================================
    //              Methods
    // =================================

    private fun getProjectsReference(themeId: String, categoryId: String) = themesReference.document(themeId)
        .collection(CATEGORIES_COLLECTION)
        .document(categoryId)
        .collection(PROJECTS_COLLECTION)
}