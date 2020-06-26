package com.skichrome.portfolio.model.base

import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.util.RequestResults

interface ThemesSource
{
    suspend fun getAllThemes(): RequestResults<List<Theme>>
}