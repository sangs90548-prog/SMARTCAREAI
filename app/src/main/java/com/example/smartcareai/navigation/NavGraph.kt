package com.example.smartcareai.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.smartcareai.ui.components.SmartCareTopBar
import com.example.smartcareai.ui.screens.assistance.*
import com.example.smartcareai.ui.screens.booking.*
import com.example.smartcareai.ui.screens.crowd.*
import com.example.smartcareai.ui.screens.discovery.*
import com.example.smartcareai.ui.screens.doctors.*
import com.example.smartcareai.ui.screens.queue.*
import com.example.smartcareai.ui.theme.PrimaryBlue
import com.example.smartcareai.ui.theme.TextMuted
import com.example.smartcareai.viewmodel.SmartCareViewModel

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Discovery", Screen.SearchHospitals.route, Icons.Default.Search),
    BottomNavItem("Doctors", Screen.DepartmentSelection.route, Icons.Default.MedicalServices),
    BottomNavItem("Queue", Screen.LiveQueueTracking.route, Icons.Default.ConfirmationNumber),
    BottomNavItem("Care", Screen.EmergencyGuide.route, Icons.Default.HealthAndSafety),
    BottomNavItem("Profile", Screen.PatientProfile.route, Icons.Default.Person)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartCareAppNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: SmartCareViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.WelcomePreferences.route

    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifs = notifications.count { !it.isRead }

    val showBottomBar = bottomNavItems.any { it.route == currentRoute } ||
            currentRoute == Screen.HospitalList.route ||
            currentRoute == Screen.BookingHistory.route ||
            currentRoute == Screen.SettingsPrivacy.route

    Scaffold(
        topBar = {
            SmartCareTopBar(
                title = when {
                    currentRoute.startsWith("hospital_details") -> "Hospital Details"
                    currentRoute.startsWith("doctor_profile") -> "Doctor Profile"
                    currentRoute.startsWith("booking_confirmation") -> "Booking Confirmation"
                    currentRoute.startsWith("cancel_reschedule") -> "Cancel / Reschedule"
                    else -> Screen.getScreenTitle(currentRoute)
                },
                showBackButton = !showBottomBar && currentRoute != Screen.WelcomePreferences.route,
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.NotificationsCenter.route) },
                unreadNotificationCount = unreadNotifs,
                onEmergencyClick = { navController.navigate(Screen.EmergencyGuide.route) }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.WelcomePreferences.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Group 1: Discovery (1 - 7)
            composable(Screen.WelcomePreferences.route) {
                WelcomePreferencesScreen(
                    viewModel = viewModel,
                    onNavigateNext = { navController.navigate(Screen.SearchHospitals.route) }
                )
            }
            composable(Screen.SearchHospitals.route) {
                SearchHospitalsScreen(
                    viewModel = viewModel,
                    onHospitalClick = { id -> navController.navigate(Screen.HospitalDetails.createRoute(id)) },
                    onBookClick = { navController.navigate(Screen.ChooseAppointmentType.route) },
                    onFilterClick = { navController.navigate(Screen.FilterSort.route) }
                )
            }
            composable(Screen.HospitalList.route) {
                HospitalListScreen(
                    viewModel = viewModel,
                    onHospitalClick = { id -> navController.navigate(Screen.HospitalDetails.createRoute(id)) },
                    onBookClick = { navController.navigate(Screen.ChooseAppointmentType.route) },
                    onMapViewClick = { navController.navigate(Screen.HospitalMapView.route) }
                )
            }
            composable(Screen.HospitalMapView.route) {
                HospitalMapViewScreen(
                    viewModel = viewModel,
                    onHospitalClick = { id -> navController.navigate(Screen.HospitalDetails.createRoute(id)) }
                )
            }
            composable(Screen.FilterSort.route) {
                FilterSortScreen(
                    viewModel = viewModel,
                    onApply = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.HospitalDetails.route,
                arguments = listOf(navArgument("hospitalId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("hospitalId") ?: "h1"
                HospitalDetailsScreen(
                    hospitalId = id,
                    viewModel = viewModel,
                    onBookClick = { navController.navigate(Screen.ChooseAppointmentType.route) },
                    onDepartmentClick = { navController.navigate(Screen.DepartmentSelection.route) },
                    onBestTimeClick = { navController.navigate(Screen.BestTimeToVisit.route) },
                    onLiveCrowdClick = { navController.navigate(Screen.LiveCrowdMonitoring.route) }
                )
            }
            composable(Screen.HospitalComparison.route) {
                HospitalComparisonScreen(
                    viewModel = viewModel,
                    onBookClick = { navController.navigate(Screen.ChooseAppointmentType.route) }
                )
            }

            // Group 2: Doctors & Departments (8 - 12)
            composable(Screen.DepartmentSelection.route) {
                DepartmentSelectionScreen(
                    viewModel = viewModel,
                    onDepartmentSelected = { navController.navigate(Screen.DoctorAvailability.route) }
                )
            }
            composable(Screen.DoctorAvailability.route) {
                DoctorAvailabilityScreen(
                    viewModel = viewModel,
                    onDoctorClick = { id -> navController.navigate(Screen.DoctorProfile.createRoute(id)) },
                    onBookClick = { navController.navigate(Screen.ChooseAppointmentType.route) }
                )
            }
            composable(
                route = Screen.DoctorProfile.route,
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStack ->
                val docId = backStack.arguments?.getString("doctorId") ?: "doc1"
                DoctorProfileScreen(
                    doctorId = docId,
                    viewModel = viewModel,
                    onBookClick = { navController.navigate(Screen.ChooseAppointmentType.route) }
                )
            }
            composable(Screen.RecommendedHospital.route) {
                RecommendedHospitalScreen(
                    viewModel = viewModel,
                    onBookClick = { navController.navigate(Screen.ChooseAppointmentType.route) }
                )
            }
            composable(Screen.BestTimeToVisit.route) {
                BestTimeToVisitScreen(viewModel = viewModel)
            }

            // Group 3: Crowd Intelligence (13 - 16)
            composable(Screen.LiveCrowdMonitoring.route) {
                LiveCrowdMonitoringScreen(viewModel = viewModel)
            }
            composable(Screen.WaitingTimePrediction.route) {
                WaitingTimePredictionScreen(viewModel = viewModel)
            }
            composable(Screen.CrowdTrendChart.route) {
                CrowdTrendChartScreen(viewModel = viewModel)
            }
            composable(Screen.HospitalDataSources.route) {
                HospitalDataSourcesScreen(viewModel = viewModel)
            }

            // Group 4: Appointment Booking (17 - 23)
            composable(Screen.ChooseAppointmentType.route) {
                ChooseAppointmentTypeScreen(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Screen.VisitPreferences.route) }
                )
            }
            composable(Screen.VisitPreferences.route) {
                VisitPreferencesScreen(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Screen.AppointmentDetails.route) }
                )
            }
            composable(Screen.AppointmentDetails.route) {
                AppointmentDetailsScreen(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Screen.BookingReview.route) }
                )
            }
            composable(Screen.BookingReview.route) {
                BookingReviewScreen(
                    viewModel = viewModel,
                    onConfirmClick = { bookingId ->
                        navController.navigate(Screen.BookingConfirmation.createRoute(bookingId)) {
                            popUpTo(Screen.SearchHospitals.route)
                        }
                    }
                )
            }
            composable(
                route = Screen.BookingConfirmation.route,
                arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
            ) { backStack ->
                val bId = backStack.arguments?.getString("bookingId") ?: "app_101"
                BookingConfirmationScreen(
                    bookingId = bId,
                    viewModel = viewModel,
                    onTrackQueueClick = { navController.navigate(Screen.LiveQueueTracking.route) },
                    onDoneClick = { navController.navigate(Screen.SearchHospitals.route) }
                )
            }
            composable(Screen.BookingHistory.route) {
                BookingHistoryScreen(
                    viewModel = viewModel,
                    onAppointmentClick = { id -> navController.navigate(Screen.CancelReschedule.createRoute(id)) }
                )
            }
            composable(
                route = Screen.CancelReschedule.route,
                arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
            ) { backStack ->
                val bId = backStack.arguments?.getString("bookingId") ?: "app_101"
                CancelRescheduleScreen(
                    bookingId = bId,
                    viewModel = viewModel,
                    onDoneClick = { navController.popBackStack() }
                )
            }

            // Group 5: Queue Management (24 - 27)
            composable(Screen.QueueTokenAssignment.route) {
                QueueTokenAssignmentScreen(
                    viewModel = viewModel,
                    onTrackClick = { navController.navigate(Screen.LiveQueueTracking.route) }
                )
            }
            composable(Screen.LiveQueueTracking.route) {
                LiveQueueTrackingScreen(
                    viewModel = viewModel,
                    onViewLogClick = { navController.navigate(Screen.WaitingTimeUpdates.route) }
                )
            }
            composable(Screen.WaitingTimeUpdates.route) {
                WaitingTimeUpdatesScreen(viewModel = viewModel)
            }
            composable(Screen.QueueNotifications.route) {
                QueueNotificationsScreen(viewModel = viewModel)
            }

            // Group 6: Patient Assistance & Settings (28 - 35)
            composable(Screen.AccessibilityAssistance.route) {
                AccessibilityAssistanceScreen(viewModel = viewModel)
            }
            composable(Screen.EmergencyGuide.route) {
                EmergencyGuideScreen(viewModel = viewModel)
            }
            composable(Screen.PatientProfile.route) {
                PatientProfileScreen(viewModel = viewModel)
            }
            composable(Screen.SettingsPrivacy.route) {
                SettingsPrivacyScreen(
                    viewModel = viewModel,
                    onNavigateToAccessibility = { navController.navigate(Screen.AccessibilityAssistance.route) }
                )
            }
            composable(Screen.HelpCenter.route) {
                HelpCenterScreen(
                    onNavigateToFaqs = { navController.navigate(Screen.Faqs.route) },
                    onNavigateToSupport = { navController.navigate(Screen.ContactSupport.route) }
                )
            }
            composable(Screen.Faqs.route) {
                FaqsScreen(viewModel = viewModel)
            }
            composable(Screen.ContactSupport.route) {
                ContactSupportScreen(
                    viewModel = viewModel,
                    onSubmitted = { navController.popBackStack() }
                )
            }
            composable(Screen.NotificationsCenter.route) {
                NotificationsCenterScreen(
                    viewModel = viewModel,
                    onNotificationTargetClick = { target -> navController.navigate(target) }
                )
            }
        }
    }
}
