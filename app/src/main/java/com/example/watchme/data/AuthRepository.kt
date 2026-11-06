package com.example.watchme.data

import com.example.watchme.model.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
            val message = when {
                e.message?.contains("badly formatted", ignoreCase = true) == true ->
                    "The email address is badly formatted."
                e.message?.contains("no user record", ignoreCase = true) == true ||
                e.message?.contains("user-not-found", ignoreCase = true) == true ->
                    "No account found with this email."
                e.message?.contains("network", ignoreCase = true) == true ->
                    "Network error. Please check your connection."
                e.message?.contains("operation-not-allowed", ignoreCase = true) == true ->
                    "Password reset is disabled in Firebase Console."
                else -> e.localizedMessage ?: e.message ?: "Failed to send reset email"
            }
            AuthResult.Error(message)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun userLoggedIn(): FirebaseUser? {
        return auth.currentUser
    }

    fun logout() {
        auth.signOut()
    }
}
