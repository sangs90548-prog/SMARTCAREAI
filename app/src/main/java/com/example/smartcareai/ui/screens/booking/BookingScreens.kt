package com.example.smartcareai.ui.screens.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcareai.data.models.Appointment
import com.example.smartcareai.data.models.AppointmentStatus
import com.example.smartcareai.data.models.AppointmentType
import com.example.smartcareai.ui.theme.*
import com.example.smartcareai.viewmodel.SmartCareViewModel

// 17. Choose Appointment Type
@Composable
fun ChooseAppointmentTypeScreen(
    viewModel: SmartCareViewModel,
    onNextClick: () -> Unit
) {
    val selectedType by viewModel.selectedAppointmentType.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Select Consultation Type", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Choose how you would like to receive medical care", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        AppointmentType.entries.forEach { type ->
            val isSelected = selectedType == type
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { viewModel.selectAppointmentType(type) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) SurfaceLightBlue else CardSurface),
                border = BorderStroke(1.5.dp, if (isSelected) PrimaryBlue else BorderLight)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.selectAppointmentType(type) }
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(type.displayName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        Text(type.description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Next: Visit Preferences", fontWeight = FontWeight.Bold)
        }
    }
}

// 18. Visit Preferences
@Composable
fun VisitPreferencesScreen(
    viewModel: SmartCareViewModel,
    onNextClick: () -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedSlot by viewModel.selectedTimeSlot.collectAsState()
    val notes by viewModel.accessibilityNotes.collectAsState()

    val dates = listOf("Tomorrow, Oct 24", "Friday, Oct 25", "Saturday, Oct 26", "Monday, Oct 28")
    val slots = listOf("09:00 AM", "10:30 AM", "01:30 PM", "03:00 PM", "04:30 PM")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Visit Preferences", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Choose your appointment date & time slot", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        Text("Select Preferred Date", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dates) { date ->
                val isSel = selectedDate == date
                Surface(
                    modifier = Modifier.clickable { viewModel.selectDate(date) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSel) PrimaryBlue else CardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) PrimaryBlue else BorderLight)
                ) {
                    Text(
                        text = date,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        color = if (isSel) Color.White else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Select Time Slot", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(slots) { slot ->
                val isSel = selectedSlot == slot
                Surface(
                    modifier = Modifier.clickable { viewModel.selectTimeSlot(slot) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSel) SecondaryTeal else CardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) SecondaryTeal else BorderLight)
                ) {
                    Text(
                        text = slot,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        color = if (isSel) Color.White else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Special / Accessibility Request", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { viewModel.setAccessibilityNotes(it) },
            placeholder = { Text("e.g. Wheelchair assistance, sign language, elevator access") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Next: Review Appointment", fontWeight = FontWeight.Bold)
        }
    }
}

// 19. Appointment Details
@Composable
fun AppointmentDetailsScreen(
    viewModel: SmartCareViewModel,
    onNextClick: () -> Unit
) {
    val hospital by viewModel.selectedHospital.collectAsState()
    val doctor by viewModel.selectedDoctor.collectAsState()
    val date by viewModel.selectedDate.collectAsState()
    val timeSlot by viewModel.selectedTimeSlot.collectAsState()
    val appType by viewModel.selectedAppointmentType.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Appointment Summary", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Inspect your selected consultation details", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(doctor?.name ?: "Dr. Amanda Richardson", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(doctor?.specialization ?: "Cardiologist", style = MaterialTheme.typography.bodySmall.copy(color = SecondaryTeal, fontWeight = FontWeight.Bold))

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hospital", color = TextSecondary, fontSize = 12.sp)
                    Text(hospital?.name ?: "MetroCare Central Hospital", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Consultation Mode", color = TextSecondary, fontSize = 12.sp)
                    Text(appType.displayName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryBlue)
                }
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Scheduled Time", color = TextSecondary, fontSize = 12.sp)
                    Text("$date at $timeSlot", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Proceed to Booking Review", fontWeight = FontWeight.Bold)
        }
    }
}

