package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.TrackedApplication
import com.example.data.repository.ApplicationTrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val APPLICATION_STATUSES = listOf(
    "Interested",
    "Preparing application",
    "Submitted",
    "Under review",
    "Approved",
    "Rejected"
)

class TrackerViewModel(
    private val trackerRepository: ApplicationTrackerRepository,
    userId: String? = null
) : ViewModel() {

    private val _rawApplications = trackerRepository.observeApplications(userId)
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _filterStatus = MutableStateFlow<String?>(null)
    val filterStatus: StateFlow<String?> = _filterStatus.asStateFlow()

    val filteredApplications: StateFlow<List<TrackedApplication>> = combine(
        _rawApplications,
        _filterStatus
    ) { apps, filter ->
        if (filter == null || filter == "All") {
            apps
        } else {
            apps.filter { it.status.equals(filter, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun setFilter(status: String?) {
        _filterStatus.value = status
    }

    fun updateStatus(applicationId: String, newStatus: String, notes: String? = null, appliedDate: String? = null) {
        viewModelScope.launch {
            try {
                trackerRepository.updateStatus(applicationId, newStatus, notes, appliedDate)
                _toastMessage.value = "Updated to $newStatus"
            } catch (e: Exception) {
                _toastMessage.value = e.localizedMessage ?: "Failed to update status"
            }
        }
    }

    fun deleteApplication(applicationId: String) {
        viewModelScope.launch {
            try {
                trackerRepository.deleteApplication(applicationId)
                _toastMessage.value = "Application removed from tracker"
            } catch (e: Exception) {
                _toastMessage.value = e.localizedMessage ?: "Failed to delete"
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
