package com.skichrome.portfolio.model.remote

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.skichrome.portfolio.model.base.CategoriesSource
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.util.RequestResults.Error
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


class RemoteCategoriesSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : CategoriesSource
{
    // =================================
    //              Fields
    // =================================

    private val themesReference = FirebaseFirestore.getInstance().collection(ROOT_COLLECTION)
        .document(CURRENT_VERSION)
        .collection(THEMES_COLLECTION)

    private val storage = Firebase.storage
    private val mediaReference = storage.reference.child(CATEGORIES_COLLECTION)

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

    override suspend fun getCategory(themeId: String, categoryId: String): RequestResults<Category> = withContext(dispatcher) {
        return@withContext try
        {
            val result = getCategoriesReference(themeId)
                .document(categoryId)
                .get()
                .await()
                .let {
                    it.toObject(Category::class.java)?.withId<Category>(it.id)
                        ?: return@withContext Error(FirebaseFirestoreClassCastException("Could not cast data object to Project class"))
                }
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun uploadCategory(themeId: String, category: Category, categoryToUpdateId: String?): RequestResults<String> =
        withContext(dispatcher) {
            return@withContext try
            {
                categoryToUpdateId?.let { id ->
                    getCategoriesReference(themeId)
                        .document(id)
                        .set(category)
                    Success(id)
                }
                    ?: getCategoriesReference(themeId)
                        .add(category)
                        .await()
                        .let { docRef ->
                            Success(docRef.id)
                        }
            }
            catch (e: Exception)
            {
                Error(e)
            }
        }

    override suspend fun uploadCategoryImage(themeId: String, categoryId: String, localImgRef: String): RequestResults<Uri> =
        withContext(dispatcher) {
            val metadata = storageMetadata {
                contentType = "image/jpg"
            }

            return@withContext try
            {
                val localFile = File(localImgRef).toUri()
                val newRef = mediaReference.child("$categoryId/${localFile.path?.split("/")?.last().toString()}")

                val result = newRef.putFile(localFile, metadata)
                    .continueWithTask { task ->
                        if (!task.isSuccessful)
                            task.exception?.let { throw it }
                        newRef.downloadUrl
                    }
                    .await()

                getCategoriesReference(themeId)
                    .document(categoryId)
                    .get()
                    .await()
                    .toObject(Category::class.java)
                    ?.imgReference
                    ?.let { remoteRef ->
                        storage.getReferenceFromUrl(remoteRef)
                            .delete()
                            .await()
                    }

                getCategoriesReference(themeId)
                    .document(categoryId)
                    .update(mapOf("image" to result.toString()))
                    .await()

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