// 20. Booking Review
@Composable
fun BookingReviewScreen(
    viewModel: SmartCareViewModel,
    onConfirmClick: (String) -> Unit
) {
    val patientProfile by viewModel.patientProfile.collectAsState()
    val doctor by viewModel.selectedDoctor.collectAsState()
    val hospital by viewModel.selectedHospital.collectAsState()
    val date by viewModel.selectedDate.collectAsState()
    val timeSlot by viewModel.selectedTimeSlot.collectAsState()
    val isBookingLoading by viewModel.bookingLoading.collectAsState()
    val bookingError by viewModel.bookingError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Final Booking Review", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Verify patient details & confirm appointment registration", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        if (bookingError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmergencyRedLight)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EmergencyRed)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = bookingError!!,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = EmergencyRed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Patient Information", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(8.dp))
                Text("Name: ${patientProfile.name}", fontSize = 13.sp)
                Text("Phone: ${patientProfile.phone}", fontSize = 13.sp)
                Text("Email: ${patientProfile.email}", fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLightBlue)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Consultation Fee & Facility", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue))
                Spacer(Modifier.height(8.dp))
                Text("Doctor: ${doctor?.name} (${doctor?.specialization})", fontSize = 13.sp)
                Text("Hospital: ${hospital?.name}", fontSize = 13.sp)
                Text("Slot: $date • $timeSlot", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Consultation Fee: $${doctor?.consultationFee?.toInt() ?: 85}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SecondaryTeal)
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.confirmBooking(
                    onSuccess = { confirmedApp ->
                        onConfirmClick(confirmedApp.id)
                    },
                    onError = {
                        // Error message handled in UI via bookingError StateFlow
                    }
                )
            },
            enabled = !isBookingLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            if (isBookingLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Confirm & Reserve Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 21. Booking Confirmation
@Composable
fun BookingConfirmationScreen(
    bookingId: String,
    viewModel: SmartCareViewModel,
    onTrackQueueClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    val appointment = viewModel.repository.getAppointmentById(bookingId) ?: viewModel.appointments.collectAsState().value.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Surface(
            shape = CircleShape,
            color = TealLight,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(48.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Booking Confirmed!", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Ref Code: ${appointment.referenceNumber}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue))

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(appointment.doctorName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(appointment.doctorSpecialization, style = MaterialTheme.typography.bodySmall.copy(color = SecondaryTeal))
                Text(appointment.hospitalName, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(Modifier.height(12.dp))

                Text("Date: ${appointment.date}", fontSize = 13.sp)
                Text("Time: ${appointment.timeSlot}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Patient: ${appointment.patientName}", fontSize = 13.sp)
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onTrackQueueClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Track Live Queue Status", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onDoneClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Return to Discovery")
        }
    }
}

// 22. Doctor Visit History Screen
@Composable
fun BookingHistoryScreen(
    viewModel: SmartCareViewModel,
    onAppointmentClick: (String) -> Unit
) {
    val historyList by viewModel.patientVisitHistory.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: All Visits, 1: Upcoming, 2: Completed, 3: Cancelled
    var selectedDetailApp by remember { mutableStateOf<Appointment?>(null) }

    val filteredApps = when (selectedTab) {
        1 -> historyList.filter { it.status == AppointmentStatus.UPCOMING }
        2 -> historyList.filter { it.status == AppointmentStatus.COMPLETED }
        3 -> historyList.filter { it.status == AppointmentStatus.CANCELLED }
        else -> historyList // All Visits (latest visit first)
    }

    // Detailed Visit History Dialog Modal
    if (selectedDetailApp != null) {
        val app = selectedDetailApp!!
        AlertDialog(
            onDismissRequest = { selectedDetailApp = null },
            title = {
                Column {
                    Text("Doctor Visit Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    Text("Ref: ${app.referenceNumber}", style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue, fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = BorderLight)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Doctor:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(app.doctorName, fontSize = 12.sp, color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Specialization:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(app.doctorSpecialization, fontSize = 12.sp, color = SecondaryTeal)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Hospital:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(app.hospitalName, fontSize = 12.sp, color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Date & Time:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("${app.date} • ${app.timeSlot}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Status:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            text = app.status.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (app.status) {
                                AppointmentStatus.UPCOMING -> PrimaryBlue
                                AppointmentStatus.COMPLETED -> SecondaryTeal
                                AppointmentStatus.CANCELLED -> EmergencyRed
                            }
                        )
                    }
                    if (!app.tokenNumber.isNullOrBlank()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Token Number:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(app.tokenNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SecondaryTeal)
                        }
                    }
                    if (!app.consultationDetails.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text("Consultation Details / Notes:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Surface(shape = RoundedCornerShape(8.dp), color = SurfaceLightBlue, modifier = Modifier.fillMaxWidth()) {
                            Text(app.consultationDetails, modifier = Modifier.padding(10.dp), fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Patient: ${app.patientName} (${app.patientPhone})", fontSize = 11.sp, color = TextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val appToManage = selectedDetailApp
                        selectedDetailApp = null
                        if (appToManage != null && appToManage.status == AppointmentStatus.UPCOMING) {
                            onAppointmentClick(appToManage.id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(if (app.status == AppointmentStatus.UPCOMING) "Cancel / Reschedule" else "Done")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedDetailApp = null }) {
                    Text("Close")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Doctor Visit History", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Saved consultation visits and appointment history", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(12.dp))

        ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 0.dp) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("All Visits (${historyList.size})") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Upcoming") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Completed") })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Cancelled") })
        }

        Spacer(Modifier.height(12.dp))

        if (filteredApps.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.EventNote, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No Doctor Visit History Found", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    Text("Booked consultations and visit records will automatically be saved here.", fontSize = 12.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredApps) { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { selectedDetailApp = app },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(app.doctorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (app.status) {
                                        AppointmentStatus.UPCOMING -> SurfaceLightBlue
                                        AppointmentStatus.COMPLETED -> TealLight
                                        AppointmentStatus.CANCELLED -> EmergencyRedLight
                                    }
                                ) {
                                    Text(
                                        text = app.status.name,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (app.status) {
                                            AppointmentStatus.UPCOMING -> PrimaryBlue
                                            AppointmentStatus.COMPLETED -> SecondaryTeal
                                            AppointmentStatus.CANCELLED -> EmergencyRed
                                        }
                                    )
                                }
                            }

                            Text("${app.doctorSpecialization} • ${app.hospitalName}", fontSize = 12.sp, color = TextSecondary)

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${app.date} • ${app.timeSlot}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SecondaryTeal)

                                if (!app.tokenNumber.isNullOrBlank()) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = SurfaceLightBlue) {
                                        Text("Token: ${app.tokenNumber}", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 23. Cancel or Reschedule Appointment
@Composable
fun CancelRescheduleScreen(
    bookingId: String,
    viewModel: SmartCareViewModel,
    onDoneClick: () -> Unit
) {
    val appointment = viewModel.repository.getAppointmentById(bookingId) ?: viewModel.appointments.collectAsState().value.first()
    var newDate by remember { mutableStateOf("Monday, Oct 28") }
    var newSlot by remember { mutableStateOf("02:00 PM") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Cancel or Reschedule", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Reference Code: ${appointment.referenceNumber}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current Appointment Details", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(6.dp))
                Text("${appointment.doctorName} • ${appointment.hospitalName}", fontSize = 12.sp)
                Text("${appointment.date} at ${appointment.timeSlot}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Option 1: Reschedule Slot", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = newDate, onValueChange = { newDate = it }, label = { Text("New Date") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = newSlot, onValueChange = { newSlot = it }, label = { Text("New Time Slot") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.rescheduleAppointment(appointment.id, newDate, newSlot)
                onDoneClick()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Confirm Reschedule")
        }

        Spacer(Modifier.height(20.dp))

        Text("Option 2: Cancel Appointment", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmergencyRed))
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.cancelAppointment(appointment.id)
                onDoneClick()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
        ) {
            Text("Cancel Booking Permanently")
        }
    }
}
