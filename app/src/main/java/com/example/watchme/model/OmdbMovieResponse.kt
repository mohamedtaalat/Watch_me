package com.example.watchme.model

import com.example.watchme.data.local.entity.MovieHistoryEntity

data class Rating(
    val Source: String? = null,
    val Value: String? = null
)

data class OmdbMovieResponse(
    val Title: String? = null,
    val Year: String? = null,
    val Rated: String? = null,
    val Released: String? = null,
    val Runtime: String? = null,
    val Genre: String? = null,
    val Director: String? = null,
    val Writer: String? = null,
    val Actors: String? = null,
    val Plot: String? = null,
    val Language: String? = null,
    val Country: String? = null,
    val Awards: String? = null,
    val Poster: String? = null,
    val Ratings: List<Rating>? = null,
    val Metascore: String? = null,
    val imdbRating: String? = null,
    val imdbVotes: String? = null,
    val imdbID: String,
    val Type: String? = null,
    val totalSeasons: String? = null,
    val Response: String? = null
) {
    fun toEntity(timestamp: Long = System.currentTimeMillis()): MovieHistoryEntity {
        val sanitizedPosterUrl = if (Poster.equals("N/A", ignoreCase = true) || Poster.isNullOrBlank()) {
            null
        } else {
            Poster
        }

        return MovieHistoryEntity(
            imdbID = imdbID,
            title = Title ?: "Unknown Title",
            year = Year ?: "",
            type = Type ?: "movie",
            genre = Genre,
            plot = Plot,
            posterUrl = sanitizedPosterUrl,
            imdbRating = imdbRating,
            timestamp = timestamp
        )
    }
}
