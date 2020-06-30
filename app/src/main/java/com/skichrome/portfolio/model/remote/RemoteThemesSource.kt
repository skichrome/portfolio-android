package com.skichrome.portfolio.model.remote

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.skichrome.portfolio.model.base.ThemesSource
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class RemoteThemesSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ThemesSource
{
    // =================================
    //              Fields
    // =================================

    private val themeReference = FirebaseFirestore.getInstance().collection(ROOT_COLLECTION)
        .document(CURRENT_VERSION)
        .collection(THEMES_COLLECTION)

    private val storage = Firebase.storage
    private val mediaReference = storage.reference.child(THEMES_COLLECTION)

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllThemes(): RequestResults<List<Theme>> = withContext(dispatcher) {
        return@withContext try
        {
            val result = themeReference.get().await()
                .map { it.toObject(Theme::class.java).withId<Theme>(it.id) }
            return@withContext Success(result)
        }
        catch (e: Exception)
        {
            Log.e("RemoteThemesSrc", "An Error occurred when fetching all themes", e)
            RequestResults.Error(e)
        }
    }

    override suspend fun loadTheme(themeId: String): RequestResults<Theme> = withContext(dispatcher) {
        return@withContext try
        {
            val result = themeReference.document(themeId).get().await()
                .let {
                    it.toObject(Theme::class.java)?.withId<Theme>(it.id)
                        ?: return@withContext RequestResults.Error(FirebaseFirestoreClassCastException("Could not cast data object to Project class"))
                }
            return@withContext Success(result)
        }
        catch (e: Exception)
        {
            Log.e("RemoteThemesSrc", "An Error occurred when fetching this themes : $themeId", e)
            RequestResults.Error(e)
        }
    }

    override suspend fun uploadTheme(theme: Theme, themeToUpdateId: String?): RequestResults<String> = withContext(dispatcher) {
        return@withContext try
        {
            themeToUpdateId?.let { id ->
                themeReference.document(id)
                    .set(theme)
                    .await()
                Success(id)
            } ?: themeReference.add(theme)
                .await()
                .let {
                    Success(it.id)
                }
        }
        catch (e: Exception)
        {
            Log.e("RemoteThemesSrc", "An Error occurred when uploading theme", e)
            RequestResults.Error(e)
        }
    }

    override suspend fun uploadThemeImage(themeId: String, localImgRef: String): RequestResults<Uri> = withContext(dispatcher) {
        val metadata = storageMetadata {
            contentType = "image/jpg"
        }

        return@withContext try
        {
            val localFile = File(localImgRef).toUri()
            val newRef = mediaReference.child("$themeId/${localFile.path?.split("/")?.last().toString()}")

            val result = newRef.putFile(localFile, metadata)
                .continueWithTask { task ->
                    if (!task.isSuccessful)
                        task.exception?.let { throw it }
                    newRef.downloadUrl
                }
                .await()

            themeReference.document(themeId)
                .get()
                .await()
                .toObject(Category::class.java)
                ?.imgReference
                ?.let { remoteRef ->
                    storage.getReferenceFromUrl(remoteRef)
                        .delete()
                        .await()
                }

            themeReference.document(themeId)
                .update(mapOf("image" to result.toString()))
                .await()

            Success(result)
        }
        catch (e: Exception)
        {
            Log.e("RemoteThemesSrc", "An Error occurred when uploading theme ($themeId) image", e)
            RequestResults.Error(e)
        }
    }
}