package com.example.smartcareai.data.models

import java.util.UUID

enum class CrowdLevel(val label: String, val badgeColorHex: Long) {
    LOW("Low Wait", 0xFF10B981),        // Green
    MODERATE("Moderate", 0xFFF59E0B),   // Amber
    HIGH("High Crowd", 0xFFEF4444),     // Red
    CRITICAL("Severe Wait", 0xFF991B1B)  // Dark Red
}

enum class AppointmentType(val displayName: String, val description: String) {
    IN_PERSON("In-Person Visit", "Physical consultation at the hospital clinic"),
    TELEHEALTH("Teleconsultation", "Video consultation with specialist from home"),
    EMERGENCY_TRIAGE("Emergency Triage", "Priority registration for urgent care"),
    FOLLOW_UP("Follow-Up Visit", "Routine review for existing patients")
}

enum class AppointmentStatus {
    UPCOMING, COMPLETED, CANCELLED
}

data class Hospital(
    val id: String,
    val name: String,
    val category: String, // e.g. "General Hospital", "Specialist Center"
    val rating: Double,
    val reviewCount: Int,
    val distanceKm: Double,
    val address: String,
    val area: String = "Medical District",
    val city: String = "Chennai",
    val state: String = "Tamil Nadu",
    val postalCode: String = "",
    val facilities: List<String>,
    val departments: List<String>,
    val imageUrl: String,
    val bestTimeToVisit: String,
    val crowdLevel: CrowdLevel,
    val averageWaitMinutes: Int,
    val liveBedAvailability: Int,
    val emergencyContact: String,
    val latitude: Double = 12.971598,
    val longitude: Double = 77.594562,
    val isRecommended: Boolean = false,
    val recommendationReason: String = ""
)

data class Doctor(
    val id: String,
    val name: String,
    val specialization: String,
    val departmentId: String,
    val departmentName: String,
    val experienceYears: Int,
    val rating: Double,
    val reviewCount: Int,
    val hospitalId: String,
    val hospitalName: String,
    val photoUrl: String,
    val consultationFee: Double,
    val availableSlots: List<String>,
    val bio: String,
    val languages: List<String>
)

data class Department(
    val id: String,
    val name: String,
    val description: String,
    val doctorCount: Int,
    val iconName: String
)

data class Appointment(
    val id: String,
    val appointmentType: AppointmentType,
    val hospitalId: String,
    val hospitalName: String,
    val doctorId: String,
    val doctorName: String,
    val doctorSpecialization: String,
    val departmentName: String,
    val date: String,
    val timeSlot: String,
    val patientName: String,
    val patientPhone: String,
    val accessibilityNeeds: String,
    val status: AppointmentStatus = AppointmentStatus.UPCOMING,
    val referenceNumber: String,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val tokenNumber: String? = null,
    val consultationDetails: String? = null
)

data class QueueToken(
    val id: String,
    val tokenNumber: String, // e.g., "A-104"
    val appointmentId: String,
    val patientName: String,
    val departmentName: String,
    val doctorName: String,
    val hospitalName: String,
    val currentServingToken: String, // e.g. "A-101"
    val estimatedWaitMinutes: Int,
    val positionInQueue: Int,
    val status: String = "Waiting", // "Waiting", "In Progress", "Called", "Completed"
    val lastUpdatedTime: String = "Just now"
)

data class NotificationItemData(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val category: String, // "Appointment", "Queue", "Hospital", "Emergency", "System"
    val isRead: Boolean = false,
    val targetRoute: String = ""
)

data class HourTrend(
    val timeLabel: String, // e.g. "8 AM", "10 AM"
    val crowdPercentage: Int, // 0 - 100
    val isOptimalWindow: Boolean = false
)

data class CrowdData(
    val hospitalId: String,
    val hospitalName: String,
    val crowdLevel: CrowdLevel,
    val peopleWaiting: Int,
    val estimatedWaitRange: String,
    val hourlyTrends: List<HourTrend>,
    val lastUpdated: String
)

data class PatientProfile(
    val name: String = "Sarah Jenkins",
    val age: Int = 34,
    val gender: String = "Female",
    val bloodType: String = "O+",
    val phone: String = "+1 (555) 234-5678",
    val email: String = "sarah.jenkins@example.com",
    val emergencyContact: String = "+1 (555) 911-0022",
    val preferredVisitTime: String = "Morning (9 AM - 12 PM)",
    val preferredAppointmentType: AppointmentType = AppointmentType.IN_PERSON,
    val mobilityAssistanceRequired: Boolean = false,
    val largeTextEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true
)

data class FAQ(
    val id: String,
    val question: String,
    val answer: String,
    val category: String,
    val isExpanded: Boolean = false
)

data class SupportTicket(
    val id: String,
    val name: String,
    val email: String,
    val category: String,
    val subject: String,
    val message: String,
    val timestamp: String,
    val status: String = "Submitted"
)

data class AiChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user" or "gemini_ai"
    val text: String,
    val photoUri: String? = null,
    val videoUri: String? = null,
    val urgencyLevel: String? = null, // "LOW", "MODERATE", "HIGH", "EMERGENCY"
    val suggestedSpecialization: String? = null,
    val suggestedDoctors: List<Doctor> = emptyList(),
    val timestamp: String = "Just now"
)
