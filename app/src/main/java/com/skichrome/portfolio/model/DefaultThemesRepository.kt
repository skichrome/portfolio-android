package com.skichrome.portfolio.model

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
}