package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Opportunity
import com.example.data.model.TrackedApplication
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

class ApplicationTrackerRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in before tracking applications.")
    }

    fun observeApplications(userId: String? = auth.currentUser?.uid): Flow<List<TrackedApplication>> {
        val uid = userId ?: auth.currentUser?.uid
        if (uid.isNullOrBlank() || uid == "guest") {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        val path = "users/$uid/applications"
        return db.collection("users").document(uid).collection("applications")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(TrackedApplication::class.java) }
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

    suspend fun trackOpportunity(
        opportunity: Opportunity,
        initialStatus: String = "Interested",
        notes: String = ""
    ): String {
        val uid = requireUserId()
        val appId = UUID.randomUUID().toString()
        val docRef = db.collection("users").document(uid).collection("applications").document(appId)
        val payload = mapOf(
            "applicationId" to appId,
            "opportunityId" to opportunity.id,
            "userId" to uid,
            "opportunityTitle" to opportunity.title,
            "organization" to opportunity.organization,
            "status" to initialStatus,
            "notes" to notes,
            "appliedDate" to "",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            return appId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateStatus(applicationId: String, newStatus: String, notes: String? = null, appliedDate: String? = null) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("applications").document(applicationId)
        val updateMap = mutableMapOf<String, Any>(
            "status" to newStatus,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (notes != null) updateMap["notes"] = notes
        if (appliedDate != null) updateMap["appliedDate"] = appliedDate

        try {
            docRef.update(updateMap).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteApplication(applicationId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("applications").document(applicationId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }
}
