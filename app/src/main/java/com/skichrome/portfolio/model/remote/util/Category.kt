package com.skichrome.portfolio.model.remote.util

import com.google.firebase.firestore.PropertyName

data class Category(
    @JvmField @PropertyName("name") val name: String,
    @JvmField @PropertyName("desc") val description: String
) : Model()
{
    constructor() : this("", "")
}