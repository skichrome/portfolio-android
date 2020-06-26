package com.skichrome.portfolio

import android.app.Application
import com.skichrome.portfolio.model.base.HomeRepository
import com.skichrome.portfolio.util.ServiceLocator

class PortfolioApplication : Application()
{
    val homeRepository: HomeRepository
        get() = ServiceLocator.getHomeRepository()
}