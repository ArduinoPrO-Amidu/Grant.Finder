package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Opportunity(
    @DocumentId val id: String = "",
    val title: String = "",
    val organization: String = "",
    val category: String = "",
    val shortDescription: String = "",
    val fullDescription: String = "",
    val fundingAmount: String = "",
    val currency: String = "GHS",
    val applicationDeadline: String = "",
    val opportunityStatus: String = "open", // open, upcoming, closing_soon, closed, unverified
    val verificationStatus: String = "verified", // verified, demo, community_sourced
    val eligibilityCriteria: List<String> = emptyList(),
    val eligibleApplicantTypes: List<String> = emptyList(),
    val requiredDocuments: List<String> = emptyList(),
    val openingDate: String = "",
    val applicationProcedure: String = "",
    val officialSource: String = "",
    val officialUrl: String = "",
    val lastVerificationDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
