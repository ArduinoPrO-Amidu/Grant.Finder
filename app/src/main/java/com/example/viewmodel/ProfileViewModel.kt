package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserProfile
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val GHANA_REGIONS = listOf(
    "Greater Accra", "Ashanti", "Western", "Eastern", "Central",
    "Northern", "Volta", "Upper East", "Upper West", "Bono",
    "Bono East", "Ahafo", "Oti", "Western North", "Savannah", "North East"
)

val USER_TYPES = listOf(
    "Student", "Entrepreneur", "Farmer", "Business owner",
    "NGO", "Researcher", "Artisan", "Individual", "Other"
)

val AGE_RANGES = listOf("18–24", "25–34", "35–49", "50+")

val EDUCATION_LEVELS = listOf(
    "High School / WASSCE", "Diploma / TVET",
    "Bachelor's Degree", "Master's / Doctorate", "Apprenticeship / Practical"
)

val ALL_OPPORTUNITY_CATEGORIES = listOf(
    "Business grants", "Agriculture", "Scholarships", "Youth empowerment",
    "Women empowerment", "NGO funding", "SME development", "Research grants",
    "Skills development", "Technology and innovation", "Community development",
    "International opportunities"
)

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val profile: UserProfile?) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModel() {

    val profileState: StateFlow<ProfileUiState> = if (userId.isBlank() || userId == "guest") {
        MutableStateFlow<ProfileUiState>(ProfileUiState.Success(null)).asStateFlow()
    } else {
        MutableStateFlow<ProfileUiState>(ProfileUiState.Loading).apply {
            viewModelScope.launch {
                try {
                    userRepository.observeProfile(userId).collect { profile ->
                        value = ProfileUiState.Success(profile)
                    }
                } catch (e: Exception) {
                    value = ProfileUiState.Error(e.localizedMessage ?: "Failed to load profile")
                }
            }
        }
    }

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    fun saveProfile(
        fullName: String,
        email: String,
        region: String,
        district: String,
        ageRange: String,
        occupation: String,
        educationLevel: String,
        userType: String,
        interests: List<String>,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank()) {
            _saveError.value = "Please enter your full name"
            return
        }
        if (region.isBlank()) {
            _saveError.value = "Please select your region in Ghana"
            return
        }

        _isSaving.value = true
        _saveError.value = null

        viewModelScope.launch {
            try {
                val profile = UserProfile(
                    userId = userId,
                    fullName = fullName.trim(),
                    email = email.trim(),
                    region = region,
                    district = district.trim(),
                    ageRange = ageRange,
                    occupation = occupation.trim(),
                    educationLevel = educationLevel,
                    userType = userType,
                    interests = interests,
                    isPremium = false
                )
                userRepository.saveProfile(profile)
                onSuccess()
            } catch (e: Exception) {
                _saveError.value = e.localizedMessage ?: "Failed to save profile"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun upgradeToPremium(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.upgradeToPremium(userId)
                onSuccess()
            } catch (e: Exception) {
                _saveError.value = e.localizedMessage ?: "Failed to upgrade subscription"
            }
        }
    }

    fun clearError() {
        _saveError.value = null
    }
}
