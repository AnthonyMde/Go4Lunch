package com.my.anthonymamode.go4lunch.domain

/**
 * Firestore adapter needs to have initialized values to work.
 */
data class Message(
    val authorUid: String = "",
    val content: String = "",
    // TODO: use real Date
    val dateCreated: String = ""
)
