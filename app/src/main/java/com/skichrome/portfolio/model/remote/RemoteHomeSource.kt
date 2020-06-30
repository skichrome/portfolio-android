package com.skichrome.portfolio.model.remote

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.skichrome.portfolio.model.base.HomeSource
import com.skichrome.portfolio.model.remote.util.User
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.util.RequestResults.Error
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class RemoteHomeSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : HomeSource
{
    // =================================
    //              Fields
    // =================================

    private val userReference = FirebaseFirestore.getInstance()
        .collection(ROOT_COLLECTION)
        .document(CURRENT_VERSION)
        .collection(USER_COLLECTION)

    private val storage = Firebase.storage
    private val userStorageReference = storage.reference.child(USER_COLLECTION)

    // =================================
    //              Methods
    // =================================

    override suspend fun getAllUsers(): RequestResults<List<User>> = withContext(dispatcher) {
        return@withContext try
        {
            val result = userReference.get()
                .await()
                .map {
                    it.toObject(User::class.java).withId<User>(userReference.id).withId<User>(it.id)
                }
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getUserInfo(userId: String): RequestResults<User> = withContext(dispatcher) {
        return@withContext try
        {
            val result = userReference.document(userId).get().await()
                .toObject(User::class.java)?.withId<User>(userReference.id)
                ?: return@withContext Error(FirebaseFirestoreClassCastException("Could not cast data object to User class"))
            Success(result)
        }
        catch (e: Exception)
        {
            Log.e("RemoteHomeSrc", "An Error occurred when fetching user info", e)
            Error(e)
        }
    }

    override suspend fun uploadProfile(user: User, userProfileToUpdate: String?): RequestResults<String> = withContext(dispatcher) {
        return@withContext try
        {
            userProfileToUpdate?.let { id ->
                userReference.document(id)
                    .set(user)
                    .await()
                Success(id)
            }
                ?: userReference.add(user)
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

    override suspend fun uploadProfileImage(userId: String, localImgRef: String): RequestResults<Uri> = withContext(dispatcher) {
        val metadata = storageMetadata {
            contentType = "image/jpg"
        }

        return@withContext try
        {
            val localFile = File(localImgRef).toUri()
            val newRef = userStorageReference.child("$userId/${localFile.path?.split("/")?.last().toString()}")

            val result = newRef.putFile(localFile, metadata)
                .continueWithTask { task ->
                    if (!task.isSuccessful)
                        task.exception?.let { throw it }
                    newRef.downloadUrl
                }
                .await()

            userReference.document(userId)
                .get()
                .await()
                .toObject(User::class.java)
                ?.photoReference
                ?.let { remoteRef ->
                    storage.getReferenceFromUrl(remoteRef)
                        .delete()
                        .await()
                }

            userReference.document(userId)
                .update(mapOf("photo_reference" to result.toString()))
                .await()

            Success(result)
        }
        catch (e: Exception)
        {
            Log.e("RemoteThemesSrc", "An Error occurred when uploading theme ($userId) image", e)
            Error(e)
        }
    }
}