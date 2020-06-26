package com.skichrome.portfolio.model.base

import com.skichrome.portfolio.model.local.util.User
import com.skichrome.portfolio.util.RequestResults

interface HomeRepository
{
    suspend fun getUserInfo(): RequestResults<User>
}