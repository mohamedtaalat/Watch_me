package com.example.watchme

import com.example.watchme.model.OmdbMovieResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OmdbMappingTest {

    @Test
    fun omdbResponse_mapsToEntity_correctlyWithValidPoster() {
        val sampleOmdb = OmdbMovieResponse(
            Title = "Naruto",
            Year = "2002–2007",
            Rated = "TV-PG",
            Released = "10 Sep 2005",
            Runtime = "24 min",
            Genre = "Animation, Action, Adventure",
            Director = "N/A",
            Writer = "Masashi Kishimoto",
            Actors = "Junko Takeuchi, Maile Flanagan, Kate Higgins",
            Plot = "Naruto Uzumaki, a mischievous adolescent ninja...",
            Language = "Japanese",
            Country = "Japan",
            Awards = "1 win total",
            Poster = "https://m.media-amazon.com/images/sample.jpg",
            imdbRating = "8.4",
            imdbVotes = "175,428",
            imdbID = "tt0409591",
            Type = "series",
            totalSeasons = "1",
            Response = "True"
        )

        val entity = sampleOmdb.toEntity()

        assertEquals("tt0409591", entity.imdbID)
        assertEquals("Naruto", entity.title)
        assertEquals("2002–2007", entity.year)
        assertEquals("series", entity.type)
        assertEquals("https://m.media-amazon.com/images/sample.jpg", entity.posterUrl)
        assertEquals("8.4", entity.imdbRating)
    }

    @Test
    fun omdbResponse_sanitizesNA_posterToNull() {
        val sampleWithoutPoster = OmdbMovieResponse(
            Title = "Unknown Indie Film",
            Year = "2024",
            Poster = "N/A",
            imdbID = "tt9999999"
        )

        val entity = sampleWithoutPoster.toEntity()

        assertNull(entity.posterUrl)
    }
}

