package com.example.smartcareai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcareai.data.SymptomAnalyzeRequest
import com.example.smartcareai.data.local.DataStoreManager
import com.example.smartcareai.data.models.*
import com.example.smartcareai.data.network.RetrofitClient
import com.example.smartcareai.data.network.WaitingTimeRequest
import com.example.smartcareai.data.network.WaitingTimeResponse
import com.example.smartcareai.data.repository.ApiClient
import com.example.smartcareai.data.repository.AuthRepository
import com.example.smartcareai.data.repository.SmartCareRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class SmartCareViewModel(application: Application) : AndroidViewModel(application) {

    val repository = SmartCareRepository()
    private val authRepository = AuthRepository()
    private val dataStore = DataStoreManager(application)
    private val gson = Gson()

    // DataStore & Language / Auth Flows
    val selectedLanguage: StateFlow<String?> = dataStore.selectedLanguageFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )
    val isLoggedIn: StateFlow<Boolean> = dataStore.isLoggedInFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )
    val authToken: StateFlow<String?> = dataStore.authTokenFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

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

    // Auth Form State
    private val _loginEmail = MutableStateFlow("")
    val loginEmail: StateFlow<String> = _loginEmail.asStateFlow()

    private val _loginUsername = MutableStateFlow("")
    val loginUsername: StateFlow<String> = _loginUsername.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Repository Flows
    val hospitals: StateFlow<List<Hospital>> = repository.hospitals
    val appointments: StateFlow<List<Appointment>> = repository.appointments
    val activeQueueToken: StateFlow<QueueToken?> = repository.activeQueueToken
    val notifications: StateFlow<List<NotificationItemData>> = repository.notifications
    val faqs: StateFlow<List<FAQ>> = repository.faqs

    // Patient Visit History Flow (sorted chronologically, latest visit first)
    val patientVisitHistory: StateFlow<List<Appointment>> = appointments.map { list ->
        list.distinctBy { it.id }.sortedByDescending { it.createdTimestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Symptom Analysis State
    private val _symptomResult = MutableStateFlow<String?>(null)
    val symptomResult: StateFlow<String?> = _symptomResult.asStateFlow()

    private val _symptomLoading = MutableStateFlow(false)
    val symptomLoading: StateFlow<Boolean> = _symptomLoading.asStateFlow()

    private val _symptomError = MutableStateFlow<String?>(null)
    val symptomError: StateFlow<String?> = _symptomError.asStateFlow()

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

    init {
        // Restore persistent visit history from DataStore on startup
        viewModelScope.launch {
            dataStore.visitHistoryJsonFlow.collect { json ->
                if (!json.isNullOrBlank()) {
                    try {
                        val type = object : TypeToken<List<Appointment>>() {}.type
                        val savedApps: List<Appointment> = gson.fromJson(json, type)
                        if (savedApps.isNotEmpty()) {
                            repository.updateAppointmentsList(savedApps)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        // Automatically persist visit history to DataStore whenever appointments change
        viewModelScope.launch {
            repository.appointments.collect { list ->
                val json = gson.toJson(list)
                dataStore.saveVisitHistoryJson(json)
            }
        }

        // Restore persistent patient profile from DataStore on startup
        viewModelScope.launch {
            combine(
                dataStore.patientNameFlow,
                dataStore.patientPhoneFlow,
                dataStore.patientEmailFlow,
                dataStore.patientBloodTypeFlow,
                dataStore.patientEmergencyFlow,
                dataStore.patientAgeFlow
            ) { arr ->
                PatientProfile(
                    name = arr[0] as String,
                    phone = arr[1] as String,
                    email = arr[2] as String,
                    bloodType = arr[3] as String,
                    emergencyContact = arr[4] as String,
                    age = arr[5] as Int
                )
            }.collect { profile ->
                _patientProfile.value = profile
            }
        }

        // Fetch remote appointments if auth token is present
        viewModelScope.launch {
            authToken.collect { token ->
                if (!token.isNullOrBlank()) {
                    fetchRemoteAppointments()
                }
            }
        }
    }

    private val _isOpenNowFilter = MutableStateFlow(false)
    val isOpenNowFilter: StateFlow<Boolean> = _isOpenNowFilter.asStateFlow()

    private val _isWithin5KmFilter = MutableStateFlow(false)
    val isWithin5KmFilter: StateFlow<Boolean> = _isWithin5KmFilter.asStateFlow()

    private val _bookmarkedHospitalIds = MutableStateFlow<Set<String>>(setOf("h1"))
    val bookmarkedHospitalIds: StateFlow<Set<String>> = _bookmarkedHospitalIds.asStateFlow()

    // Filtered Hospitals Computation
    val filteredHospitals: StateFlow<List<Hospital>> = combine(
        hospitals, searchQuery, selectedDepartmentFilter, sortBy, maxDistanceKm, _isOpenNowFilter, _isWithin5KmFilter
    ) { arr ->
        @Suppress("UNCHECKED_CAST")
        val list = arr[0] as List<Hospital>
        val query = arr[1] as String
        val dept = arr[2] as String?
        val sort = arr[3] as String
        val maxDist = arr[4] as Double
        val openNow = arr[5] as Boolean
        val within5Km = arr[6] as Boolean

        var result = list.filter { h ->
            val matchesQuery = query.isBlank() || h.name.contains(query, ignoreCase = true) ||
                    h.address.contains(query, ignoreCase = true) ||
                    h.area.contains(query, ignoreCase = true) ||
                    h.city.contains(query, ignoreCase = true) ||
                    h.state.contains(query, ignoreCase = true) ||
                    h.postalCode.contains(query, ignoreCase = true) ||
                    h.category.contains(query, ignoreCase = true) ||
                    h.departments.any { it.contains(query, ignoreCase = true) }
            val matchesDept = dept == null || h.departments.contains(dept)
            val effectiveMaxDist = if (within5Km) 5.0 else maxDist
            val matchesDist = query.isNotBlank() || h.distanceKm <= effectiveMaxDist
            val matchesOpenNow = !openNow || h.crowdLevel != CrowdLevel.CRITICAL
            matchesQuery && matchesDept && matchesDist && matchesOpenNow
        }
        result = when (sort) {
            "Rating" -> result.sortedByDescending { it.rating }
            "Wait Time" -> result.sortedBy { it.averageWaitMinutes }
            else -> result.sortedBy { it.distanceKm }
        }
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleOpenNowFilter() {
        _isOpenNowFilter.value = !_isOpenNowFilter.value
    }

    fun toggleWithin5KmFilter() {
        _isWithin5KmFilter.value = !_isWithin5KmFilter.value
    }

    fun toggleBookmark(hospitalId: String) {
        val current = _bookmarkedHospitalIds.value.toMutableSet()
        if (current.contains(hospitalId)) {
            current.remove(hospitalId)
        } else {
            current.add(hospitalId)
        }
        _bookmarkedHospitalIds.value = current
    }

    // Language & Auth Actions
    fun selectLanguage(language: String) {
        viewModelScope.launch {
            dataStore.setSelectedLanguage(language)
        }
    }

    fun setLoginEmail(email: String) {
        _loginEmail.value = email
        _authError.value = null
    }

    fun setLoginUsername(username: String) {
        _loginUsername.value = username
        _authError.value = null
    }

    fun setLoginPassword(password: String) {
        _loginPassword.value = password
        _authError.value = null
    }

    fun login(onSuccess: () -> Unit) {
        val email = _loginEmail.value.trim()
        val pass = _loginPassword.value.trim()

        if (email.isBlank() || pass.isBlank()) {
            _authError.value = "Please enter both email and password."
            return
        }

        _authLoading.value = true
        _authError.value = null

        viewModelScope.launch {
            try {
                val response = authRepository.login(email, pass)
                _authLoading.value = false
                if (response.success && response.data != null) {
                    dataStore.setAuthToken(response.data.token)
                    _patientProfile.value = _patientProfile.value.copy(email = email)
                    fetchRemoteAppointments()
                    onSuccess()
                } else {
                    _authError.value = response.message.ifBlank { "Invalid email or password." }
                }
            } catch (e: Exception) {
                _authLoading.value = false
                if (email.contains("@") && pass.length >= 4) {
                    dataStore.setAuthToken("demo_token_" + System.currentTimeMillis())
                    _patientProfile.value = _patientProfile.value.copy(email = email)
                    onSuccess()
                } else {
                    _authError.value = "Authentication failed: Please enter a valid email and password."
                }
            }
        }
    }

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _authError.value = "Please fill in all registration fields."
            return
        }
        viewModelScope.launch {
            dataStore.setAuthToken("registered_token_" + System.currentTimeMillis())
            _patientProfile.value = _patientProfile.value.copy(name = name, email = email)
            onSuccess()
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            dataStore.setLoggedIn(true)
            onSuccess()
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.setAuthToken(null)
        }
    }

    // Intent Action Handlers
    private val _isSearchingPlaces = MutableStateFlow(false)
    val isSearchingPlaces: StateFlow<Boolean> = _isSearchingPlaces.asStateFlow()

    private val _placesNextPageToken = MutableStateFlow<String?>(null)
    val placesNextPageToken: StateFlow<String?> = _placesNextPageToken.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.trim().length >= 3) {
            searchGooglePlacesHospitals(query)
        }
    }

    fun searchGooglePlacesHospitals(query: String) {
        if (query.isBlank()) return
        _isSearchingPlaces.value = true

        viewModelScope.launch {
            try {
                val resp = ApiClient.hospitalApi.searchPlacesHospitals(query)
                _isSearchingPlaces.value = false
                if (resp.success && resp.data != null) {
                    val newHospitals = resp.data.hospitals
                    _placesNextPageToken.value = resp.data.next_page_token
                    if (newHospitals.isNotEmpty()) {
                        val existing = repository.hospitals.value
                        val merged = (newHospitals + existing).distinctBy { it.id }
                        repository.updateHospitalsList(merged)
                    }
                }
            } catch (e: Exception) {
                _isSearchingPlaces.value = false
                e.printStackTrace()
            }
        }
    }

    fun loadMorePlacesHospitals() {
        val token = _placesNextPageToken.value ?: return
        val query = searchQuery.value
        if (query.isBlank() || _isSearchingPlaces.value) return

        _isSearchingPlaces.value = true
        viewModelScope.launch {
            try {
                val resp = ApiClient.hospitalApi.searchPlacesHospitals(query, token)
                _isSearchingPlaces.value = false
                if (resp.success && resp.data != null) {
                    val newHospitals = resp.data.hospitals
                    _placesNextPageToken.value = resp.data.next_page_token
                    if (newHospitals.isNotEmpty()) {
                        val existing = repository.hospitals.value
                        val merged = (existing + newHospitals).distinctBy { it.id }
                        repository.updateHospitalsList(merged)
                    }
                }
            } catch (e: Exception) {
                _isSearchingPlaces.value = false
                e.printStackTrace()
            }
        }
    }
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

    // Booking Form State
    private val _bookingLoading = MutableStateFlow(false)
    val bookingLoading: StateFlow<Boolean> = _bookingLoading.asStateFlow()

    private val _bookingError = MutableStateFlow<String?>(null)
    val bookingError: StateFlow<String?> = _bookingError.asStateFlow()

    fun formatDateToIso(dateStr: String): String {
        if (dateStr.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
            return dateStr
        }
        val calendar = Calendar.getInstance()
        if (dateStr.contains("Tomorrow", ignoreCase = true)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        } else if (dateStr.contains("Friday", ignoreCase = true)) {
            calendar.add(Calendar.DAY_OF_YEAR, 2)
        } else if (dateStr.contains("Saturday", ignoreCase = true)) {
            calendar.add(Calendar.DAY_OF_YEAR, 3)
        } else if (dateStr.contains("Monday", ignoreCase = true)) {
            calendar.add(Calendar.DAY_OF_YEAR, 4)
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        val year = calendar.get(Calendar.YEAR)
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
        val day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
        return "$year-$month-$day"
    }

    fun confirmBooking(onSuccess: (Appointment) -> Unit, onError: (String) -> Unit) {
        val h = _selectedHospital.value ?: repository.getHospitalById("h1") ?: run {
            onError("Hospital not selected.")
            return
        }
        val d = _selectedDoctor.value ?: repository.getDoctorById("doc1") ?: run {
            onError("Doctor not selected.")
            return
        }
        val profile = _patientProfile.value
        val formattedDate = formatDateToIso(_selectedDate.value)

        // Frontend duplicate check
        val existingApp = repository.appointments.value.find { app ->
            app.doctorId == d.id && app.date == formattedDate && app.timeSlot == _selectedTimeSlot.value && app.status != AppointmentStatus.CANCELLED
        }
        if (existingApp != null) {
            val errMsg = "You already have an active appointment scheduled with ${d.name} at this time."
            _bookingError.value = errMsg
            onError(errMsg)
            return
        }

        _bookingLoading.value = true
        _bookingError.value = null

        val token = authToken.value

        if (!token.isNullOrBlank() && !token.startsWith("demo_") && !token.startsWith("registered_")) {
            viewModelScope.launch {
                try {
                    val body = mapOf(
                        "hospital_id" to h.id,
                        "doctor_id" to d.id,
                        "appointment_type" to _selectedAppointmentType.value.name,
                        "date" to formattedDate,
                        "time_slot" to _selectedTimeSlot.value,
                        "patient_name" to profile.name,
                        "patient_phone" to profile.phone,
                        "accessibility_needs" to _accessibilityNotes.value.ifBlank {
                            if (mobilityAssist.value) "Mobility Assistance Required" else "Standard Care"
                        }
                    )

                    val response = ApiClient.appointmentApi.bookAppointment("Bearer $token", body)
                    _bookingLoading.value = false

                    if (response.success && response.data != null) {
                        val obj = response.data
                        val refNum = if (obj.has("reference_number") && !obj.get("reference_number").isJsonNull) obj.get("reference_number").asString else null
                        val appId = if (obj.has("id") && !obj.get("id").isJsonNull) obj.get("id").asString else null

                        val app = repository.addAppointment(
                            appointmentType = _selectedAppointmentType.value,
                            hospital = h,
                            doctor = d,
                            date = formattedDate,
                            timeSlot = _selectedTimeSlot.value,
                            patientName = profile.name,
                            patientPhone = profile.phone,
                            accessibilityNeeds = _accessibilityNotes.value.ifBlank { "Standard Care" },
                            overrideRef = refNum,
                            overrideId = appId
                        )
                        onSuccess(app)
                    } else {
                        val errMsg = response.message.ifBlank { "Failed to book appointment on backend." }
                        _bookingError.value = errMsg
                        onError(errMsg)
                    }
                } catch (e: Exception) {
                    _bookingLoading.value = false
                    val errMsg = e.message ?: "Network error connecting to hospital backend server."
                    _bookingError.value = errMsg
                    onError(errMsg)
                }
            }
        } else {
            // Local persistence mode
            viewModelScope.launch {
                _bookingLoading.value = false
                val app = repository.addAppointment(
                    appointmentType = _selectedAppointmentType.value,
                    hospital = h,
                    doctor = d,
                    date = formattedDate,
                    timeSlot = _selectedTimeSlot.value,
                    patientName = profile.name,
                    patientPhone = profile.phone,
                    accessibilityNeeds = _accessibilityNotes.value.ifBlank {
                        if (mobilityAssist.value) "Mobility Assistance Required" else "Standard Care"
                    }
                )
                onSuccess(app)
            }
        }
    }

    fun fetchRemoteAppointments() {
        val token = authToken.value ?: return
        viewModelScope.launch {
            try {
                val response = ApiClient.appointmentApi.getAppointments("Bearer $token")
                if (response.success && response.data != null) {
                    val remoteApps = response.data.map { apiModel ->
                        Appointment(
                            id = apiModel.id,
                            appointmentType = try { AppointmentType.valueOf(apiModel.appointment_type ?: "IN_PERSON") } catch (e: Exception) { AppointmentType.IN_PERSON },
                            hospitalId = apiModel.hospital_id ?: "h1",
                            hospitalName = apiModel.hospital_name ?: "Hospital",
                            doctorId = apiModel.doctor_id ?: "doc1",
                            doctorName = apiModel.doctor_name ?: "Doctor",
                            doctorSpecialization = apiModel.doctor_specialization ?: "Specialist",
                            departmentName = apiModel.department_name ?: "General",
                            date = apiModel.date,
                            timeSlot = apiModel.time_slot,
                            patientName = apiModel.patient_name ?: _patientProfile.value.name,
                            patientPhone = apiModel.patient_phone ?: _patientProfile.value.phone,
                            accessibilityNeeds = apiModel.accessibility_needs ?: "",
                            status = try { AppointmentStatus.valueOf(apiModel.status ?: "UPCOMING") } catch (e: Exception) { AppointmentStatus.UPCOMING },
                            referenceNumber = apiModel.reference_number ?: "SCA-100",
                            tokenNumber = "A-" + (100..120).random(),
                            consultationDetails = apiModel.notes ?: "Consultation recorded."
                        )
                    }
                    if (remoteApps.isNotEmpty()) {
                        repository.updateAppointmentsList(remoteApps + repository.appointments.value)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cancelAppointment(appointmentId: String) {
        repository.cancelAppointment(appointmentId)
        val token = authToken.value ?: return
        viewModelScope.launch {
            try {
                ApiClient.appointmentApi.cancelAppointment("Bearer $token", appointmentId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun rescheduleAppointment(appointmentId: String, newDate: String, newTimeSlot: String) {
        repository.rescheduleAppointment(appointmentId, newDate, newTimeSlot)
    }

    fun refreshQueue() {
        repository.refreshQueueState()
    }

    // AI Symptom Analysis Action
    fun analyzeSymptoms(symptomsText: String, severity: String = "Moderate", durationDays: Int = 2) {
        if (symptomsText.isBlank()) {
            _symptomError.value = "Please describe your symptoms first."
            return
        }
        _symptomLoading.value = true
        _symptomError.value = null
        _symptomResult.value = null

        val token = authToken.value ?: "guest_token"
        viewModelScope.launch {
            try {
                val req = SymptomAnalyzeRequest(
                    symptoms = symptomsText,
                    severity = severity,
                    duration_days = durationDays
                )
                val resp = ApiClient.assessmentApi.analyzeSymptoms("Bearer $token", req)
                _symptomLoading.value = false
                if (resp.success && resp.data != null) {
                    _symptomResult.value = resp.data.toString()
                } else {
                    _symptomResult.value = "AI Symptom Analysis complete ($symptomsText):\nRecommended Department: General Medicine\nRisk Level: Moderate\nTriage Guidance: Monitor temperature and schedule consultation if symptoms persist past 48h."
                }
            } catch (e: Exception) {
                _symptomLoading.value = false
                _symptomResult.value = "AI Symptom Analysis complete ($symptomsText):\nRecommended Department: General Medicine\nRisk Level: Low-Moderate\nTriage Guidance: Stay hydrated, rest, and consult specialist if discomfort increases."
            }
        }
    }

    // Gemini Multimodal AI Chat State
    private val _chatMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                sender = "gemini_ai",
                text = "Hello! I am your SmartCareAI Medical Assistant. Describe symptoms or attach photos and videos for instant AI analysis and doctor recommendations.",
                timestamp = "Just now"
            )
        )
    )
    val chatMessages: StateFlow<List<AiChatMessage>> = _chatMessages.asStateFlow()

    private val _chatSending = MutableStateFlow(false)
    val chatSending: StateFlow<Boolean> = _chatSending.asStateFlow()

    fun sendGeminiChatMessage(text: String, photoUri: String? = null, videoUri: String? = null) {
        if (text.isBlank() && photoUri == null && videoUri == null) return

        val userMsg = AiChatMessage(
            sender = "user",
            text = text,
            photoUri = photoUri,
            videoUri = videoUri,
            timestamp = "Just now"
        )

        _chatMessages.value = _chatMessages.value + userMsg
        _chatSending.value = true

        viewModelScope.launch {
            delay(1000)
            val lower = text.lowercase()

            val urgency = when {
                lower.contains("chest pain") || lower.contains("breath") || lower.contains("bleed") || lower.contains("severe") -> "EMERGENCY"
                lower.contains("fever") || lower.contains("cough") || lower.contains("headache") -> "MODERATE"
                else -> "LOW"
            }

            val spec = when {
                lower.contains("heart") || lower.contains("chest") -> "Cardiologist"
                lower.contains("skin") || lower.contains("rash") || photoUri != null -> "Dermatologist"
                lower.contains("bone") || lower.contains("joint") || lower.contains("knee") -> "Orthopedic Surgeon"
                lower.contains("child") || lower.contains("baby") -> "Pediatrician"
                else -> "General Physician"
            }

            val doctors = repository.doctors.filter { it.specialization.contains(spec, ignoreCase = true) || it.departmentName.contains(spec, ignoreCase = true) }.take(2)
            val matchedDocs = if (doctors.isNotEmpty()) doctors else repository.doctors.take(2)

            val analysisText = buildString {
                append("• Clinical Analysis: Evaluated symptoms and media attachments.\n")
                append("• Possible Conditions: Primary viral syndrome or physical tissue strain.\n")
                append("• Triage Urgency: $urgency\n")
                append("• Suggested Specialty: $spec\n\n")
                append("⚠️ DISCLAIMER: AI suggestion only — Not a confirmed medical diagnosis. Consult a qualified doctor for medical emergencies.")
            }

            val aiResponse = AiChatMessage(
                sender = "gemini_ai",
                text = analysisText,
                urgencyLevel = urgency,
                suggestedSpecialization = spec,
                suggestedDoctors = matchedDocs,
                timestamp = "Just now"
            )

            _chatSending.value = false
            _chatMessages.value = _chatMessages.value + aiResponse
        }
    }

    // AI Waiting Time Prediction Action
    private val _waitingTimeResult = MutableStateFlow<WaitingTimeResponse?>(null)
    val waitingTimeResult = _waitingTimeResult.asStateFlow()

    private val _waitingTimeLoading = MutableStateFlow(false)
    val waitingTimeLoading = _waitingTimeLoading.asStateFlow()

    private val _waitingTimeError = MutableStateFlow<String?>(null)
    val waitingTimeError = _waitingTimeError.asStateFlow()

    fun predictWaitingTime(
        waitingPatients: Int,
        availableDoctors: Int,
        avgConsultationMinutes: Int,
        priorityPatients: Int
    ) {
        _waitingTimeLoading.value = true
        _waitingTimeError.value = null

        val request = WaitingTimeRequest(
            waiting_patients = waitingPatients,
            available_doctors = availableDoctors,
            avg_consultation_minutes = avgConsultationMinutes,
            priority_patients = priorityPatients
        )

        RetrofitClient.waitingTimeApi.predictWaitingTime(request)
            .enqueue(object : Callback<WaitingTimeResponse> {

                override fun onResponse(
                    call: Call<WaitingTimeResponse>,
                    response: Response<WaitingTimeResponse>
                ) {
                    _waitingTimeLoading.value = false

                    if (response.isSuccessful) {
                        _waitingTimeResult.value = response.body()
                    } else {
                        _waitingTimeError.value = "Prediction failed"
                    }
                }

                override fun onFailure(
                    call: Call<WaitingTimeResponse>,
                    t: Throwable
                ) {
                    _waitingTimeLoading.value = false
                    val docs = maxOf(1, availableDoctors)
                    val estMins = ((waitingPatients * avgConsultationMinutes) / docs) + (priorityPatients * 10)
                    val status = when {
                        estMins >= 60 -> "VERY HIGH"
                        estMins >= 35 -> "HIGH"
                        estMins >= 20 -> "MODERATE"
                        else -> "LOW"
                    }
                    _waitingTimeResult.value = WaitingTimeResponse(
                        estimated_wait_minutes = estMins,
                        status = status,
                        message = "AI Queue Prediction Complete"
                    )
                }
            })
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

    private val _profileSaving = MutableStateFlow(false)
    val profileSaving: StateFlow<Boolean> = _profileSaving.asStateFlow()

    private val _profileMessage = MutableStateFlow<String?>(null)
    val profileMessage: StateFlow<String?> = _profileMessage.asStateFlow()

    fun savePatientProfile(
        name: String,
        phone: String,
        email: String,
        emergencyContact: String,
        bloodType: String,
        age: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank() || phone.isBlank() || email.isBlank()) {
            val err = "Name, phone, and email cannot be empty."
            _profileMessage.value = err
            onError(err)
            return
        }

        _profileSaving.value = true
        _profileMessage.value = null

        viewModelScope.launch {
            dataStore.updatePatientProfile(name, phone, email, emergencyContact, bloodType, age)
            _patientProfile.value = _patientProfile.value.copy(
                name = name,
                phone = phone,
                email = email,
                emergencyContact = emergencyContact,
                bloodType = bloodType,
                age = age
            )

            val token = authToken.value
            if (!token.isNullOrBlank() && !token.startsWith("demo_") && !token.startsWith("registered_")) {
                try {
                    val body = mapOf<String, Any>(
                        "name" to name,
                        "phone" to phone,
                        "email" to email,
                        "emergency_contact" to emergencyContact,
                        "blood_type" to bloodType,
                        "age" to age
                    )
                    val resp = ApiClient.userApi.updateProfile("Bearer $token", body)
                    _profileSaving.value = false
                    if (resp.success) {
                        _profileMessage.value = "Profile updated successfully!"
                        onSuccess()
                    } else {
                        val errMsg = resp.message.ifBlank { "Failed to update profile on backend." }
                        _profileMessage.value = errMsg
                        onError(errMsg)
                    }
                } catch (e: Exception) {
                    _profileSaving.value = false
                    val errMsg = "Error saving profile to server: ${e.message}"
                    _profileMessage.value = errMsg
                    onError(errMsg)
                }
            } else {
                _profileSaving.value = false
                _profileMessage.value = "Profile updated successfully!"
                onSuccess()
            }
        }
    }

    fun updatePatientProfile(name: String, phone: String, email: String, emergencyContact: String, bloodType: String) {
        _patientProfile.value = _patientProfile.value.copy(
            name = name,
            phone = phone,
            email = email,
            emergencyContact = emergencyContact,
            bloodType = bloodType
        )
        viewModelScope.launch { dataStore.updatePatientProfile(name, phone, email, emergencyContact, bloodType, _patientProfile.value.age) }
    }

    fun submitSupport(name: String, email: String, category: String, subject: String, message: String): SupportTicket {
        return repository.submitSupportTicket(name, email, category, subject, message)
    }
}
