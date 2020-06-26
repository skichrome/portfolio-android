package com.skichrome.portfolio.model.remote.util

import com.google.firebase.firestore.PropertyName

data class Theme(
    @JvmField @PropertyName("name") val name: String,
    @JvmField @PropertyName("description") val description: String
) : Model()
{
    constructor() : this("", "")
}