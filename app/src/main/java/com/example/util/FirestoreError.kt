package com.example.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException

enum class OperationType {
    CREATE,
    READ,
    LIST,
    UPDATE,
    DELETE
}

private const val TAG = "FirestoreError"

fun handleFirestoreError(
    exception: Exception,
    operationType: OperationType,
    path: String
) {
    val firestoreException = exception as? FirebaseFirestoreException
    val code = firestoreException?.code?.name ?: "UNKNOWN"
    val message = exception.localizedMessage ?: "Unknown Firestore error"

    Log.e(
        TAG,
        "Firestore error during $operationType on path '$path': [$code] $message",
        exception
    )
}
