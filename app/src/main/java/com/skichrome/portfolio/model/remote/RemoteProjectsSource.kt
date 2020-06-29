package com.skichrome.portfolio.model.remote

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.skichrome.portfolio.model.base.ProjectsSource
import com.skichrome.portfolio.model.remote.util.ParagraphContent
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.util.RequestResults.Error
import com.skichrome.portfolio.util.RequestResults.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class RemoteProjectsSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ProjectsSource
{
    // =================================
    //              Fields
    // =================================

    private val themesReference = FirebaseFirestore.getInstance().collection(ROOT_COLLECTION)
        .document(CURRENT_VERSION)
        .collection(THEMES_COLLECTION)

    private val storage = Firebase.storage
    private val mediaReference = storage.reference.child(PROJECTS_COLLECTION)

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllProjectsFromCategoryFromTheme(themeId: String, categoryId: String): RequestResults<List<Project>> =
        withContext(dispatcher) {
            return@withContext try
            {
                val result = getProjectsReference(themeId, categoryId).get().await()
                    .map { it.toObject(Project::class.java).withId<Project>(it.id) }
                Success(result)
            }
            catch (e: Exception)
            {
                Log.e("RemoteProjectsSrc", "An Error occurred when fetching Projects", e)
                Error(e)
            }
        }

    override suspend fun getProject(themeId: String, categoryId: String, projectId: String): RequestResults<Project> = withContext(dispatcher) {
        return@withContext try
        {
            val result = getProjectsReference(themeId, categoryId).document(projectId).get().await()
                .let {
                    it.toObject(Project::class.java)
                        ?.withId<Project>(it.id)
                        ?.also { project -> project.content.mapIndexed { index, paragraphContent -> paragraphContent.withId<ParagraphContent>("$index") } }
                        ?: return@withContext Error(FirebaseFirestoreClassCastException("Could not cast data object to Project class"))
                }
            Success(result)
        }
        catch (e: Exception)
        {
            Log.e("RemoteProjectsSrc", "An Error occurred when fetching Project with ID : $projectId", e)
            Error(e)
        }
    }

    override suspend fun saveProject(themeId: String, categoryId: String, projectToUpdateId: String?, project: Project): RequestResults<String> =
        withContext(dispatcher) {
            return@withContext try
            {
                projectToUpdateId?.let { id ->
                    getProjectsReference(themeId, categoryId)
                        .document(id)
                        .set(project)
                        .await()
                    Success(id)
                }
                    ?: getProjectsReference(themeId, categoryId)
                        .add(project)
                        .await()
                        .let { docRef ->
                            Success(docRef.id)
                        }
            }
            catch (e: Exception)
            {
                Log.e("RemoteProjectsSrc", "Could not save project with id ${project.id} / $projectToUpdateId", e)
                Error(e)
            }
        }

    override suspend fun uploadProjectImage(themeId: String, categoryId: String, projectId: String, localRef: String): RequestResults<Uri> =
        withContext(dispatcher) {
            val localFile = File(localRef).toUri()
            val newRef = mediaReference.child("$projectId/${localFile.path?.split("/")?.last().toString()}")

            val metadata = storageMetadata {
                contentType = "image/jpg"
            }
            return@withContext try
            {
                val result = newRef.putFile(localFile, metadata)
                    .continueWithTask { task ->
                        if (!task.isSuccessful)
                            task.exception?.let { throw it }
                        newRef.downloadUrl
                    }
                    .await()
                suppressImageIfExists(themeId, categoryId, projectId)
                updateFirestoreWithImgRemoteUri(themeId, categoryId, projectId, result)
                Success(result)
            }
            catch (e: Exception)
            {
                Log.e("RemoteProjectsSrc", "Could not upload picture to storage", e)
                Error(e)
            }
        }

    private suspend fun suppressImageIfExists(themeId: String, categoryId: String, projectId: String) = withContext(dispatcher) {
        getProjectsReference(themeId, categoryId)
            .document(projectId)
            .get()
            .await()
            .toObject(Project::class.java)
            ?.mainPicture
            ?.let { remotePictureRef ->
                storage.getReferenceFromUrl(remotePictureRef)
                    .delete()
                    .await()
            }
    }

    private suspend fun updateFirestoreWithImgRemoteUri(themeId: String, categoryId: String, projectId: String, remoteUri: Uri) =
        withContext(dispatcher) {
            getProjectsReference(themeId, categoryId)
                .document(projectId)
                .update(mapOf("main_picture" to remoteUri.toString()))
                .await()
        }

    // =================================
    //              Methods
    // =================================

    private fun getProjectsReference(themeId: String, categoryId: String) = themesReference.document(themeId)
        .collection(CATEGORIES_COLLECTION)
        .document(categoryId)
        .collection(PROJECTS_COLLECTION)
}