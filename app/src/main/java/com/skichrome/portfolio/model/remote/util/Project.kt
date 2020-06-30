package com.skichrome.portfolio.model.remote.util

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class Project(
    @JvmField @PropertyName("title") val title: String,
    @JvmField @PropertyName("description") val description: String,
    @JvmField @PropertyName("created") val createdAt: Timestamp,
    @JvmField @PropertyName("post_content") var content: MutableList<ParagraphContent> = mutableListOf(),
    @JvmField @PropertyName("main_picture") val mainPicture: String? = null,
    @JvmField @PropertyName("main_picture_alt") val mainPictureAlt: String? = null,
    @JvmField @Exclude val localMainPicture: Uri? = null
) : Model()
{
    constructor() : this("", "", Timestamp(0, 0))
}

@IgnoreExtraProperties
data class ParagraphContent(
    @JvmField @PropertyName("title") var postTitle: String,
    @JvmField @PropertyName("content") var postContentText: String,
    @JvmField @PropertyName("image") var postImage: String? = null,
    @JvmField @Exclude var localPostImage: Uri? = null,
    @JvmField @Exclude var index: Int = -1
) : Model()
{
    constructor() : this("", "")
}