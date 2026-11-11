package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.NotificationItem
import com.example.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val userId: String? = null
) : ViewModel() {

    val notifications: StateFlow<List<NotificationItem>> = repository.observeNotifications(userId)
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // Notification preferences state
    val newOpportunitiesAlert = MutableStateFlow(true)
    val matchingOpportunitiesAlert = MutableStateFlow(true)
    val deadlineReminders = MutableStateFlow(true)
    val statusChangeAlerts = MutableStateFlow(true)

    init {
        if (!userId.isNullOrBlank() && userId != "guest") {
            seedSampleNotificationIfEmpty()
        }
    }

    private fun seedSampleNotificationIfEmpty() {
        viewModelScope.launch {
            try {
                val list = notifications.firstOrNull()
                if (list != null && list.isEmpty()) {
                    repository.createNotification(
                        title = "Welcome to Ghana GrantFinder! 🇬🇭",
                        message = "Explore open grants, scholarships and government initiatives tailored to your profile.",
                        type = "new_opportunity"
                    )
                }
            } catch (e: Exception) {
                // Ignore seeding errors in background
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
        }
    }
}
