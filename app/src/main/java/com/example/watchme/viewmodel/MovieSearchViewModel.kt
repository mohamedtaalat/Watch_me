package com.example.watchme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchme.api.OmdbApi
import com.example.watchme.api.RetrofitClient
import com.example.watchme.data.MovieHistoryRepository
import com.example.watchme.data.local.AppDatabase
import com.example.watchme.data.local.entity.MovieHistoryEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MovieUiState {
    data object Idle : MovieUiState
    data object Loading : MovieUiState
    data class Success(val movie: MovieHistoryEntity) : MovieUiState
    data class Error(val message: String) : MovieUiState
}

class MovieSearchViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: MovieHistoryRepository = MovieHistoryRepository(
        AppDatabase.getDatabase(application).movieHistoryDao()
    ),
    private val api: OmdbApi = RetrofitClient.omdbApi
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<MovieUiState>(MovieUiState.Idle)
    val state: StateFlow<MovieUiState> = _state.asStateFlow()

    /** Cache-first: Room hit -> show it, otherwise call OMDb and store the result. */
    fun search(title: String) {
        val clean = title.trim()
        if (clean.isBlank()) return

        viewModelScope.launch {
            _state.value = MovieUiState.Loading
            try {
                val cached = repository.accessMovieByName(clean)
                if (cached != null) {
                    _state.value = MovieUiState.Success(cached)
                    return@launch
                }

                val response = api.getMovieByTitle(clean)
                if (!response.Response.equals("True", ignoreCase = true)) {
                    _state.value = MovieUiState.Error(response.Error ?: "Movie not found")
                    return@launch
                }

                repository.storeMovie(response)
                _state.value = MovieUiState.Success(response.toEntity())
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _state.value = MovieUiState.Error("No internet connection")
            } catch (e: HttpException) {
                _state.value = MovieUiState.Error("Server error (${e.code()})")
            } catch (e: Exception) {
                _state.value = MovieUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
