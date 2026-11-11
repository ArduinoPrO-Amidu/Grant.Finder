package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class SavedOpportunity(
    @DocumentId val id: String = "",
    val opportunityId: String = "",
    val userId: String = "",
    val title: String = "",
    val organization: String = "",
    val category: String = "",
    val fundingAmount: String = "",
    val applicationDeadline: String = "",
    val opportunityStatus: String = "open",
    @ServerTimestamp val savedAt: Timestamp? = null
)
