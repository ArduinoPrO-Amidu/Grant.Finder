package com.example.ui.screens.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.CharcoalSubtle
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.GhanaGoldAccent
import com.example.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumUpgradeScreen(
    profileViewModel: ProfileViewModel,
    userProfile: UserProfile?,
    onNavigateBack: () -> Unit
) {
    val isAlreadyPremium = userProfile?.isPremium == true
    var upgradeSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upgrade to Premium", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ForestGreenPrimary,
                    titleContentColor = Color.White
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Star Badge
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GhanaGoldAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = ForestGreenDark,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Supercharge Your Funding Search",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                ),
                color = CharcoalText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Get AI-assisted grant proposals, unlimited saved bookmarks, and custom regional alerts.",
                style = MaterialTheme.typography.bodyMedium,
                color = CharcoalSubtle,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Pricing Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ForestGreenDark
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GhanaGoldAccent)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PREMIUM TIER",
                            color = ForestGreenDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "GHS 35 / month",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Billed annually or cancel anytime",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Comparison Feature Matrix
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Plan Comparison",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CharcoalText
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    PlanFeatureRow(feature = "Browse Public Directory", free = true, premium = true)
                    PlanFeatureRow(feature = "Search & Filter Grants", free = true, premium = true)
                    PlanFeatureRow(feature = "Official Application Links", free = true, premium = true)
                    PlanFeatureRow(feature = "Saved Grants Storage", freeText = "Up to 5", premiumText = "Unlimited")
                    PlanFeatureRow(feature = "Basic Eligibility Check", free = true, premium = true)
                    PlanFeatureRow(feature = "Advanced AI Eligibility Scoring", free = false, premium = true)
                    PlanFeatureRow(feature = "AI Proposal & Objective Drafting", free = false, premium = true)
                    PlanFeatureRow(feature = "Personalized Regional Matching", free = false, premium = true)
                    PlanFeatureRow(feature = "Grant Budget Advisor", free = false, premium = true)
                    PlanFeatureRow(feature = "Priority Deadline Notifications", free = false, premium = true)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (isAlreadyPremium || upgradeSuccess) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓ You are currently enjoying Premium Access!",
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            } else {
                Button(
                    onClick = {
                        profileViewModel.upgradeToPremium {
                            upgradeSuccess = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("activate_premium_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreenPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GhanaGoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Activate Premium Access",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Demonstration Mode · Prepared for Google Play Billing",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalSubtle,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun PlanFeatureRow(
    feature: String,
    free: Boolean? = null,
    premium: Boolean? = null,
    freeText: String? = null,
    premiumText: String? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = feature,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = CharcoalText
                ),
                modifier = Modifier.weight(1f)
            )

            // Free column
            Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.Center) {
                if (freeText != null) {
                    Text(text = freeText, fontSize = 11.sp, color = CharcoalSubtle)
                } else if (free == true) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
                } else {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }
            }

            // Premium column
            Box(modifier = Modifier.width(70.dp), contentAlignment = Alignment.Center) {
                if (premiumText != null) {
                    Text(text = premiumText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                } else if (premium == true) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
                } else {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }
            }
        }
        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}
