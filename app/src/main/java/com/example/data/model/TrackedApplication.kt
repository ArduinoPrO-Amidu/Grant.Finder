package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class TrackedApplication(
    @DocumentId val id: String = "",
    val applicationId: String = "",
    val opportunityId: String = "",
    val userId: String = "",
    val opportunityTitle: String = "",
    val organization: String = "",
    val status: String = "Interested", // Interested, Preparing application, Submitted, Under review, Approved, Rejected
    val notes: String = "",
    val appliedDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
