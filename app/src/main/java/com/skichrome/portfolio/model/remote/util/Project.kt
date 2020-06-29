package com.skichrome.portfolio.model.remote.util

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Project(
    @JvmField @PropertyName("title") val title: String,
    @JvmField @PropertyName("created") val createdAt: Timestamp,
    @JvmField @PropertyName("post_content") var content: List<ParagraphContent> = emptyList(),
    @JvmField @PropertyName("main_picture") val mainPicture: String? = null,
    @JvmField @PropertyName("main_picture_alt") val mainPictureAlt: String? = null
) : Model()
{
    constructor() : this("", Timestamp(0, 0))
}

data class ParagraphContent(
    @JvmField @PropertyName("title") var postTitle: String,
    @JvmField @PropertyName("content") var postContentText: String,
    @JvmField @PropertyName("image") var postImage: String? = null
)
{
    constructor() : this("", "")
}