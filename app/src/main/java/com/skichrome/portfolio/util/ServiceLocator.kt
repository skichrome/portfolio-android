package com.skichrome.portfolio.util

import com.skichrome.portfolio.model.DefaultHomeRepository
import com.skichrome.portfolio.model.DefaultThemesRepository
import com.skichrome.portfolio.model.base.HomeRepository
import com.skichrome.portfolio.model.base.HomeSource
import com.skichrome.portfolio.model.base.ThemesRepository
import com.skichrome.portfolio.model.base.ThemesSource
import com.skichrome.portfolio.model.remote.RemoteHomeSource
import com.skichrome.portfolio.model.remote.RemoteThemesSource

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

    private fun provideRemoteHomeSource(): HomeSource = RemoteHomeSource()

    private fun provideHomeRepository(): HomeRepository
    {
        val remoteSrc = provideRemoteHomeSource()
        return DefaultHomeRepository(remoteSrc)
    }

    fun getHomeRepository(): HomeRepository = provideHomeRepository()

    // --- Themes --- //

    private fun provideRemoteThemesSource(): ThemesSource = RemoteThemesSource()

    private fun provideThemesRepository(): ThemesRepository
    {
        val remoteSrc = provideRemoteThemesSource()
        return DefaultThemesRepository(remoteSrc)
    }

    fun getThemesRepository(): ThemesRepository = provideThemesRepository()
}