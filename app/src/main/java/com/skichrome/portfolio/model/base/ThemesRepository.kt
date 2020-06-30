package com.skichrome.portfolio.model.base

import android.net.Uri
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.util.RequestResults

interface ThemesRepository
{
    suspend fun getAllThemes(): RequestResults<List<Theme>>

    suspend fun loadTheme(themeId: String): RequestResults<Theme>

    suspend fun uploadTheme(theme: Theme, themeToUpdateId: String?): RequestResults<String>

    suspend fun uploadThemeImage(themeId: String, localImgRef: String): RequestResults<Uri>
}