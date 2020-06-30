package com.skichrome.portfolio.model.base

import android.net.Uri
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.util.RequestResults

interface CategoriesSource
{
    suspend fun getAllCategoryFromThemeId(themeId: String): RequestResults<List<Category>>

    suspend fun getCategory(themeId: String, categoryId: String): RequestResults<Category>

    suspend fun uploadCategory(themeId: String, category: Category, categoryToUpdateId: String?): RequestResults<String>

    suspend fun uploadCategoryImage(themeId: String, categoryId: String, localImgRef: String): RequestResults<Uri>
}