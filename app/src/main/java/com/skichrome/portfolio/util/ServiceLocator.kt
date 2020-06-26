package com.skichrome.portfolio.util

import com.skichrome.portfolio.model.DefaultHomeRepository
import com.skichrome.portfolio.model.base.HomeRepository
import com.skichrome.portfolio.model.base.HomeSource
import com.skichrome.portfolio.model.local.RemoteHomeSource

object ServiceLocator
{
    // =================================
    //              Fields
    // =================================

    private var homeRepository: HomeRepository? = null

    // =================================
    //              Methods
    // =================================

    // --- Home --- //

    private fun provideLocalHomeSource(): HomeSource = RemoteHomeSource()

    private fun provideHomeRepository(): HomeRepository
    {
        val remoteSrc = provideLocalHomeSource()
        return DefaultHomeRepository(remoteSrc)
    }

    fun getHomeRepository(): HomeRepository = provideHomeRepository()
}