package com.example.smartcareai.data.repository

import com.example.smartcareai.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SmartCareRepository {

    // Initial Mock Hospitals
    private val initialHospitals = listOf(
        Hospital(
            id = "h1",
            name = "MetroCare Central Hospital",
            category = "General Tertiary Hospital",
            rating = 4.8,
            reviewCount = 1240,
            distanceKm = 1.8,
            address = "742 Evergreen Terrace, Medical District",
            facilities = listOf("24/7 Emergency", "ICU", "MRI & CT Scan", "Pharmacy", "Wheelchair Accessible", "Valet Parking"),
            departments = listOf("Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Emergency Care", "General Medicine"),
            imageUrl = "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc",
            bestTimeToVisit = "8:30 AM - 10:30 AM (Low Wait)",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 15,
            liveBedAvailability = 42,
            emergencyContact = "+1 (800) 555-0199",
            isRecommended = true,
            recommendationReason = "Lowest waiting time (15 mins) & shortest distance (1.8 km)"
        ),
        Hospital(
            id = "h2",
            name = "St. Jude Heart & Health Institute",
            category = "Specialist Cardiac & Surgical Center",
            rating = 4.9,
            reviewCount = 890,
            distanceKm = 3.4,
            address = "108 Ocean Boulevard, Downtown",
            facilities = listOf("Cath Lab", "Advanced Cardiac ICU", "24/7 Pharmacy", "Cafeteria", "Subsidized Transport"),
            departments = listOf("Cardiology", "Cardiothoracic Surgery", "Vascular Surgery", "General Medicine"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "1:00 PM - 3:00 PM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 30,
            liveBedAvailability = 18,
            emergencyContact = "+1 (800) 555-0288",
            isRecommended = false
        ),
        Hospital(
            id = "h3",
            name = "Valley Memorial Children & Women's Hospital",
            category = "Pediatric & Maternal Care",
            rating = 4.7,
            reviewCount = 650,
            distanceKm = 5.2,
            address = "455 Pinecrest Avenue, Northside",
            facilities = listOf("NICU & PICU", "Pediatric ER", "Playroom", "Lactation Suites", "24/7 Pharmacy"),
            departments = listOf("Pediatrics", "Obstetrics & Gynecology", "Pediatric Surgery", "Neonatology"),
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3",
            bestTimeToVisit = "10:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.HIGH,
            averageWaitMinutes = 45,
            liveBedAvailability = 9,
            emergencyContact = "+1 (800) 555-0377",
            isRecommended = false
        ),
        Hospital(
            id = "h4",
            name = "CityCare Community Hospital",
            category = "Primary & Urgent Care Facility",
            rating = 4.4,
            reviewCount = 410,
            distanceKm = 6.8,
            address = "1200 West Maple St, Suburban West",
            facilities = listOf("Urgent Care Clinic", "X-Ray Lab", "Outpatient Pharmacy", "Free Parking"),
            departments = listOf("General Medicine", "Dermatology", "ENT", "Orthopedics"),
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09",
            bestTimeToVisit = "7:00 AM - 9:00 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 20,
            liveBedAvailability = 25,
            emergencyContact = "+1 (800) 555-0466",
            isRecommended = false
        )
    )

    // Initial Departments
    val departments = listOf(
        Department("dep_cardio", "Cardiology", "Heart, vascular, and cardiac surgical care", 12, "favorite"),
        Department("dep_neuro", "Neurology", "Brain, spine, and nerve specialist care", 8, "psychology"),
        Department("dep_ortho", "Orthopedics", "Bone, joint, and trauma surgical specialists", 15, "accessible"),
        Department("dep_ped", "Pediatrics", "Comprehensive child and newborn care", 10, "child_care"),
        Department("dep_ent", "ENT (Ear, Nose, Throat)", "Otolaryngology and sinus treatments", 6, "hearing"),
        Department("dep_general", "General Medicine", "Primary consultations, checkups, and diagnostics", 20, "medical_services"),
        Department("dep_derma", "Dermatology", "Skin, hair, and cosmetic medical treatments", 7, "clean_hands"),
        Department("dep_emerg", "Emergency Medicine", "24/7 Trauma, acute care, and triage", 25, "emergency")
    )

    // Initial Doctors
    val doctors = listOf(
        Doctor(
            id = "doc1",
            name = "Dr. Amanda Richardson",
            specialization = "Senior Cardiologist",
            departmentId = "dep_cardio",
            departmentName = "Cardiology",
            experienceYears = 16,
            rating = 4.9,
            reviewCount = 312,
            hospitalId = "h1",
            hospitalName = "MetroCare Central Hospital",
            photoUrl = "https://images.unsplash.com/photo-1559839734-2b71ea197ec2",
            consultationFee = 85.0,
            availableSlots = listOf("09:00 AM", "10:30 AM", "01:30 PM", "03:00 PM"),
            bio = "Dr. Amanda Richardson is a board-certified Senior Cardiologist specializing in preventive cardiac health, hypertension, and non-invasive diagnostics.",
            languages = listOf("English", "Spanish")
        ),
        Doctor(
            id = "doc2",
            name = "Dr. Robert Vance",
            specialization = "Neurologist & Spine Specialist",
            departmentId = "dep_neuro",
            departmentName = "Neurology",
            experienceYears = 14,
            rating = 4.8,
            reviewCount = 245,
            hospitalId = "h1",
            hospitalName = "MetroCare Central Hospital",
            photoUrl = "https://images.unsplash.com/photo-1622253692010-333f2da6031d",
            consultationFee = 95.0,
            availableSlots = listOf("09:30 AM", "11:00 AM", "02:00 PM", "04:15 PM"),
            bio = "Dr. Vance leads the Neuro-spine Research Team at MetroCare Central with over 14 years of clinical experience in headache and neuropathy management.",
            languages = listOf("English", "French")
        ),
        Doctor(
            id = "doc3",
            name = "Dr. Marcus Chen",
            specialization = "Orthopedic Surgeon",
            departmentId = "dep_ortho",
            departmentName = "Orthopedics",
            experienceYears = 12,
            rating = 4.7,
            reviewCount = 198,
            hospitalId = "h2",
            hospitalName = "St. Jude Heart & Health Institute",
            photoUrl = "https://images.unsplash.com/photo-1612349317150-e413f6a5b16d",
            consultationFee = 90.0,
            availableSlots = listOf("10:00 AM", "11:30 AM", "02:30 PM", "05:00 PM"),
            bio = "Specialist in joint reconstruction, sports injury rehabilitation, and arthroscopic procedures.",
            languages = listOf("English", "Mandarin")
        ),
        Doctor(
            id = "doc4",
            name = "Dr. Sophia Patel",
            specialization = "Pediatric Consultant",
            departmentId = "dep_ped",
            departmentName = "Pediatrics",
            experienceYears = 10,
            rating = 4.9,
            reviewCount = 420,
            hospitalId = "h3",
            hospitalName = "Valley Memorial Children & Women's Hospital",
            photoUrl = "https://images.unsplash.com/photo-1594824813566-78a08a28f804",
            consultationFee = 75.0,
            availableSlots = listOf("08:30 AM", "10:00 AM", "01:00 PM", "03:30 PM"),
            bio = "Compassionate pediatric consultant passionate about early childhood development and allergy prevention.",
            languages = listOf("English", "Hindi")
        )
    )

    // StateFlows
    private val _hospitals = MutableStateFlow<List<Hospital>>(initialHospitals)
    val hospitals: StateFlow<List<Hospital>> = _hospitals.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(
        listOf(
            Appointment(
                id = "app_101",
                appointmentType = AppointmentType.IN_PERSON,
                hospitalId = "h1",
                hospitalName = "MetroCare Central Hospital",
                doctorId = "doc1",
                doctorName = "Dr. Amanda Richardson",
                doctorSpecialization = "Senior Cardiologist",
                departmentName = "Cardiology",
                date = "Tomorrow, Oct 24",
                timeSlot = "10:30 AM",
                patientName = "Sarah Jenkins",
                patientPhone = "+1 (555) 234-5678",
                accessibilityNeeds = "Wheelchair Ramp requested",
                status = AppointmentStatus.UPCOMING,
                referenceNumber = "SCA-892401"
            ),
            Appointment(
                id = "app_100",
                appointmentType = AppointmentType.TELEHEALTH,
                hospitalId = "h2",
                hospitalName = "St. Jude Heart & Health Institute",
                doctorId = "doc3",
                doctorName = "Dr. Marcus Chen",
                doctorSpecialization = "Orthopedic Surgeon",
                departmentName = "Orthopedics",
                date = "12 Oct 2024",
                timeSlot = "02:30 PM",
                patientName = "Sarah Jenkins",
                patientPhone = "+1 (555) 234-5678",
                accessibilityNeeds = "None",
                status = AppointmentStatus.COMPLETED,
                referenceNumber = "SCA-773190"
            )
        )
    )
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _activeQueueToken = MutableStateFlow<QueueToken?>(
        QueueToken(
            id = "q_892",
            tokenNumber = "A-104",
            appointmentId = "app_101",
            patientName = "Sarah Jenkins",
            departmentName = "Cardiology",
            doctorName = "Dr. Amanda Richardson",
            hospitalName = "MetroCare Central Hospital",
            currentServingToken = "A-101",
            estimatedWaitMinutes = 18,
            positionInQueue = 3,
            status = "Waiting",
            lastUpdatedTime = "1 min ago"
        )
    )
    val activeQueueToken: StateFlow<QueueToken?> = _activeQueueToken.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItemData>>(
        listOf(
            NotificationItemData(
                id = "n1",
                title = "Queue Update: 3 Patients Ahead",
                message = "Your token A-104 for Cardiology is approaching. Estimated turn in 18 mins.",
                timestamp = "10 mins ago",
                category = "Queue",
                isRead = false,
                targetRoute = "live_queue_tracking"
            ),
            NotificationItemData(
                id = "n2",
                title = "Appointment Confirmed",
                message = "Your appointment with Dr. Amanda Richardson is set for Tomorrow at 10:30 AM.",
                timestamp = "2 hours ago",
                category = "Appointment",
                isRead = true,
                targetRoute = "booking_history"
            ),
            NotificationItemData(
                id = "n3",
                title = "Low Crowds at MetroCare Central",
                message = "Current wait time at MetroCare Central ER is under 15 mins.",
                timestamp = "Yesterday",
                category = "Hospital",
                isRead = true,
                targetRoute = "hospital_details/h1"
            )
        )
    )
    val notifications: StateFlow<List<NotificationItemData>> = _notifications.asStateFlow()

    private val _faqs = MutableStateFlow<List<FAQ>>(
        listOf(
            FAQ("f1", "How does SmartCareAI estimate waiting times?", "SmartCareAI combines real-time queue registration logs, historical hourly department flow rates, and doctor consultation throughput to generate predictive wait estimates.", "Queue & Wait Times"),
            FAQ("f2", "Can I reschedule or cancel my appointment?", "Yes! Go to Booking History -> select your appointment -> tap 'Cancel or Reschedule'. You can choose a new time slot or cancel at least 1 hour prior.", "Appointments"),
            FAQ("f3", "What happens when my queue token is called?", "You will receive an immediate push notification and audio alert on SmartCareAI. Proceed to the designated consultation room indicated on your live queue screen.", "Queue & Wait Times"),
            FAQ("f4", "Is wheelchair or mobility assistance available?", "Yes. You can toggle mobility assistance in your Visit Preferences or Patient Profile. Hospital staff will be alerted upon your arrival.", "Accessibility"),
            FAQ("f5", "How do emergency triage services work?", "Emergency Triage in SmartCareAI provides instant hospital contact numbers, direct ER maps, and high-priority registration guidance.", "Emergency Care")
        )
    )
    val faqs: StateFlow<List<FAQ>> = _faqs.asStateFlow()

    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    // Helper functions
    fun getHospitalById(id: String): Hospital? {
        return _hospitals.value.find { it.id == id } ?: _hospitals.value.firstOrNull()
    }

    fun getDoctorById(id: String): Doctor? {
        return doctors.find { it.id == id } ?: doctors.firstOrNull()
    }

    fun getAppointmentById(id: String): Appointment? {
        return _appointments.value.find { it.id == id } ?: _appointments.value.firstOrNull()
    }

    fun addAppointment(
        appointmentType: AppointmentType,
        hospital: Hospital,
        doctor: Doctor,
        date: String,
        timeSlot: String,
        patientName: String,
        patientPhone: String,
        accessibilityNeeds: String
    ): Appointment {
        val ref = "SCA-" + (100000..999999).random()
        val newApp = Appointment(
            id = "app_" + UUID.randomUUID().toString().take(6),
            appointmentType = appointmentType,
            hospitalId = hospital.id,
            hospitalName = hospital.name,
            doctorId = doctor.id,
            doctorName = doctor.name,
            doctorSpecialization = doctor.specialization,
            departmentName = doctor.departmentName,
            date = date,
            timeSlot = timeSlot,
            patientName = patientName,
            patientPhone = patientPhone,
            accessibilityNeeds = accessibilityNeeds.ifBlank { "Standard Care" },
            status = AppointmentStatus.UPCOMING,
            referenceNumber = ref
        )
        _appointments.value = listOf(newApp) + _appointments.value

        // Auto assign a demo Queue Token for this booking
        val tokenNum = "A-" + (102..120).random()
        val currentServing = "A-" + (100..101).random()
        val queueToken = QueueToken(
            id = "q_" + UUID.randomUUID().toString().take(6),
            tokenNumber = tokenNum,
            appointmentId = newApp.id,
            patientName = patientName,
            departmentName = doctor.departmentName,
            doctorName = doctor.name,
            hospitalName = hospital.name,
            currentServingToken = currentServing,
            estimatedWaitMinutes = (12..25).random(),
            positionInQueue = (2..5).random(),
            status = "Waiting",
            lastUpdatedTime = "Just now"
        )
        _activeQueueToken.value = queueToken

        // Add a notification
        val notification = NotificationItemData(
            id = "n_" + UUID.randomUUID().toString().take(6),
            title = "Appointment Confirmed ($ref)",
            message = "Booking confirmed for ${doctor.name} at ${hospital.name} on $date at $timeSlot.",
            timestamp = "Just now",
            category = "Appointment",
            isRead = false,
            targetRoute = "booking_confirmation/${newApp.id}"
        )
        _notifications.value = listOf(notification) + _notifications.value

        return newApp
    }

    fun cancelAppointment(appointmentId: String) {
        _appointments.value = _appointments.value.map { app ->
            if (app.id == appointmentId) app.copy(status = AppointmentStatus.CANCELLED) else app
        }
        if (_activeQueueToken.value?.appointmentId == appointmentId) {
            _activeQueueToken.value = null
        }
    }

    fun rescheduleAppointment(appointmentId: String, newDate: String, newTimeSlot: String) {
        _appointments.value = _appointments.value.map { app ->
            if (app.id == appointmentId) {
                app.copy(date = newDate, timeSlot = newTimeSlot, status = AppointmentStatus.UPCOMING)
            } else app
        }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map { notif ->
            if (notif.id == id) notif.copy(isRead = true) else notif
        }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    fun toggleFaqExpand(faqId: String) {
        _faqs.value = _faqs.value.map { f ->
            if (f.id == faqId) f.copy(isExpanded = !f.isExpanded) else f
        }
    }

    fun submitSupportTicket(name: String, email: String, category: String, subject: String, message: String): SupportTicket {
        val ticket = SupportTicket(
            id = "TKT-" + (1000..9999).random(),
            name = name,
            email = email,
            category = category,
            subject = subject,
            message = message,
            timestamp = "Just now"
        )
        _supportTickets.value = listOf(ticket) + _supportTickets.value
        return ticket
    }

    fun refreshQueueState() {
        val current = _activeQueueToken.value ?: return
        val currentServingNum = current.currentServingToken.substringAfter("-").toIntOrNull() ?: 101
        val myTokenNum = current.tokenNumber.substringAfter("-").toIntOrNull() ?: 104
        val newServing = if (currentServingNum < myTokenNum) currentServingNum + 1 else currentServingNum
        val newWait = maxOf(0, current.estimatedWaitMinutes - 5)
        val newPos = maxOf(0, myTokenNum - newServing)

        _activeQueueToken.value = current.copy(
            currentServingToken = "A-$newServing",
            estimatedWaitMinutes = newWait,
            positionInQueue = newPos,
            lastUpdatedTime = "Updated just now",
            status = if (newPos == 0) "Called - Proceed to Room 3" else "Waiting"
        )
    }

    fun getCrowdDataForHospital(hospitalId: String): CrowdData {
        val hospital = getHospitalById(hospitalId)
        return CrowdData(
            hospitalId = hospital?.id ?: "h1",
            hospitalName = hospital?.name ?: "MetroCare Central Hospital",
            crowdLevel = hospital?.crowdLevel ?: CrowdLevel.LOW,
            peopleWaiting = 14,
            estimatedWaitRange = "10 - 20 mins",
            hourlyTrends = listOf(
                HourTrend("08 AM", 20, isOptimalWindow = true),
                HourTrend("09 AM", 35, isOptimalWindow = true),
                HourTrend("10 AM", 65, isOptimalWindow = false),
                HourTrend("11 AM", 85, isOptimalWindow = false),
                HourTrend("12 PM", 70, isOptimalWindow = false),
                HourTrend("01 PM", 40, isOptimalWindow = true),
                HourTrend("02 PM", 30, isOptimalWindow = true),
                HourTrend("03 PM", 50, isOptimalWindow = false),
                HourTrend("04 PM", 75, isOptimalWindow = false)
            ),
            lastUpdated = "2 mins ago"
        )
    }
}
