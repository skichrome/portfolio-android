package com.skichrome.portfolio.model.base

import android.net.Uri
import com.skichrome.portfolio.model.remote.util.User
import com.skichrome.portfolio.util.RequestResults

interface HomeRepository
{
    suspend fun getAllUsers(): RequestResults<List<User>>

    suspend fun getUserInfo(userId: String): RequestResults<User>

    suspend fun uploadProfile(user: User, userProfileToUpdate: String?): RequestResults<String>

    suspend fun uploadProfileImage(userId: String, localImgRef: Uri): RequestResults<Uri>
}