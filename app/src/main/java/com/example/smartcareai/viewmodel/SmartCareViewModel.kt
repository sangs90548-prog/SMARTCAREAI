package com.example.smartcareai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcareai.data.local.DataStoreManager
import com.example.smartcareai.data.models.*
import com.example.smartcareai.data.repository.SmartCareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SmartCareViewModel(application: Application) : AndroidViewModel(application) {

    val repository = SmartCareRepository()
    private val dataStore = DataStoreManager(application)

    // DataStore Flows
    val largeTextEnabled: StateFlow<Boolean> = dataStore.largeTextFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )
    val notificationsEnabled: StateFlow<Boolean> = dataStore.notificationsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )
    val preferredVisitTime: StateFlow<String> = dataStore.preferredVisitTimeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "Morning (9 AM - 12 PM)"
    )
    val mobilityAssist: StateFlow<Boolean> = dataStore.mobilityAssistFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    // Repository Flows
    val hospitals: StateFlow<List<Hospital>> = repository.hospitals
    val appointments: StateFlow<List<Appointment>> = repository.appointments
    val activeQueueToken: StateFlow<QueueToken?> = repository.activeQueueToken
    val notifications: StateFlow<List<NotificationItemData>> = repository.notifications
    val faqs: StateFlow<List<FAQ>> = repository.faqs

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDepartmentFilter = MutableStateFlow<String?>(null)
    val selectedDepartmentFilter: StateFlow<String?> = _selectedDepartmentFilter.asStateFlow()

    private val _sortBy = MutableStateFlow("Distance") // "Distance", "Rating", "Wait Time"
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    private val _maxDistanceKm = MutableStateFlow(10.0)
    val maxDistanceKm: StateFlow<Double> = _maxDistanceKm.asStateFlow()

    // Comparison Selection State (Hospital IDs)
    private val _comparisonHospitalIds = MutableStateFlow<List<String>>(listOf("h1", "h2"))
    val comparisonHospitalIds: StateFlow<List<String>> = _comparisonHospitalIds.asStateFlow()

    // Active Booking Draft State
    private val _selectedAppointmentType = MutableStateFlow(AppointmentType.IN_PERSON)
    val selectedAppointmentType: StateFlow<AppointmentType> = _selectedAppointmentType.asStateFlow()

    private val _selectedHospital = MutableStateFlow<Hospital?>(repository.getHospitalById("h1"))
    val selectedHospital: StateFlow<Hospital?> = _selectedHospital.asStateFlow()

    private val _selectedDoctor = MutableStateFlow<Doctor?>(repository.getDoctorById("doc1"))
    val selectedDoctor: StateFlow<Doctor?> = _selectedDoctor.asStateFlow()

    private val _selectedDate = MutableStateFlow("Tomorrow, Oct 24")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedTimeSlot = MutableStateFlow("10:30 AM")
    val selectedTimeSlot: StateFlow<String> = _selectedTimeSlot.asStateFlow()

    private val _accessibilityNotes = MutableStateFlow("")
    val accessibilityNotes: StateFlow<String> = _accessibilityNotes.asStateFlow()

    // Patient Profile State
    private val _patientProfile = MutableStateFlow(PatientProfile())
    val patientProfile: StateFlow<PatientProfile> = _patientProfile.asStateFlow()

    // Filtered Hospitals Computation
    val filteredHospitals: StateFlow<List<Hospital>> = combine(
        hospitals, searchQuery, selectedDepartmentFilter, sortBy, maxDistanceKm
    ) { list, query, dept, sort, maxDist ->
        var result = list.filter { h ->
            val matchesQuery = query.isBlank() || h.name.contains(query, ignoreCase = true) ||
                    h.address.contains(query, ignoreCase = true) ||
                    h.departments.any { it.contains(query, ignoreCase = true) }
            val matchesDept = dept == null || h.departments.contains(dept)
            val matchesDist = h.distanceKm <= maxDist
            matchesQuery && matchesDept && matchesDist
        }
        result = when (sort) {
            "Rating" -> result.sortedByDescending { it.rating }
            "Wait Time" -> result.sortedBy { it.averageWaitMinutes }
            else -> result.sortedBy { it.distanceKm }
        }
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Intent Action Handlers
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setDepartmentFilter(dept: String?) { _selectedDepartmentFilter.value = dept }
    fun setSortBy(sort: String) { _sortBy.value = sort }
    fun setMaxDistanceKm(dist: Double) { _maxDistanceKm.value = dist }

    fun selectHospitalForBooking(hospital: Hospital) { _selectedHospital.value = hospital }
    fun selectDoctorForBooking(doctor: Doctor) {
        _selectedDoctor.value = doctor
        _selectedHospital.value = repository.getHospitalById(doctor.hospitalId)
    }
    fun selectAppointmentType(type: AppointmentType) { _selectedAppointmentType.value = type }
    fun selectDate(date: String) { _selectedDate.value = date }
    fun selectTimeSlot(slot: String) { _selectedTimeSlot.value = slot }
    fun setAccessibilityNotes(notes: String) { _accessibilityNotes.value = notes }

    fun toggleHospitalComparison(hospitalId: String) {
        val current = _comparisonHospitalIds.value.toMutableList()
        if (current.contains(hospitalId)) {
            if (current.size > 1) current.remove(hospitalId)
        } else {
            if (current.size < 3) current.add(hospitalId)
        }
        _comparisonHospitalIds.value = current
    }

    fun confirmBooking(): Appointment? {
        val h = _selectedHospital.value ?: repository.getHospitalById("h1") ?: return null
        val d = _selectedDoctor.value ?: repository.getDoctorById("doc1") ?: return null
        val profile = _patientProfile.value

        return repository.addAppointment(
            appointmentType = _selectedAppointmentType.value,
            hospital = h,
            doctor = d,
            date = _selectedDate.value,
            timeSlot = _selectedTimeSlot.value,
            patientName = profile.name,
            patientPhone = profile.phone,
            accessibilityNeeds = _accessibilityNotes.value.ifBlank {
                if (mobilityAssist.value) "Mobility Assistance Required" else "Standard Care"
            }
        )
    }

    fun cancelAppointment(appointmentId: String) {
        repository.cancelAppointment(appointmentId)
    }

    fun rescheduleAppointment(appointmentId: String, newDate: String, newTimeSlot: String) {
        repository.rescheduleAppointment(appointmentId, newDate, newTimeSlot)
    }

    fun refreshQueue() {
        repository.refreshQueueState()
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationAsRead(id)
    }

    fun clearNotifications() {
        repository.clearAllNotifications()
    }

    fun toggleFaqExpand(faqId: String) {
        repository.toggleFaqExpand(faqId)
    }

    fun setLargeTextEnabled(enabled: Boolean) {
        viewModelScope.launch { dataStore.setLargeText(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { dataStore.setNotifications(enabled) }
    }

    fun setPreferredVisitTimeSetting(time: String) {
        viewModelScope.launch { dataStore.setPreferredVisitTime(time) }
    }

    fun setMobilityAssistSetting(enabled: Boolean) {
        viewModelScope.launch { dataStore.setMobilityAssist(enabled) }
    }

    fun updatePatientProfile(name: String, phone: String, email: String, emergencyContact: String, bloodType: String) {
        _patientProfile.value = _patientProfile.value.copy(
            name = name,
            phone = phone,
            email = email,
            emergencyContact = emergencyContact,
            bloodType = bloodType
        )
        viewModelScope.launch { dataStore.updatePatientInfo(name, phone) }
    }

    fun submitSupport(name: String, email: String, category: String, subject: String, message: String): SupportTicket {
        return repository.submitSupportTicket(name, email, category, subject, message)
    }
}
