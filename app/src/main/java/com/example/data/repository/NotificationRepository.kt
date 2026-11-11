package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.NotificationItem
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NotificationRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in before reading notifications.")
    }

    fun observeNotifications(userId: String? = auth.currentUser?.uid): Flow<List<NotificationItem>> {
        val uid = userId ?: auth.currentUser?.uid
        if (uid.isNullOrBlank() || uid == "guest") {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        val path = "users/$uid/notifications"
        return db.collection("users").document(uid).collection("notifications")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(NotificationItem::class.java)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                emit(emptyList())
            }
    }

    suspend fun markAsRead(notificationId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("notifications").document(notificationId)
        try {
            docRef.update("isRead", true).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
    }

    suspend fun createNotification(
        title: String,
        message: String,
        type: String = "new_opportunity",
        relatedOpportunityId: String = ""
    ) {
        val uid = requireUserId()
        val notifId = UUID.randomUUID().toString()
        val docRef = db.collection("users").document(uid).collection("notifications").document(notifId)
        val payload = mapOf(
            "notificationId" to notifId,
            "userId" to uid,
            "title" to title,
            "message" to message,
            "type" to type,
            "relatedOpportunityId" to relatedOpportunityId,
            "isRead" to false,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
        }
    }
}
