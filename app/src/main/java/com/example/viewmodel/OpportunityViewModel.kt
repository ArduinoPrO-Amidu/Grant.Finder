package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Opportunity
import com.example.data.repository.OpportunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

import kotlinx.coroutines.flow.map

data class OpportunityFilter(
    val query: String = "",
    val category: String? = null,
    val applicantType: String? = null,
    val status: String? = null,
    val sort: SortOrder = SortOrder.NEWEST_FIRST
)

sealed interface OpportunityListUiState {
    data object Loading : OpportunityListUiState
    data class Success(val opportunities: List<Opportunity>) : OpportunityListUiState
    data class Error(val message: String) : OpportunityListUiState
}

enum class SortOrder {
    NEWEST_FIRST,
    CLOSING_SOONEST
}

class OpportunityViewModel(
    private val repository: OpportunityRepository
) : ViewModel() {

    private val _rawOpportunities = repository.observeOpportunities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedApplicantType = MutableStateFlow<String?>(null)
    val selectedApplicantType: StateFlow<String?> = _selectedApplicantType.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _filterState = combine(
        _searchQuery,
        _selectedCategory,
        _selectedApplicantType,
        _selectedStatus,
        _sortOrder
    ) { query, category, applicantType, status, sort ->
        OpportunityFilter(query, category, applicantType, status, sort)
    }

    // Filtered list for Explore screen
    val filteredOpportunities: StateFlow<List<Opportunity>> = combine(
        _rawOpportunities,
        _filterState
    ) { opportunities, filter ->
        opportunities.filter { opp ->
            val matchesQuery = filter.query.isBlank() ||
                    opp.title.contains(filter.query, ignoreCase = true) ||
                    opp.organization.contains(filter.query, ignoreCase = true) ||
                    opp.shortDescription.contains(filter.query, ignoreCase = true) ||
                    opp.category.contains(filter.query, ignoreCase = true)

            val matchesCategory = filter.category == null || opp.category.equals(filter.category, ignoreCase = true)

            val matchesApplicant = filter.applicantType == null ||
                    opp.eligibleApplicantTypes.any { it.contains(filter.applicantType, ignoreCase = true) }

            val matchesStatus = filter.status == null ||
                    opp.opportunityStatus.equals(filter.status, ignoreCase = true)

            matchesQuery && matchesCategory && matchesApplicant && matchesStatus
        }.let { list ->
            when (filter.sort) {
                SortOrder.NEWEST_FIRST -> list
                SortOrder.CLOSING_SOONEST -> list.sortedBy { it.opportunityStatus != "closing_soon" }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    // Featured opportunities (first 4)
    val featuredOpportunities: StateFlow<List<Opportunity>> = _rawOpportunities
        .map { opps -> opps.take(4) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // Closing soon opportunities
    val closingSoonOpportunities: StateFlow<List<Opportunity>> = _rawOpportunities
        .map { opps ->
            opps.filter { it.opportunityStatus == "closing_soon" || it.opportunityStatus == "open" }.take(3)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun onApplicantTypeSelected(applicantType: String?) {
        _selectedApplicantType.value = if (_selectedApplicantType.value == applicantType) null else applicantType
    }

    fun onStatusSelected(status: String?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
    }

    fun onSortOrderChanged(sort: SortOrder) {
        _sortOrder.value = sort
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _selectedApplicantType.value = null
        _selectedStatus.value = null
        _sortOrder.value = SortOrder.NEWEST_FIRST
    }
}
