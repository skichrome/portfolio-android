package com.skichrome.portfolio.model

import android.net.Uri
import com.skichrome.portfolio.model.base.ThemesRepository
import com.skichrome.portfolio.model.base.ThemesSource
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.util.RequestResults

class DefaultThemesRepository(private val remoteSrc: ThemesSource) : ThemesRepository
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllThemes(): RequestResults<List<Theme>> = remoteSrc.getAllThemes()

    override suspend fun loadTheme(themeId: String): RequestResults<Theme> = remoteSrc.loadTheme(themeId)

    override suspend fun uploadTheme(theme: Theme, themeToUpdateId: String?): RequestResults<String> = remoteSrc.uploadTheme(theme, themeToUpdateId)

    override suspend fun uploadThemeImage(themeId: String, localImgRef: Uri): RequestResults<Uri> =
        remoteSrc.uploadThemeImage(themeId, localImgRef)
}