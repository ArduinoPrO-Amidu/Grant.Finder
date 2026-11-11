package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.CharcoalSubtle
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.GhanaGoldAccent
import com.example.viewmodel.AGE_RANGES
import com.example.viewmodel.ALL_OPPORTUNITY_CATEGORIES
import com.example.viewmodel.EDUCATION_LEVELS
import com.example.viewmodel.GHANA_REGIONS
import com.example.viewmodel.ProfileViewModel
import com.example.viewmodel.USER_TYPES

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    profileViewModel: ProfileViewModel,
    existingProfile: UserProfile?,
    onCompleted: () -> Unit
) {
    var fullName by remember { mutableStateOf(existingProfile?.fullName ?: "") }
    var email by remember { mutableStateOf(existingProfile?.email ?: "") }
    var selectedRegion by remember { mutableStateOf(existingProfile?.region ?: "Greater Accra") }
    var district by remember { mutableStateOf(existingProfile?.district ?: "") }
    var selectedAgeRange by remember { mutableStateOf(existingProfile?.ageRange ?: "25–34") }
    var occupation by remember { mutableStateOf(existingProfile?.occupation ?: "") }
    var selectedEducation by remember { mutableStateOf(existingProfile?.educationLevel ?: "Bachelor's Degree") }
    var selectedUserType by remember { mutableStateOf(existingProfile?.userType ?: "Entrepreneur") }

    val selectedInterests = remember {
        mutableStateListOf<String>().apply {
            if (existingProfile?.interests?.isNotEmpty() == true) {
                addAll(existingProfile.interests)
            } else {
                addAll(listOf("Business grants", "Youth empowerment", "Agriculture"))
            }
        }
    }

    var regionExpanded by remember { mutableStateOf(false) }
    var userTypeExpanded by remember { mutableStateOf(false) }

    val isSaving by profileViewModel.isSaving.collectAsState()
    val saveError by profileViewModel.saveError.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingProfile == null) "Setup Your Profile" else "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ForestGreenPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            Text(
                text = "Ghana Grant Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText
                )
            )
            Text(
                text = "Customize your profile to enable tailored grant matching across Ghana's 16 regions.",
                style = MaterialTheme.typography.bodyMedium,
                color = CharcoalSubtle
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (saveError != null) {
                Text(
                    text = saveError ?: "",
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name *") },
                placeholder = { Text("e.g. Kwame Mensah") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_fullname"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Region in Ghana Dropdown
            Text(
                text = "Region in Ghana *",
                style = MaterialTheme.typography.labelLarge,
                color = CharcoalText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { regionExpanded = true }
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedRegion, color = CharcoalText, fontWeight = FontWeight.Medium)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = ForestGreenPrimary)
                }

                DropdownMenu(
                    expanded = regionExpanded,
                    onDismissRequest = { regionExpanded = false }
                ) {
                    GHANA_REGIONS.forEach { region ->
                        DropdownMenuItem(
                            text = { Text(region) },
                            onClick = {
                                selectedRegion = region
                                regionExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // District (Optional)
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District / Municipality (Optional)") },
                placeholder = { Text("e.g. Accra Metropolitan, Kumasi, Tamale") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_district"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // User Type Dropdown
            Text(
                text = "User Type / Applicant Persona *",
                style = MaterialTheme.typography.labelLarge,
                color = CharcoalText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { userTypeExpanded = true }
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedUserType, color = CharcoalText, fontWeight = FontWeight.Medium)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = ForestGreenPrimary)
                }

                DropdownMenu(
                    expanded = userTypeExpanded,
                    onDismissRequest = { userTypeExpanded = false }
                ) {
                    USER_TYPES.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedUserType = type
                                userTypeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Occupation
            OutlinedTextField(
                value = occupation,
                onValueChange = { occupation = it },
                label = { Text("Current Occupation / Business") },
                placeholder = { Text("e.g. Cocoa Farmer, Software Developer, Student") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_occupation"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Age Range
            Text(
                text = "Age Range",
                style = MaterialTheme.typography.labelLarge,
                color = CharcoalText
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AGE_RANGES.forEach { age ->
                    val isSelected = selectedAgeRange == age
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedAgeRange = age },
                        label = { Text(age) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Opportunity Interests
            Text(
                text = "Areas of Opportunity Interest",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText
                )
            )
            Text(
                text = "Select all categories you wish to discover funding for:",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalSubtle
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ALL_OPPORTUNITY_CATEGORIES.forEach { category ->
                    val isSelected = selectedInterests.contains(category)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedInterests.remove(category)
                            } else {
                                selectedInterests.add(category)
                            }
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                            }
                        } else null,
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreenPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save / Submit Button
            Button(
                onClick = {
                    profileViewModel.saveProfile(
                        fullName = fullName,
                        email = email,
                        region = selectedRegion,
                        district = district,
                        ageRange = selectedAgeRange,
                        occupation = occupation,
                        educationLevel = selectedEducation,
                        userType = selectedUserType,
                        interests = selectedInterests.toList(),
                        onSuccess = onCompleted
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreenPrimary,
                    contentColor = Color.White
                ),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Save & Continue to Opportunities",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
