package com.skichrome.portfolio.model.remote.util

import android.net.Uri
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class User(
    @JvmField @PropertyName("first_name") val firstName: String,
    @JvmField @PropertyName("last_name") val lastName: String,
    @JvmField @PropertyName("summary") val summary: String,
    @JvmField @PropertyName("email") val email: String,
    @JvmField @PropertyName("phone_number") val phoneNumber: String,
    @JvmField @PropertyName("photo_reference") val photoReference: String? = null,
    @JvmField @Exclude val localPhotoReference: Uri? = null
) : Model()
{
    constructor() : this(firstName = "", lastName = "", summary = "", email = "", phoneNumber = "")
}