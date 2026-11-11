package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.UserProfile
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

class UserRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in before accessing Firestore.")
    }

    fun observeProfile(uid: String): Flow<UserProfile?> {
        if (uid.isBlank() || uid == "guest") {
            return kotlinx.coroutines.flow.flowOf(null)
        }
        val path = "users/$uid"
        return db.collection("users").document(uid)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java)
                } else {
                    null
                }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                    handleFirestoreError(exceptionToLog, OperationType.READ, path)
                }
                throw error
            }
    }

    suspend fun getProfile(uid: String): UserProfile? {
        val path = "users/$uid"
        return try {
            val doc = db.collection("users").document(uid).get().await()
            if (doc.exists()) doc.toObject(UserProfile::class.java) else null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.READ, path)
            null
        }
    }

    suspend fun saveProfile(profile: UserProfile) {
        val uid = requireUserId()
        require(profile.userId == uid) { "Profile userId must match authenticated user" }
        val docRef = db.collection("users").document(uid)

        // Check if existing document exists to preserve createdAt on update
        val existingDoc = try { docRef.get().await() } catch (e: Exception) { null }
        val payload: Map<String, Any?> = if (existingDoc != null && existingDoc.exists()) {
            mapOf(
                "fullName" to profile.fullName,
                "email" to profile.email,
                "region" to profile.region,
                "district" to profile.district,
                "ageRange" to profile.ageRange,
                "occupation" to profile.occupation,
                "educationLevel" to profile.educationLevel,
                "userType" to profile.userType,
                "interests" to profile.interests,
                "isPremium" to profile.isPremium,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        } else {
            mapOf(
                "userId" to uid,
                "fullName" to profile.fullName,
                "email" to profile.email,
                "region" to profile.region,
                "district" to profile.district,
                "ageRange" to profile.ageRange,
                "occupation" to profile.occupation,
                "educationLevel" to profile.educationLevel,
                "userType" to profile.userType,
                "interests" to profile.interests,
                "isPremium" to profile.isPremium,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        }

        try {
            if (existingDoc != null && existingDoc.exists()) {
                docRef.update(payload.filterValues { it != null }).await()
            } else {
                docRef.set(payload.filterValues { it != null }).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, if (existingDoc?.exists() == true) OperationType.UPDATE else OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateProfile(profile: UserProfile) {
        saveProfile(profile)
    }

    suspend fun upgradeToPremium(uid: String) {
        val currentUid = requireUserId()
        require(currentUid == uid)
        val docRef = db.collection("users").document(uid)
        try {
            docRef.update(
                mapOf(
                    "isPremium" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }
}
