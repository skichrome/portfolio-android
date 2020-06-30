package com.skichrome.portfolio.model.remote.util

import android.net.Uri
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class Theme(
    @JvmField @PropertyName("name") val name: String,
    @JvmField @PropertyName("description") val description: String,
    @JvmField @PropertyName("image") val imgReference: String? = null,
    @JvmField @PropertyName("image_alt") val imgReferenceAlt: String,
    @JvmField @Exclude val localImgReference: Uri? = null
) : Model()
{
    constructor() : this(name = "", description = "", imgReferenceAlt = "")
}