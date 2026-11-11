package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.AiAssistantRepository
import com.example.data.repository.ApplicationTrackerRepository
import com.example.data.repository.NotificationRepository
import com.example.data.repository.OpportunityRepository
import com.example.data.repository.SavedOpportunityRepository
import com.example.data.repository.UserRepository
import com.example.ui.screens.ai.AiAssistantScreen
import com.example.ui.screens.alerts.NotificationsScreen
import com.example.ui.screens.auth.SignInScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.detail.OpportunityDetailScreen
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.premium.PremiumUpgradeScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.ProfileSetupScreen
import com.example.ui.screens.saved.SavedOpportunitiesScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.tracker.ApplicationTrackerScreen
import com.example.ui.theme.CharcoalSubtle
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.GhanaGoldAccent
import com.example.viewmodel.AiAssistantViewModel
import com.example.viewmodel.AuthState
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.NotificationViewModel
import com.example.viewmodel.OpportunityDetailViewModel
import com.example.viewmodel.OpportunityViewModel
import com.example.viewmodel.ProfileUiState
import com.example.viewmodel.ProfileViewModel
import com.example.viewmodel.SavedViewModel
import com.example.viewmodel.TrackerViewModel

enum class MainTab(val label: String) {
    HOME("Home"),
    EXPLORE("Explore"),
    SAVED("Saved"),
    ALERTS("Alerts"),
    PROFILE("Profile")
}

sealed interface Screen {
    data object Splash : Screen
    data object Onboarding : Screen
    data object SignIn : Screen
    data object ProfileSetup : Screen
    data object Main : Screen
    data class OpportunityDetail(val opportunityId: String) : Screen
    data object AiAssistant : Screen
    data object ApplicationTracker : Screen
    data object PremiumUpgrade : Screen
    data object NotificationsCenter : Screen
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current

    // Repositories
    val userRepository = remember { UserRepository(context) }
    val opportunityRepository = remember { OpportunityRepository(context) }
    val savedOpportunityRepository = remember { SavedOpportunityRepository(context) }
    val applicationTrackerRepository = remember { ApplicationTrackerRepository(context) }
    val notificationRepository = remember { NotificationRepository(context) }
    val aiAssistantRepository = remember { AiAssistantRepository() }

    // Shared ViewModels
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val currentUserId = (authState as? AuthState.Authenticated)?.user?.uid ?: "guest"

    val opportunityViewModel = remember { OpportunityViewModel(opportunityRepository) }
    val savedViewModel = remember(currentUserId) { SavedViewModel(savedOpportunityRepository, currentUserId) }
    val trackerViewModel = remember(currentUserId) { TrackerViewModel(applicationTrackerRepository, currentUserId) }
    val notificationViewModel = remember(currentUserId) { NotificationViewModel(notificationRepository, currentUserId) }
    val aiAssistantViewModel = remember { AiAssistantViewModel(aiAssistantRepository) }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    var selectedOpportunityIdForDetail by remember { mutableStateOf<String?>(null) }
    var isEditingProfileFromSettings by remember { mutableStateOf(false) }

    // Profile ViewModel for current authenticated user
    val profileViewModel = remember(currentUserId) {
        ProfileViewModel(userRepository, currentUserId)
    }
    val profileState by profileViewModel.profileState.collectAsState()
    val userProfile = (profileState as? ProfileUiState.Success)?.profile

    // Attempt auto sign-in on start
    LaunchedEffect(Unit) {
        authViewModel.attemptAutoSignIn(context)
    }

