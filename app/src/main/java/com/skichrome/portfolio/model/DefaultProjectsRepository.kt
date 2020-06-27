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
}