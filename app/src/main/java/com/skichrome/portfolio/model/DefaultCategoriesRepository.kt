package com.skichrome.portfolio.model

import com.skichrome.portfolio.model.base.CategoriesRepository
import com.skichrome.portfolio.model.base.CategoriesSource
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.util.RequestResults

class DefaultCategoriesRepository(private val remoteSrc: CategoriesSource) : CategoriesRepository
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllCategoryFromThemeId(themeId: String): RequestResults<List<Category>> = remoteSrc.getAllCategoryFromThemeId(themeId)
}