package com.skichrome.portfolio.model.local.util

data class User(
    val firstName: String,
    val lastName: String,
    val summary: String,
    val email: String,
    val phoneNumber: String
)
{
    constructor() : this("", "", "", "", "")
}