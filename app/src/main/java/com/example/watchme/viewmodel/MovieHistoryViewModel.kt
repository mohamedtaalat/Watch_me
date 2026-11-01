package com.example.watchme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchme.data.MovieHistoryRepository
import com.example.watchme.data.local.AppDatabase
import com.example.watchme.data.local.entity.MovieHistoryEntity
import com.example.watchme.model.OmdbMovieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieHistoryViewModel(
    application: Application,
    private val repository: MovieHistoryRepository = MovieHistoryRepository(
        AppDatabase.getDatabase(application).movieHistoryDao()
    )
) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val historyList: StateFlow<List<MovieHistoryEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllHistory()
            } else {
                repository.searchMoviesByName(query.trim())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun accessMovieByName(title: String): MovieHistoryEntity? {
        return withContext(Dispatchers.IO) {
            repository.accessMovieByName(title)
        }
    }

    fun storeMovie(response: OmdbMovieResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.storeMovie(response)
        }
    }

    fun searchMovies(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun deleteMovie(imdbID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovie(imdbID)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearHistory()
        }
    }
}
