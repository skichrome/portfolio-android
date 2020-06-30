package com.skichrome.portfolio.model

import android.net.Uri
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

    override suspend fun getCategory(themeId: String, categoryId: String): RequestResults<Category> = remoteSrc.getCategory(themeId, categoryId)

    override suspend fun uploadCategory(themeId: String, category: Category, categoryToUpdateId: String?): RequestResults<String> =
        remoteSrc.uploadCategory(themeId, category, categoryToUpdateId)

    override suspend fun uploadCategoryImage(themeId: String, categoryId: String, localImgRef: String): RequestResults<Uri> =
        remoteSrc.uploadCategoryImage(themeId, categoryId, localImgRef)
}