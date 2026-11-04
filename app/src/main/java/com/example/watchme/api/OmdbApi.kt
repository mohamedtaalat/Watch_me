package com.example.watchme.api

import com.example.watchme.model.OmdbMovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {

    // OMDb answers HTTP 200 even for "movie not found", so always check
    // OmdbMovieResponse.Response == "True" before using the body.
    @GET("/")
    suspend fun getMovieByTitle(
        @Query("t") title: String,
        @Query("apikey") apiKey: String = "6eadd55f"
    ): OmdbMovieResponse
}
