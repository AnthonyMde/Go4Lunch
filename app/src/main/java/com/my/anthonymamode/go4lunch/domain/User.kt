package com.my.anthonymamode.go4lunch.domain

/**
 * Firestore adapter needs to have initialized values to work.
 */
data class User(
    var uid: String = "",
    var displayName: String? = "",
    var email: String? = "",
    var photoPath: String? = "",
    var hasLunch: Boolean = false,
    var lunch: Lunch? = null
)
