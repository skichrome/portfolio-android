package com.skichrome.portfolio.model.remote.util

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Project(
    @JvmField @PropertyName("title") val title: String,
    @JvmField @PropertyName("created") val createdAt: Timestamp,
    @JvmField @PropertyName("post_content") val content: List<PostContent> = emptyList(),
    @JvmField @PropertyName("main_picture") val mainPicture: String? = null,
    @JvmField @PropertyName("main_picture_alt") val mainPictureAlt: String? = null
) : Model()
{
    constructor() : this("", Timestamp(0, 0))
}

data class PostContent(
    @JvmField @PropertyName("title") val postTitle: String,
    @JvmField @PropertyName("content") val postContentText: String,
    @JvmField @PropertyName("image") val postImage: String? = null
)
{
    constructor() : this("", "")
}