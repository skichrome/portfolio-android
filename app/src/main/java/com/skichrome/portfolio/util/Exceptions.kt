package com.skichrome.portfolio.util

abstract class PortfolioException(msg: String, e: Exception? = null) : Exception(msg, e)

class FirebaseFirestoreClassCastException(msg: String, e: Exception? = null) : PortfolioException(msg, e)