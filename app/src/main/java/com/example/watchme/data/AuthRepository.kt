package com.example.watchme.data

import com.example.watchme.model.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(
        email: String,
        password: String
    ): AuthResult {

        return try {

            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()

            AuthResult.Success

        } catch (e: Exception) {

            AuthResult.Error(
                e.message ?: "Login failed"
            )
        }
    }

    suspend fun signup(
        email: String,
        password: String
    ): AuthResult {

        return try {

            auth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

            AuthResult.Success

        } catch (e: Exception) {

            AuthResult.Error(
                e.message ?: "Registration failed"
            )
        }
    }

    suspend fun resetPassword(
        email: String
    ): AuthResult {

        return try {

            auth.sendPasswordResetEmail(email).await()

            AuthResult.Success

        } catch (e: Exception) {

            AuthResult.Error(
                e.message ?: "Failed to send reset email"
            )
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
    }
}