package com.skichrome.portfolio.model

import com.skichrome.portfolio.model.base.HomeRepository
import com.skichrome.portfolio.model.base.HomeSource
import com.skichrome.portfolio.model.local.util.User
import com.skichrome.portfolio.util.RequestResults

class DefaultHomeRepository(private val source: HomeSource) : HomeRepository
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getUserInfo(): RequestResults<User> = source.getUserInfo()
}