package com.my.anthonymamode.go4lunch.domain

data class FirebaseUserUpdate(
    var uid: String = "",
    var displayName: String? = "",
    var email: String? = "",
    var photoPath: String? = ""
)
