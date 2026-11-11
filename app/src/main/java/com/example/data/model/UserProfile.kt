package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val region: String = "",
    val district: String = "",
    val ageRange: String = "",
    val occupation: String = "",
    val educationLevel: String = "",
    val userType: String = "Individual",
    val interests: List<String> = emptyList(),
    val isPremium: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
