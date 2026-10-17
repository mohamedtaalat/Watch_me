package com.example.watchme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchme.data.AuthRepository
import com.example.watchme.model.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState =
        MutableStateFlow<AuthResult?>(null)

    val authState: StateFlow<AuthResult?> =
        _authState



    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            _authState.value = AuthResult.Loading

            _authState.value =
                repository.login(email, password)
        }
    }

    fun signup(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _authState.value = AuthResult.Loading

            _authState.value =
                repository.signup(email, password)
        }
    }

    fun resetPassword(email: String) {

        viewModelScope.launch {

            _authState.value = AuthResult.Loading

            _authState.value =
                repository.resetPassword(email)
        }
    }

    fun logout() {
        repository.logout()
    }
}