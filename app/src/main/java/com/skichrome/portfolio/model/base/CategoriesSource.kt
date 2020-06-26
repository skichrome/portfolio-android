package com.skichrome.portfolio.model.base

import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.util.RequestResults

interface CategoriesSource
{
    suspend fun getAllCategoryFromThemeId(themeId: String): RequestResults<List<Category>>
}