package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Opportunity
import com.example.data.model.SavedOpportunity
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

class SavedOpportunityRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in before accessing saved opportunities.")
    }

    fun observeSavedOpportunities(userId: String? = auth.currentUser?.uid): Flow<List<SavedOpportunity>> {
        val uid = userId ?: auth.currentUser?.uid
        if (uid.isNullOrBlank() || uid == "guest") {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        val path = "users/$uid/saved_opportunities"
        return db.collection("users").document(uid).collection("saved_opportunities")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(SavedOpportunity::class.java) }
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

    suspend fun saveOpportunity(opportunity: Opportunity) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("saved_opportunities").document(opportunity.id)
        val payload = mapOf(
            "opportunityId" to opportunity.id,
            "userId" to uid,
            "title" to opportunity.title,
            "organization" to opportunity.organization,
            "category" to opportunity.category,
            "fundingAmount" to opportunity.fundingAmount,
            "applicationDeadline" to opportunity.applicationDeadline,
            "opportunityStatus" to opportunity.opportunityStatus,
            "savedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun removeSavedOpportunity(opportunityId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("saved_opportunities").document(opportunityId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    fun isOpportunitySaved(opportunityId: String): Flow<Boolean> {
        val uid = requireUserId()
        return db.collection("users").document(uid).collection("saved_opportunities").document(opportunityId)
            .snapshots()
            .map { it.exists() }
            .catch { emit(false) }
    }
}
