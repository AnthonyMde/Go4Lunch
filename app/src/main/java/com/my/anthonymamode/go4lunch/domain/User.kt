package com.my.anthonymamode.go4lunch.domain

data class User(
    var uid: String = "",
    var displayName: String? = "",
    var email: String? = "",
    var photoPath: String? = "",
    var hasLunch: Boolean = false
    // TODO: Add current user restaurant
    // var workmateRestaurantChoice: String? = ""
)