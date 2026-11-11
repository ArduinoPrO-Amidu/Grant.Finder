package com.example.data.model

data class EligibilityResult(
    val score: Int = 0,
    val verdict: String = "",
    val requirementsMet: List<String> = emptyList(),
    val requirementsNotMet: List<String> = emptyList(),
    val informationNeeded: List<String> = emptyList(),
    val explanation: String = "",
    val recommendedNextSteps: List<String> = emptyList()
)
