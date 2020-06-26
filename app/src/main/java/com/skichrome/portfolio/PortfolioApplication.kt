package com.skichrome.portfolio

import android.app.Application
import com.skichrome.portfolio.model.base.CategoriesRepository
import com.skichrome.portfolio.model.base.HomeRepository
import com.skichrome.portfolio.model.base.ThemesRepository
import com.skichrome.portfolio.util.ServiceLocator

class PortfolioApplication : Application()
{
    val homeRepository: HomeRepository
        get() = ServiceLocator.getHomeRepository()

    val themesRepository: ThemesRepository
        get() = ServiceLocator.getThemesRepository()

    val categoriesRepository: CategoriesRepository
        get() = ServiceLocator.getCategoriesRepository()
}