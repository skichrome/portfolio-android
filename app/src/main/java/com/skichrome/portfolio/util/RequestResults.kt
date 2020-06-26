package com.skichrome.portfolio.util

sealed class RequestResults<out R>
{
    data class Success<out T>(val data: T) : RequestResults<T>()
    data class Error(val exception: Exception) : RequestResults<Nothing>()
    object Loading : RequestResults<Nothing>()

    override fun toString(): String
    {
        return when (this)
        {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}