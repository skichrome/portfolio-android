package com.skichrome.portfolio.model.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skichrome.portfolio.model.base.CategoriesSource
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.util.RequestResults.Error
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteCategoriesSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : CategoriesSource
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

    override suspend fun getAllCategoryFromThemeId(themeId: String): RequestResults<List<Category>> = withContext(dispatcher) {
        return@withContext try
        {
            val result = getCategoriesReference(themeId).get().await()
                .map { it.toObject(Category::class.java).withId<Category>(it.id) }
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun getCategoriesReference(themeId: String): CollectionReference = themesReference.document(themeId)
        .collection(CATEGORIES_COLLECTION)
}