package com.skichrome.portfolio.model.local.util

import com.google.firebase.firestore.PropertyName

data class User(
    @JvmField @PropertyName("first_name") val firstName: String,
    @JvmField @PropertyName("last_name") val lastName: String,
    @JvmField @PropertyName("summary") val summary: String,
    @JvmField @PropertyName("email") val email: String,
    @JvmField @PropertyName("phone_number") val phoneNumber: String,
    @JvmField @PropertyName("photo_reference") val photoReference: String?
)
{
    constructor() : this("", "", "", "", "", null)
}