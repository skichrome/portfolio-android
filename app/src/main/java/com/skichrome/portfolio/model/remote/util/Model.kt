package com.skichrome.portfolio.model.remote.util

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@Suppress("UNCHECKED_CAST")
@IgnoreExtraProperties
abstract class Model
{
    @Exclude
    var id: String = ""

    fun <T : Model> withId(id: String): T
    {
        this.id = id
        return this as T
    }
}