package com.example.watchme.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.watchme.data.local.entity.MovieHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieHistoryEntity)

    @Query("SELECT * FROM movie_history WHERE LOWER(title) = LOWER(:title) LIMIT 1")
    suspend fun getMovieByName(title: String): MovieHistoryEntity?

    @Query("UPDATE movie_history SET timestamp = :timestamp WHERE LOWER(title) = LOWER(:title)")
    suspend fun updateTimestampByName(
        title: String,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE movie_history SET timestamp = :timestamp WHERE imdbID = :imdbID")
    suspend fun updateTimestampById(
        imdbID: String,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("SELECT * FROM movie_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<MovieHistoryEntity>>

    @Query("SELECT * FROM movie_history ORDER BY timestamp DESC")
    suspend fun getAllHistoryList(): List<MovieHistoryEntity>

    @Query("SELECT * FROM movie_history WHERE title LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMoviesByName(query: String): Flow<List<MovieHistoryEntity>>

    @Query("SELECT * FROM movie_history WHERE title LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    suspend fun searchMoviesByNameOnce(query: String): List<MovieHistoryEntity>

    @Query("DELETE FROM movie_history WHERE imdbID = :imdbID")
    suspend fun deleteMovieById(imdbID: String)

    @Query("DELETE FROM movie_history")
    suspend fun clearAllHistory()
}
