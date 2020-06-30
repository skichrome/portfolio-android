package com.skichrome.portfolio.model.base

import android.net.Uri
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.util.RequestResults

interface ProjectsSource
{
    suspend fun getAllProjectsFromCategoryFromTheme(themeId: String, categoryId: String): RequestResults<List<Project>>

    suspend fun getProject(themeId: String, categoryId: String, projectId: String): RequestResults<Project>

    suspend fun saveProject(themeId: String, categoryId: String, projectToUpdateId: String?, project: Project): RequestResults<String>

    suspend fun uploadProjectImage(themeId: String, categoryId: String, projectId: String, localRef: String): RequestResults<Uri>

    suspend fun uploadContentImage(themeId: String, categoryId: String, projectId: String, contentId: Int, localRef: String): RequestResults<Uri>
}