    when (val screen = currentScreen) {
        is Screen.Splash -> {
            SplashScreen(
                onSplashFinished = {
                    currentScreen = when (authState) {
                        is AuthState.Authenticated -> Screen.Main
                        is AuthState.Guest -> Screen.Main
                        else -> Screen.Onboarding
                    }
                }
            )
        }

        is Screen.Onboarding -> {
            OnboardingScreen(
                onGetStarted = { currentScreen = Screen.SignIn },
                onSignIn = { currentScreen = Screen.SignIn },
                onExploreAsGuest = {
                    authViewModel.continueAsGuest()
                    currentScreen = Screen.Main
                }
            )
        }

        is Screen.SignIn -> {
            BackHandler { currentScreen = Screen.Onboarding }
            SignInScreen(
                authViewModel = authViewModel,
                onContinueAsGuest = { currentScreen = Screen.Main }
            )

            // When user authenticates, check if they have a profile
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    val uid = (authState as AuthState.Authenticated).user.uid
                    val existing = userRepository.getProfile(uid)
                    if (existing == null) {
                        currentScreen = Screen.ProfileSetup
                    } else {
                        currentScreen = Screen.Main
                    }
                }
            }
        }

        is Screen.ProfileSetup -> {
            BackHandler {
                if (isEditingProfileFromSettings) {
                    isEditingProfileFromSettings = false
                    currentScreen = Screen.Main
                }
            }
            ProfileSetupScreen(
                profileViewModel = profileViewModel,
                existingProfile = userProfile,
                onCompleted = {
                    isEditingProfileFromSettings = false
                    currentScreen = Screen.Main
                }
            )
        }

        is Screen.OpportunityDetail -> {
            BackHandler { currentScreen = Screen.Main }
            val detailViewModel = remember(screen.opportunityId) {
                OpportunityDetailViewModel(
                    opportunityRepository = opportunityRepository,
                    savedRepository = savedOpportunityRepository,
                    trackerRepository = applicationTrackerRepository,
                    aiRepository = aiAssistantRepository,
                    opportunityId = screen.opportunityId
                )
            }
            OpportunityDetailScreen(
                viewModel = detailViewModel,
                userProfile = userProfile,
                onNavigateBack = { currentScreen = Screen.Main }
            )
        }

        is Screen.AiAssistant -> {
            BackHandler { currentScreen = Screen.Main }
            AiAssistantScreen(aiViewModel = aiAssistantViewModel)
        }

        is Screen.ApplicationTracker -> {
            BackHandler { currentScreen = Screen.Main }
            ApplicationTrackerScreen(
                trackerViewModel = trackerViewModel,
                onNavigateToDetail = { oppId ->
                    currentScreen = Screen.OpportunityDetail(oppId)
                }
            )
        }

        is Screen.PremiumUpgrade -> {
            BackHandler { currentScreen = Screen.Main }
            PremiumUpgradeScreen(
                profileViewModel = profileViewModel,
                userProfile = userProfile,
                onNavigateBack = { currentScreen = Screen.Main }
            )
        }

        is Screen.NotificationsCenter -> {
            BackHandler { currentScreen = Screen.Main }
            NotificationsScreen(
                notificationViewModel = notificationViewModel,
                onNavigateBack = { currentScreen = Screen.Main },
                onNavigateToOpportunity = { oppId ->
                    currentScreen = Screen.OpportunityDetail(oppId)
                }
            )
        }

        is Screen.Main -> {
            // Main Scaffold with Bottom Navigation
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("bottom_nav_bar")
                    ) {
                        // 1. Home
                        NavigationBarItem(
                            selected = currentTab == MainTab.HOME,
                            onClick = { currentTab = MainTab.HOME },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                selectedTextColor = ForestGreenPrimary,
                                indicatorColor = ForestGreenPrimary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_home")
                        )

                        // 2. Explore
                        NavigationBarItem(
                            selected = currentTab == MainTab.EXPLORE,
                            onClick = { currentTab = MainTab.EXPLORE },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                                    contentDescription = "Explore"
                                )
                            },
                            label = { Text("Explore", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                selectedTextColor = ForestGreenPrimary,
                                indicatorColor = ForestGreenPrimary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_explore")
                        )

                        // 3. Saved
                        NavigationBarItem(
                            selected = currentTab == MainTab.SAVED,
                            onClick = { currentTab = MainTab.SAVED },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.SAVED) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Saved"
                                )
                            },
                            label = { Text("Saved", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                selectedTextColor = ForestGreenPrimary,
                                indicatorColor = ForestGreenPrimary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_saved")
                        )

                        // 4. Alerts
                        NavigationBarItem(
                            selected = currentTab == MainTab.ALERTS,
                            onClick = { currentTab = MainTab.ALERTS },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.ALERTS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                    contentDescription = "Alerts"
                                )
                            },
                            label = { Text("Alerts", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                selectedTextColor = ForestGreenPrimary,
                                indicatorColor = ForestGreenPrimary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_alerts")
                        )

                        // 5. Profile
                        NavigationBarItem(
                            selected = currentTab == MainTab.PROFILE,
                            onClick = { currentTab = MainTab.PROFILE },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                selectedTextColor = ForestGreenPrimary,
                                indicatorColor = ForestGreenPrimary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_profile")
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                ) {
                    when (currentTab) {
                        MainTab.HOME -> {
                            DashboardScreen(
                                userProfile = userProfile,
                                opportunityViewModel = opportunityViewModel,
                                onNavigateToExplore = { category ->
                                    if (category != null) {
                                        opportunityViewModel.onCategorySelected(category)
                                    }
                                    currentTab = MainTab.EXPLORE
                                },
                                onNavigateToDetail = { oppId ->
                                    currentScreen = Screen.OpportunityDetail(oppId)
                                },
                                onNavigateToNotifications = {
                                    currentScreen = Screen.NotificationsCenter
                                },
                                onNavigateToPremium = {
                                    currentScreen = Screen.PremiumUpgrade
                                },
                                onNavigateToAiAssistant = {
                                    currentScreen = Screen.AiAssistant
                                }
                            )
                        }

                        MainTab.EXPLORE -> {
                            ExploreScreen(
                                opportunityViewModel = opportunityViewModel,
                                onNavigateToDetail = { oppId ->
                                    currentScreen = Screen.OpportunityDetail(oppId)
                                }
                            )
                        }

                        MainTab.SAVED -> {
                            SavedOpportunitiesScreen(
                                savedViewModel = savedViewModel,
                                userProfile = userProfile,
                                onNavigateToDetail = { oppId ->
                                    currentScreen = Screen.OpportunityDetail(oppId)
                                },
                                onNavigateToExplore = {
                                    currentTab = MainTab.EXPLORE
                                },
                                onNavigateToPremium = {
                                    currentScreen = Screen.PremiumUpgrade
                                }
                            )
                        }

                        MainTab.ALERTS -> {
                            NotificationsScreen(
                                notificationViewModel = notificationViewModel,
                                onNavigateBack = { currentTab = MainTab.HOME },
                                onNavigateToOpportunity = { oppId ->
                                    currentScreen = Screen.OpportunityDetail(oppId)
                                }
                            )
                        }

                        MainTab.PROFILE -> {
                            ProfileScreen(
                                userProfile = userProfile,
                                authViewModel = authViewModel,
                                onEditProfile = {
                                    isEditingProfileFromSettings = true
                                    currentScreen = Screen.ProfileSetup
                                },
                                onNavigateToPremium = {
                                    currentScreen = Screen.PremiumUpgrade
                                },
                                onNavigateToNotifications = {
                                    currentScreen = Screen.NotificationsCenter
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
