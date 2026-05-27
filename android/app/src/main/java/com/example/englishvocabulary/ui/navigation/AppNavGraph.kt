package com.example.englishvocabulary.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.englishvocabulary.App
import com.example.englishvocabulary.domain.model.User
import com.example.englishvocabulary.domain.model.Word
import com.example.englishvocabulary.ui.auth.AuthViewModel
import com.example.englishvocabulary.ui.auth.LoginScreen
import com.example.englishvocabulary.ui.auth.RegisterScreen
import com.example.englishvocabulary.ui.home.HomeScreen
import com.example.englishvocabulary.ui.onboarding.OnboardingScreen
import com.example.englishvocabulary.ui.onboarding.OnboardingViewModel
import com.example.englishvocabulary.ui.previous.PreviousWordsScreen
import com.example.englishvocabulary.ui.profile.ProfileScreen
import com.example.englishvocabulary.ui.review.ReviewScreen
import com.example.englishvocabulary.ui.review.ReviewViewModel
import com.example.englishvocabulary.ui.word.*

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current.applicationContext as App
    val appModule = context.appModule

    // Instantiate ViewModels with Factories linking directly to the AppModule dependencies
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(
            appModule.loginUseCase,
            appModule.registerUseCase,
            appModule.authRepository
        )
    )

    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.Factory(
            appModule.userRepository
        )
    )

    val wordViewModel: WordViewModel = viewModel(
        factory = WordViewModel.Factory(
            appModule.getDailyWordUseCase,
            appModule.addCustomWordUseCase,
            appModule.saveWordUseCase,
            appModule.getPreviousWordsUseCase
        )
    )

    val reviewViewModel: ReviewViewModel = viewModel(
        factory = ReviewViewModel.Factory(
            appModule.getDueReviewsUseCase,
            appModule.submitReviewAnswerUseCase
        )
    )

    // Global in-memory user reference to configure session profiles
    var activeUser by remember { mutableStateOf<User?>(null) }
    
    // Support detailed word lookups inside the graph without intense bundle serialization
    var selectedDetailWord by remember { mutableStateOf<Word?>(null) }

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // Dynamically route startup destinations based on session existence and onboarding parameters
    val startDestination = if (isLoggedIn) {
        if (activeUser == null) {
            Routes.ONBOARDING
        } else {
            Routes.HOME
        }
    } else {
        Routes.LOGIN
    }

    // Capture auth changes during app session
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            // Retrieve current profile on successful authentication
            appModule.userRepository.getMyProfile().collect { resource ->
                if (resource is com.example.englishvocabulary.core.Resource.Success) {
                    activeUser = resource.data
                    
                    // If interests are already set, skip onboarding and jump straight into dashboard
                    if (resource.data.interests.isNotEmpty()) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    // Profile retrieval is handled by our global listener
                },
                onConfigureUrl = {
                    // Let unauthorized user configure base URLs easily
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onRegisterSuccess = {
                    // Handled by global listener
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                userDisplayName = activeUser?.displayName ?: "Scholar",
                onOnboardingComplete = {
                    // Profile got synced, skip straight to application dashboard
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val user = activeUser
            HomeScreen(
                userName = user?.displayName ?: "Scholar",
                userLevel = user?.cefrLevel ?: "B1",
                onNavigateToDailyWord = { navController.navigate(Routes.DAILY_WORD) },
                onNavigateToReview = { navController.navigate(Routes.REVIEW) },
                onNavigateToPreviousWords = { navController.navigate(Routes.PREVIOUS_WORDS) },
                onNavigateToAddCustom = { navController.navigate(Routes.ADD_WORD) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onLogout = { authViewModel.logout() }
            )
        }

        composable(Routes.DAILY_WORD) {
            DailyWordScreen(
                viewModel = wordViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADD_WORD) {
            AddWordScreen(
                viewModel = wordViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PREVIOUS_WORDS) {
            PreviousWordsScreen(
                viewModel = wordViewModel,
                onNavigateToDetail = { word ->
                    selectedDetailWord = word
                    navController.navigate("word_detail")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("word_detail") {
            val target = selectedDetailWord
            if (target != null) {
                // Check local saved status inside words list stream
                val list by wordViewModel.cachedWords.collectAsState()
                val isSaved = list.find { it.id == target.id }?.isSaved ?: target.isSaved

                WordDetailScreen(
                    wordItem = target,
                    isSaved = isSaved,
                    onToggleSave = { wordViewModel.toggleSave(target.id, isSaved) },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.REVIEW) {
            ReviewScreen(
                viewModel = reviewViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            val fakeInitialUser = User(0, "", "Visitor", "B1", emptyList())
            ProfileScreen(
                userRepository = appModule.userRepository,
                settingsDataStore = appModule.settingsDataStore,
                initialUser = activeUser ?: fakeInitialUser,
                onProfileUpdated = { updated ->
                    activeUser = updated
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
