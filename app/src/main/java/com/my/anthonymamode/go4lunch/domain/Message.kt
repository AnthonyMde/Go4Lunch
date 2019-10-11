package com.my.anthonymamode.go4lunch.domain

import java.util.Date

/**
 * Firestore adapter needs to have initialized values to work.
 */
data class Message(
    val authorUid: String = "",
    val content: String = "",
    val dateCreated: Date? = Date()
)
