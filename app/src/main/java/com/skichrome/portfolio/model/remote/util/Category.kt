package com.skichrome.portfolio.model.remote.util

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class Category(
    @JvmField @PropertyName("name") val name: String,
    @JvmField @PropertyName("desc") val description: String,
    @JvmField @PropertyName("image") val imgReference: String? = null,
    @JvmField @PropertyName("image_alt") val imageAlt: String,
    @JvmField @Exclude val localFileReference: String? = null
) : Model()
{
    constructor() : this(name = "", description = "", imageAlt = "")
}