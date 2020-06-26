package com.skichrome.portfolio.util

import com.skichrome.portfolio.model.DefaultCategoriesRepository
import com.skichrome.portfolio.model.DefaultHomeRepository
import com.skichrome.portfolio.model.DefaultThemesRepository
import com.skichrome.portfolio.model.base.*
import com.skichrome.portfolio.model.remote.RemoteCategoriesSource
import com.skichrome.portfolio.model.remote.RemoteHomeSource
import com.skichrome.portfolio.model.remote.RemoteThemesSource

object ServiceLocator
{
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

    // --- Categories --- //

    private fun provideRemoteCategoriesSource(): CategoriesSource = RemoteCategoriesSource()

    private fun provideCategoriesRepository(): CategoriesRepository
    {
        val remoteSrc = provideRemoteCategoriesSource()
        return DefaultCategoriesRepository(remoteSrc)
    }

    fun getCategoriesRepository(): CategoriesRepository = provideCategoriesRepository()
}