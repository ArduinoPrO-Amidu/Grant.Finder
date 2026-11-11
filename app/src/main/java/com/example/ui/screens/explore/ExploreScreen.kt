package com.example.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Opportunity
import com.example.ui.components.OpportunityCard
import com.example.ui.theme.CharcoalSubtle
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.ForestGreenPrimary
import com.example.viewmodel.ALL_OPPORTUNITY_CATEGORIES
import com.example.viewmodel.OpportunityViewModel
import com.example.viewmodel.SortOrder
import com.example.viewmodel.USER_TYPES

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    opportunityViewModel: OpportunityViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val searchQuery by opportunityViewModel.searchQuery.collectAsState()
    val selectedCategory by opportunityViewModel.selectedCategory.collectAsState()
    val selectedApplicantType by opportunityViewModel.selectedApplicantType.collectAsState()
    val selectedStatus by opportunityViewModel.selectedStatus.collectAsState()
    val sortOrder by opportunityViewModel.sortOrder.collectAsState()
    val opportunities by opportunityViewModel.filteredOpportunities.collectAsState()

    val hasActiveFilters = searchQuery.isNotBlank() ||
            selectedCategory != null ||
            selectedApplicantType != null ||
            selectedStatus != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text(
                text = "Explore Opportunities",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                ),
                color = CharcoalText
            )
            Text(
                text = "Search across grants, scholarships and funding initiatives in Ghana",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalSubtle
            )
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { opportunityViewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search by keywords, organization, category...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ForestGreenPrimary
                )
            },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { opportunityViewModel.onSearchQueryChanged("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("explore_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = ForestGreenPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ALL_OPPORTUNITY_CATEGORIES.forEach { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { opportunityViewModel.onCategorySelected(category) },
                    label = { Text(category, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Status & Sort Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("open", "closing_soon", "upcoming").forEach { status ->
                val isSelected = selectedStatus == status
                val label = when (status) {
                    "open" -> "Open"
                    "closing_soon" -> "Closing Soon"
                    "upcoming" -> "Upcoming"
                    else -> status
                }
                FilterChip(
                    selected = isSelected,
                    onClick = { opportunityViewModel.onStatusSelected(status) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Sort toggle
            FilterChip(
                selected = sortOrder == SortOrder.CLOSING_SOONEST,
                onClick = {
                    val nextSort = if (sortOrder == SortOrder.NEWEST_FIRST) SortOrder.CLOSING_SOONEST else SortOrder.NEWEST_FIRST
                    opportunityViewModel.onSortOrderChanged(nextSort)
                },
                label = {
                    Text(
                        if (sortOrder == SortOrder.NEWEST_FIRST) "Sort: Newest" else "Sort: Closing Soonest",
                        fontSize = 12.sp
                    )
                }
            )

            if (hasActiveFilters) {
                TextButton(onClick = { opportunityViewModel.clearAllFilters() }) {
                    Text("Clear All", color = Color.Red, fontSize = 12.sp)
                }
            }
        }

        // Results counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${opportunities.size} opportunities found",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = CharcoalSubtle
                )
            )
        }

        // Opportunity List
        if (opportunities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = CharcoalSubtle,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No matching opportunities",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CharcoalText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try clearing filters or searching for terms like 'agriculture', 'tech', or 'student'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalSubtle,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { opportunityViewModel.clearAllFilters() }) {
                        Text("Reset All Filters", color = ForestGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(opportunities, key = { it.id }) { opp ->
                    OpportunityCard(
                        opportunity = opp,
                        onClick = { onNavigateToDetail(opp.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
