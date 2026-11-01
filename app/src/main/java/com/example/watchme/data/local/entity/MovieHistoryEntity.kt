package com.example.watchme.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movie_history",
    indices = [Index(value = ["title"])]
)
data class MovieHistoryEntity(
    @PrimaryKey
    val imdbID: String,
    val title: String,
    val year: String,
    val type: String,
    val genre: String? = null,
    val plot: String? = null,
    val posterUrl: String? = null,
    val imdbRating: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
