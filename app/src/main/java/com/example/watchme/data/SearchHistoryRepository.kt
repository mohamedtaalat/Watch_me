package com.example.watchme.data

import com.example.watchme.model.SearchHistoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class SearchHistoryRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun historyCollection(): CollectionReference? {
        val uid = auth.currentUser?.uid ?: return null
        return firestore.collection("users").document(uid).collection("search_history")
    }

    fun observeHistory(): Flow<List<SearchHistoryItem>> = callbackFlow {
        val collection = historyCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(HISTORY_LIMIT)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    val query = doc.getString("query") ?: return@mapNotNull null
                    SearchHistoryItem(
                        id = doc.id,
                        query = query,
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }.orEmpty()
                trySend(items)
            }

        awaitClose { registration.remove() }
    }

    suspend fun saveSearch(query: String) {
        val clean = query.trim()
        if (clean.isEmpty()) return
        val collection = historyCollection() ?: return
        collection.document(documentId(clean))
            .set(mapOf("query" to clean, "timestamp" to System.currentTimeMillis()))
            .await()
    }

    suspend fun deleteSearch(id: String) {
        historyCollection()?.document(id)?.delete()?.await()
    }

    private fun documentId(query: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(query.lowercase().toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val HISTORY_LIMIT = 50L
    }
}
