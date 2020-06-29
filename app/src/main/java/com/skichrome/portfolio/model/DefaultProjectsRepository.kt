package com.skichrome.portfolio.model

import com.skichrome.portfolio.model.base.ProjectsRepository
import com.skichrome.portfolio.model.base.ProjectsSource
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.util.RequestResults

class DefaultProjectsRepository(private val remoteSrc: ProjectsSource) : ProjectsRepository
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllProjectsFromCategoryFromTheme(themeId: String, categoryId: String): RequestResults<List<Project>> =
        remoteSrc.getAllProjectsFromCategoryFromTheme(themeId, categoryId)

    override suspend fun getProject(themeId: String, categoryId: String, projectId: String): RequestResults<Project> =
        remoteSrc.getProject(themeId, categoryId, projectId)

    override suspend fun saveProject(themeId: String, categoryId: String, projectToUpdateId: String?, project: Project): RequestResults<String> =
        remoteSrc.saveProject(themeId, categoryId, projectToUpdateId, project)
}