package com.example.smartcareai.navigation

sealed class Screen(val route: String, val title: String) {

    // Group 1: Hospital Discovery
    object WelcomePreferences : Screen("welcome_preferences", "Welcome & Preferences")
    object SearchHospitals : Screen("search_hospitals", "Search Hospitals")
    object HospitalList : Screen("hospital_list", "Hospital Directory")
    object HospitalMapView : Screen("hospital_map_view", "Hospital Map")
    object FilterSort : Screen("filter_sort", "Filter & Sort")
    object HospitalDetails : Screen("hospital_details/{hospitalId}", "Hospital Details") {
        fun createRoute(hospitalId: String) = "hospital_details/$hospitalId"
    }
    object HospitalComparison : Screen("hospital_comparison", "Compare Hospitals")

    // Group 2: Doctors & Departments
    object DepartmentSelection : Screen("department_selection", "Select Department")
    object DoctorAvailability : Screen("doctor_availability", "Doctor Availability")
    object DoctorProfile : Screen("doctor_profile/{doctorId}", "Doctor Profile") {
        fun createRoute(doctorId: String) = "doctor_profile/$doctorId"
    }
    object RecommendedHospital : Screen("recommended_hospital", "AI Recommended Hospital")
    object BestTimeToVisit : Screen("best_time_to_visit", "Best Time to Visit")

    // Group 3: Crowd Intelligence
    object LiveCrowdMonitoring : Screen("live_crowd_monitoring", "Live Crowd Monitoring")
    object WaitingTimePrediction : Screen("waiting_time_prediction", "Wait-Time Prediction")
    object CrowdTrendChart : Screen("crowd_trend_chart", "Crowd Trends")
    object HospitalDataSources : Screen("hospital_data_sources", "Hospital Data Sources")

    // Group 4: Appointment Booking
    object ChooseAppointmentType : Screen("choose_appointment_type", "Choose Visit Type")
    object VisitPreferences : Screen("visit_preferences", "Visit Preferences")
    object AppointmentDetails : Screen("appointment_details", "Appointment Details")
    object BookingReview : Screen("booking_review", "Booking Review")
    object BookingConfirmation : Screen("booking_confirmation/{bookingId}", "Booking Confirmed") {
        fun createRoute(bookingId: String) = "booking_confirmation/$bookingId"
    }
    object BookingHistory : Screen("booking_history", "Booking History")
    object CancelReschedule : Screen("cancel_reschedule/{bookingId}", "Cancel / Reschedule") {
        fun createRoute(bookingId: String) = "cancel_reschedule/$bookingId"
    }

    // Group 5: Queue Management
    object QueueTokenAssignment : Screen("queue_token_assignment", "Queue Token Assigned")
    object LiveQueueTracking : Screen("live_queue_tracking", "Live Queue Status")
    object WaitingTimeUpdates : Screen("waiting_time_updates", "Wait-Time Log")
    object QueueNotifications : Screen("queue_notifications", "Queue Alerts")

    // Group 6: Patient Assistance & Settings
    object AccessibilityAssistance : Screen("accessibility_assistance", "Accessibility Options")
    object EmergencyGuide : Screen("emergency_guide", "Emergency & ER Triage")
    object PatientProfile : Screen("patient_profile", "Patient Profile")
    object SettingsPrivacy : Screen("settings_privacy", "Settings & Privacy")
    object HelpCenter : Screen("help_center", "Help Center")
    object Faqs : Screen("faqs", "Frequently Asked Questions")
    object ContactSupport : Screen("contact_support", "Contact Support")
    object NotificationsCenter : Screen("notifications_center", "Notifications Center")

    companion object {
        fun getScreenTitle(route: String): String {
            return when (route) {
                WelcomePreferences.route -> WelcomePreferences.title
                SearchHospitals.route -> SearchHospitals.title
                HospitalList.route -> HospitalList.title
                HospitalMapView.route -> HospitalMapView.title
                FilterSort.route -> FilterSort.title
                HospitalComparison.route -> HospitalComparison.title
                DepartmentSelection.route -> DepartmentSelection.title
                DoctorAvailability.route -> DoctorAvailability.title
                RecommendedHospital.route -> RecommendedHospital.title
                BestTimeToVisit.route -> BestTimeToVisit.title
                LiveCrowdMonitoring.route -> LiveCrowdMonitoring.title
                WaitingTimePrediction.route -> WaitingTimePrediction.title
                CrowdTrendChart.route -> CrowdTrendChart.title
                HospitalDataSources.route -> HospitalDataSources.title
                ChooseAppointmentType.route -> ChooseAppointmentType.title
                VisitPreferences.route -> VisitPreferences.title
                AppointmentDetails.route -> AppointmentDetails.title
                BookingReview.route -> BookingReview.title
                BookingHistory.route -> BookingHistory.title
                QueueTokenAssignment.route -> QueueTokenAssignment.title
                LiveQueueTracking.route -> LiveQueueTracking.title
                WaitingTimeUpdates.route -> WaitingTimeUpdates.title
                QueueNotifications.route -> QueueNotifications.title
                AccessibilityAssistance.route -> AccessibilityAssistance.title
                EmergencyGuide.route -> EmergencyGuide.title
                PatientProfile.route -> PatientProfile.title
                SettingsPrivacy.route -> SettingsPrivacy.title
                HelpCenter.route -> HelpCenter.title
                Faqs.route -> Faqs.title
                ContactSupport.route -> ContactSupport.title
                NotificationsCenter.route -> NotificationsCenter.title
                else -> "SmartCareAI"
            }
        }
    }
}
