package com.example.watchme.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchme.api.OmdbApi
import com.example.watchme.api.RetrofitClient
import com.example.watchme.data.SearchHistoryRepository
import com.example.watchme.model.OmdbSearchItem
import com.example.watchme.model.SearchHistoryItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface SearchResultsState {
    data object Idle : SearchResultsState
    data object Loading : SearchResultsState
    data class Success(val movies: List<OmdbSearchItem>) : SearchResultsState
    data class Empty(val query: String) : SearchResultsState
    data class Error(val message: String) : SearchResultsState
}

class HomeViewModel : ViewModel() {

    private val api: OmdbApi = RetrofitClient.omdbApi
    private val historyRepository = SearchHistoryRepository()

    private val _searchState = MutableStateFlow<SearchResultsState>(SearchResultsState.Idle)
    val searchState: StateFlow<SearchResultsState> = _searchState.asStateFlow()

    val history: StateFlow<List<SearchHistoryItem>> = historyRepository.observeHistory()
        .catch { e ->
            Log.e(TAG, "Could not load search history", e)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var searchJob: Job? = null

    fun search(query: String) {
        val clean = query.trim()
        if (clean.isBlank()) return

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searchState.value = SearchResultsState.Loading
            try {
                val response = api.searchMovies(clean)
                val movies = response.Search.orEmpty()

                when {
                    response.Response.equals("True", ignoreCase = true) && movies.isNotEmpty() -> {
                        _searchState.value = SearchResultsState.Success(movies)
                        saveToHistory(clean)
                    }
                    response.Error.equals("Movie not found!", ignoreCase = true) ->
                        _searchState.value = SearchResultsState.Empty(clean)
                    else ->
                        _searchState.value = SearchResultsState.Error(
                            response.Error ?: "Something went wrong"
                        )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _searchState.value = SearchResultsState.Error("No internet connection")
            } catch (e: HttpException) {
                _searchState.value = SearchResultsState.Error("Server error (${e.code()})")
            } catch (e: Exception) {
                _searchState.value = SearchResultsState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun deleteHistory(item: SearchHistoryItem) {
        viewModelScope.launch {
            try {
                historyRepository.deleteSearch(item.id)
            } catch (e: Exception) {
                Log.e(TAG, "Could not delete history item", e)
            }
        }
    }

    private fun saveToHistory(query: String) {
        viewModelScope.launch {
            try {
                historyRepository.saveSearch(query)
            } catch (e: Exception) {
                Log.e(TAG, "Could not save search to history", e)
            }
        }
    }

    private companion object {
        const val TAG = "HomeViewModel"
    }
}
