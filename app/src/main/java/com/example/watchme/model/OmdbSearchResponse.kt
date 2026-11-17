package com.example.watchme.model

data class OmdbSearchItem(
    val Title: String? = null,
    val Year: String? = null,
    val imdbID: String = "",
    val Type: String? = null,
    val Poster: String? = null
)

data class OmdbSearchResponse(
    val Search: List<OmdbSearchItem>? = null,
    val totalResults: String? = null,
    val Response: String? = null,
    val Error: String? = null
)
