package com.my.anthonymamode.go4lunch.domain

import java.util.Date

data class Message(
    val authorUid: String,
    val content: String,
    val dateCreated: Date
)
