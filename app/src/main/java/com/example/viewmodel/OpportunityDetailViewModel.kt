package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EligibilityResult
import com.example.data.model.Opportunity
import com.example.data.model.UserProfile
import com.example.data.repository.AiAssistantRepository
import com.example.data.repository.ApplicationTrackerRepository
import com.example.data.repository.OpportunityRepository
import com.example.data.repository.SavedOpportunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface EligibilityUiState {
    data object Idle : EligibilityUiState
    data object Assessing : EligibilityUiState
    data class Completed(val result: EligibilityResult) : EligibilityUiState
    data class Error(val message: String) : EligibilityUiState
}

class OpportunityDetailViewModel(
    private val opportunityRepository: OpportunityRepository,
    private val savedRepository: SavedOpportunityRepository,
    private val trackerRepository: ApplicationTrackerRepository,
    private val aiRepository: AiAssistantRepository,
    val opportunityId: String
) : ViewModel() {

    private val _opportunity = MutableStateFlow<Opportunity?>(null)
    val opportunity: StateFlow<Opportunity?> = _opportunity.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isSaved: StateFlow<Boolean> = savedRepository.isOpportunitySaved(opportunityId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    private val _eligibilityState = MutableStateFlow<EligibilityUiState>(EligibilityUiState.Idle)
    val eligibilityState: StateFlow<EligibilityUiState> = _eligibilityState.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    init {
        loadOpportunity()
    }

    private fun loadOpportunity() {
        viewModelScope.launch {
            _isLoading.value = true
            _opportunity.value = opportunityRepository.getOpportunityById(opportunityId)
            _isLoading.value = false
        }
    }

    fun toggleSave() {
        val opp = _opportunity.value ?: return
        viewModelScope.launch {
            try {
                if (isSaved.value) {
                    savedRepository.removeSavedOpportunity(opp.id)
                    _actionMessage.value = "Removed from saved opportunities"
                } else {
                    savedRepository.saveOpportunity(opp)
                    _actionMessage.value = "Saved to your bookmarks"
                }
            } catch (e: Exception) {
                _actionMessage.value = e.localizedMessage ?: "Failed to update bookmark"
            }
        }
    }

    fun trackApplication(status: String, notes: String, onSuccess: () -> Unit) {
        val opp = _opportunity.value ?: return
        viewModelScope.launch {
            try {
                trackerRepository.trackOpportunity(opp, initialStatus = status, notes = notes)
                _actionMessage.value = "Application status set to: $status"
                onSuccess()
            } catch (e: Exception) {
                _actionMessage.value = e.localizedMessage ?: "Failed to track application"
            }
        }
    }

    fun assessEligibility(profile: UserProfile?) {
        val opp = _opportunity.value ?: return
        if (profile == null) {
            _eligibilityState.value = EligibilityUiState.Error("Please complete your profile first to check eligibility.")
            return
        }

        _eligibilityState.value = EligibilityUiState.Assessing

        viewModelScope.launch {
            val result = aiRepository.checkEligibility(profile, opp)
            result.fold(
                onSuccess = { res ->
                    _eligibilityState.value = EligibilityUiState.Completed(res)
                },
                onFailure = { err ->
                    _eligibilityState.value = EligibilityUiState.Error(err.localizedMessage ?: "Eligibility assessment failed")
                }
            )
        }
    }

    fun dismissEligibility() {
        _eligibilityState.value = EligibilityUiState.Idle
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
