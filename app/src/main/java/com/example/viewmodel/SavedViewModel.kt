package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.SavedOpportunity
import com.example.data.repository.SavedOpportunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.catch

class SavedViewModel(
    private val savedRepository: SavedOpportunityRepository,
    userId: String? = null
) : ViewModel() {

    val savedList: StateFlow<List<SavedOpportunity>> = savedRepository.observeSavedOpportunities(userId)
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun removeOpportunity(id: String) {
        viewModelScope.launch {
            try {
                savedRepository.removeSavedOpportunity(id)
                _message.value = "Removed from bookmarks"
            } catch (e: Exception) {
                _message.value = e.localizedMessage ?: "Failed to remove"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
