package com.example.smartcareai.ui.screens.doctors

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.smartcareai.data.models.Department
import com.example.smartcareai.data.models.Doctor
import com.example.smartcareai.data.models.Hospital
import com.example.smartcareai.ui.components.CrowdBadge
import com.example.smartcareai.ui.components.DoctorCard
import com.example.smartcareai.ui.theme.*
import com.example.smartcareai.viewmodel.SmartCareViewModel

// 8. Department Selection
@Composable
fun DepartmentSelectionScreen(
    viewModel: SmartCareViewModel,
    onDepartmentSelected: (Department) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val depts = viewModel.repository.departments.filter {
        query.isBlank() || it.name.contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search hospital department...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Hospital Departments (${depts.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(depts) { dept ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setDepartmentFilter(dept.name)
                            onDepartmentSelected(dept)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SurfaceLightBlue,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = PrimaryBlue)
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(dept.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), fontSize = 14.sp)
                        Text("${dept.doctorCount} Specialists", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                }
            }
        }
    }
}

// 9. Doctor Availability
@Composable
fun DoctorAvailabilityScreen(
    viewModel: SmartCareViewModel,
    onDoctorClick: (String) -> Unit,
    onBookClick: (Doctor) -> Unit
) {
    val doctors = viewModel.repository.doctors
    val activeFilter by viewModel.selectedDepartmentFilter.collectAsState()

    val filteredDoctors = doctors.filter {
        activeFilter == null || it.departmentName.equals(activeFilter, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Doctor Availability", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text(activeFilter ?: "All Departments & Specialists", style = MaterialTheme.typography.bodySmall.copy(color = SecondaryTeal, fontWeight = FontWeight.Bold))
            }

            if (activeFilter != null) {
                TextButton(onClick = { viewModel.setDepartmentFilter(null) }) {
                    Text("Clear Filter")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredDoctors) { doctor ->
                DoctorCard(
                    doctor = doctor,
                    onDoctorClick = { onDoctorClick(doctor.id) },
                    onBookClick = {
                        viewModel.selectDoctorForBooking(doctor)
                        onBookClick(doctor)
                    }
                )
            }
        }
    }
}

// 10. Doctor Profile
@Composable
fun DoctorProfileScreen(
    doctorId: String,
    viewModel: SmartCareViewModel,
    onBookClick: (Doctor) -> Unit
) {
    val doctor = viewModel.repository.getDoctorById(doctorId) ?: viewModel.repository.doctors.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    color = SurfaceLightBlue,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(48.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(doctor.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text(doctor.specialization, style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryTeal, fontWeight = FontWeight.Bold))
                Text(doctor.hospitalName, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(shape = RoundedCornerShape(10.dp), color = SurfaceLightBlue) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Experience", fontSize = 11.sp, color = TextSecondary)
                            Text("${doctor.experienceYears} Years", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }
                    Surface(shape = RoundedCornerShape(10.dp), color = TealLight) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Rating", fontSize = 11.sp, color = TextSecondary)
                            Text("${doctor.rating} ★", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SecondaryTeal)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Bio Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("About Doctor", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(6.dp))
                Text(doctor.bio, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Available Time Slots
        Text("Next Available Slots Today", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(doctor.availableSlots) { slot ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceLightBlue,
                    border = BorderStroke(1.dp, PrimaryBlue)
                ) {
                    Text(slot, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.selectDoctorForBooking(doctor)
                onBookClick(doctor)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Proceed to Book Consultation ($${doctor.consultationFee.toInt()})", fontWeight = FontWeight.Bold)
        }
    }
}

// 11. Recommended Hospital
@Composable
fun RecommendedHospitalScreen(
    viewModel: SmartCareViewModel,
    onBookClick: (Hospital) -> Unit
) {
    val hospitals by viewModel.hospitals.collectAsState()
    val hospital = hospitals.firstOrNull { it.isRecommended } ?: hospitals.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PrimaryBlue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("SMART CARE AI RECOMMENDATION", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                }

                Spacer(Modifier.height(10.dp))

                Text(hospital.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
                Text(hospital.category, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f)))
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Why SmartCareAI Recommended This Hospital?", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Lowest current waiting time (~${hospital.averageWaitMinutes} mins)", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Closest proximity to your current location (${hospital.distanceKm} km)", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("4.8 ★ Highest patient satisfaction rating", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.selectHospitalForBooking(hospital)
                onBookClick(hospital)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Book at Recommended Hospital", fontWeight = FontWeight.Bold)
        }
    }
}

// 12. Best Time to Visit
@Composable
fun BestTimeToVisitScreen(
    viewModel: SmartCareViewModel
) {
    val hospital = viewModel.selectedHospital.collectAsState().value ?: viewModel.repository.hospitals.value.first()
    val crowdData = viewModel.repository.getCrowdDataForHospital(hospital.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Best Time to Visit", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text(hospital.name, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TealLight)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Optimal Visiting Window Today", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryTeal, fontWeight = FontWeight.Bold))
                    Text(hospital.bestTimeToVisit, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Hourly Crowd Density Heatmap", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(crowdData.hourlyTrends) { trend ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(if (trend.isOptimalWindow) SurfaceLightBlue else CardSurface, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(trend.timeLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    LinearProgressIndicator(
                        progress = { trend.crowdPercentage / 100f },
                        modifier = Modifier
                            .width(140.dp)
                            .height(8.dp),
                        color = if (trend.isOptimalWindow) SecondaryTeal else WarningAmber,
                        trackColor = BorderLight
                    )
                    Text("${trend.crowdPercentage}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
