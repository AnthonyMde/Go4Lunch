package com.my.anthonymamode.go4lunch.model

import android.net.Uri

data class User(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUri: Uri?
    // TODO: Add lunch and message properties
)