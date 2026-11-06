package com.example.watchme.data

import com.example.watchme.data.local.dao.MovieHistoryDao
import com.example.watchme.data.local.entity.MovieHistoryEntity
import com.example.watchme.model.OmdbMovieResponse
import kotlinx.coroutines.flow.Flow

class MovieHistoryRepository(
    private val movieHistoryDao: MovieHistoryDao
) {

    suspend fun storeMovie(response: OmdbMovieResponse) {
        val entity = response.toEntity()
        movieHistoryDao.insertMovie(entity)
    }

    suspend fun storeMovie(movie: MovieHistoryEntity) {
        movieHistoryDao.insertMovie(movie)
    }

    suspend fun accessMovieByName(title: String): MovieHistoryEntity? {
        val cleanTitle = title.trim()
        val cachedMovie = movieHistoryDao.getMovieByName(cleanTitle)

        if (cachedMovie != null) {
            val now = System.currentTimeMillis()
            movieHistoryDao.updateTimestampByName(cleanTitle, now)
            return cachedMovie.copy(timestamp = now)
        }

        return null
    }

    suspend fun updateTimestampByName(title: String, timestamp: Long = System.currentTimeMillis()) {
        movieHistoryDao.updateTimestampByName(title.trim(), timestamp)
    }

    fun getAllHistory(): Flow<List<MovieHistoryEntity>> {
        return movieHistoryDao.getAllHistory()
    }

    suspend fun getAllHistoryList(): List<MovieHistoryEntity> {
        return movieHistoryDao.getAllHistoryList()
    }

    fun searchMoviesByName(query: String): Flow<List<MovieHistoryEntity>> {
        return movieHistoryDao.searchMoviesByName(query.trim())
    }

    suspend fun searchMoviesByNameOnce(query: String): List<MovieHistoryEntity> {
        return movieHistoryDao.searchMoviesByNameOnce(query.trim())
    }

    suspend fun clearHistory() {
        movieHistoryDao.clearAllHistory()
    }

    suspend fun deleteMovie(imdbID: String) {
        movieHistoryDao.deleteMovieById(imdbID)
    }
}
