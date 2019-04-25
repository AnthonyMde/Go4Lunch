package com.my.anthonymamode.go4lunch.domain

import com.google.gson.annotations.SerializedName

data class Places(
    val html_attributions: List<String>,
    val next_page_token: String?,
    @SerializedName("results")
    val places: List<Place>,
    val status: String,
    val error_message: String?
)