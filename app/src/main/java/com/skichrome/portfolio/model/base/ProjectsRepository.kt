package com.skichrome.portfolio.model.base

import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.util.RequestResults

interface ProjectsRepository
{
    suspend fun getAllProjectsFromCategoryFromTheme(themeId: String, categoryId: String): RequestResults<List<Project>>
}