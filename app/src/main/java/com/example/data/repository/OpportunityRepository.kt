package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.demo.DemoOpportunitiesData
import com.example.data.model.Opportunity
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class OpportunityRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    fun observeOpportunities(): Flow<List<Opportunity>> {
        val path = "opportunities"
        return db.collection("opportunities")
            .snapshots()
            .map { snapshot ->
                val list = snapshot.toObjects(Opportunity::class.java)
                if (list.isNotEmpty()) {
                    list
                } else {
                    DemoOpportunitiesData.sampleOpportunities
                }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                // Graceful fallback to demo dataset if network is unavailable
                emit(DemoOpportunitiesData.sampleOpportunities)
            }
    }

    suspend fun getOpportunityById(id: String): Opportunity? {
        val path = "opportunities/$id"
        return try {
            val doc = db.collection("opportunities").document(id).get().await()
            if (doc.exists()) {
                doc.toObject(Opportunity::class.java)
            } else {
                DemoOpportunitiesData.sampleOpportunities.firstOrNull { it.id == id }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.READ, path)
            DemoOpportunitiesData.sampleOpportunities.firstOrNull { it.id == id }
        }
    }
}
