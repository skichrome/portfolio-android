package com.skichrome.portfolio.model

import android.net.Uri
import com.skichrome.portfolio.model.base.HomeRepository
import com.skichrome.portfolio.model.base.HomeSource
import com.skichrome.portfolio.model.remote.util.User
import com.skichrome.portfolio.util.RequestResults

class DefaultHomeRepository(private val source: HomeSource) : HomeRepository
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllUsers(): RequestResults<List<User>> = source.getAllUsers()

    override suspend fun getUserInfo(userId: String): RequestResults<User> = source.getUserInfo(userId)

    override suspend fun uploadProfile(user: User, userProfileToUpdate: String?): RequestResults<String> =
        source.uploadProfile(user, userProfileToUpdate)

    override suspend fun uploadProfileImage(userId: String, localImgRef: String): RequestResults<Uri> = source.uploadProfileImage(userId, localImgRef)
}