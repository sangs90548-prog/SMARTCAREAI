package com.example.smartcareai.ui.screens.discovery

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcareai.data.models.AppointmentType
import com.example.smartcareai.data.models.CrowdLevel
import com.example.smartcareai.data.models.Hospital
import com.example.smartcareai.navigation.Screen
import com.example.smartcareai.ui.components.*
import com.example.smartcareai.ui.theme.*
import com.example.smartcareai.viewmodel.SmartCareViewModel

// 1. Welcome / Preferences
@Composable
fun WelcomePreferencesScreen(
    viewModel: SmartCareViewModel,
    onNavigateNext: () -> Unit
) {
    val preferredTime by viewModel.preferredVisitTime.collectAsState()
    val mobilityAssist by viewModel.mobilityAssist.collectAsState()
    val selectedType by viewModel.selectedAppointmentType.collectAsState()

    val visitTimes = listOf("Morning (9 AM - 12 PM)", "Afternoon (12 PM - 4 PM)", "Evening (4 PM - 8 PM)")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = CircleShape,
            color = PrimaryBlue.copy(alpha = 0.1f),
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to SmartCareAI",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )
        Text(
            text = "Personalize your visit preferences for instant hospital discovery & minimal wait times.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Visit Time Preference
        Text("Preferred Visit Window", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Spacer(modifier = Modifier.height(8.dp))
        visitTimes.forEach { time ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(if (preferredTime == time) SurfaceLightBlue else CardSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, if (preferredTime == time) PrimaryBlue else BorderLight, RoundedCornerShape(12.dp))
                    .clickable { viewModel.setPreferredVisitTimeSetting(time) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = preferredTime == time, onClick = { viewModel.setPreferredVisitTimeSetting(time) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(time, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = TextPrimary))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Appointment Type Preference
        Text("Default Care Mode", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AppointmentType.values().take(2).forEach { type ->
                val isSelected = selectedType == type
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectAppointmentType(type) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) SurfaceLightBlue else CardSurface),
                    border = BorderStroke(1.dp, if (isSelected) PrimaryBlue else BorderLight)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(if (type == AppointmentType.IN_PERSON) Icons.Default.LocalHospital else Icons.Default.VideoCall, contentDescription = null, tint = PrimaryBlue)
                        Spacer(Modifier.height(6.dp))
                        Text(type.displayName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary), textAlign = TextAlign.Center)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Accessibility Option
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Accessible, contentDescription = null, tint = SecondaryTeal)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Wheelchair / Mobility Assist", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Notify clinic staff upon check-in", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                }
                Switch(checked = mobilityAssist, onCheckedChange = { viewModel.setMobilityAssistSetting(it) })
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Explore Hospitals", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

// 2. Search Hospitals
@Composable
fun SearchHospitalsScreen(
    viewModel: SmartCareViewModel,
    onHospitalClick: (String) -> Unit,
    onBookClick: (Hospital) -> Unit,
    onFilterClick: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredHospitals by viewModel.filteredHospitals.collectAsState()
    var locationInput by remember { mutableStateOf("Medical District, Downtown") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Location Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null, tint = PrimaryBlue)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Your Current Location", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Text(locationInput, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                }
                TextButton(onClick = { locationInput = "Downtown Central, Sector 4" }) {
                    Text("Change", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Field with Filter Trigger
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search hospital, department, or care...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardSurface,
                    unfocusedContainerColor = CardSurface
                )
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(52.dp)
                    .background(PrimaryBlue, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        SectionHeader(
            title = "Nearby Hospitals (${filteredHospitals.size})",
            subtitle = "Live wait times updated 2 mins ago"
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredHospitals) { hospital ->
                HospitalCard(
                    hospital = hospital,
                    onCardClick = { onHospitalClick(hospital.id) },
                    onBookClick = {
                        viewModel.selectHospitalForBooking(hospital)
                        onBookClick(hospital)
                    }
                )
            }
        }
    }
}

// 3. Hospital List
@Composable
fun HospitalListScreen(
    viewModel: SmartCareViewModel,
    onHospitalClick: (String) -> Unit,
    onBookClick: (Hospital) -> Unit,
    onMapViewClick: () -> Unit
) {
    val hospitals by viewModel.filteredHospitals.collectAsState()

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
                Text("Hospital Directory", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text("Verified healthcare providers in your network", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }

            OutlinedButton(
                onClick = onMapViewClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Map View")
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(hospitals) { hospital ->
                HospitalCard(
                    hospital = hospital,
                    onCardClick = { onHospitalClick(hospital.id) },
                    onBookClick = {
                        viewModel.selectHospitalForBooking(hospital)
                        onBookClick(hospital)
                    }
                )
            }
        }
    }
}

// 4. Hospital Map View
@Composable
fun HospitalMapViewScreen(
    viewModel: SmartCareViewModel,
    onHospitalClick: (String) -> Unit
) {
    val hospitals by viewModel.hospitals.collectAsState()
    var selectedHospitalId by remember { mutableStateOf(hospitals.firstOrNull()?.id ?: "h1") }
    val selectedHospital = hospitals.find { it.id == selectedHospitalId } ?: hospitals.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Map Container Fallback
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFE2E8F0))
        ) {
            // Simulated Map Background Grid
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(6) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 2.dp)
                }
            }

            // Map Markers Overlay
            hospitals.forEachIndexed { index, h ->
                val isSel = h.id == selectedHospitalId
                Box(
                    modifier = Modifier
                        .offset(x = (40 + index * 70).dp, y = (60 + index * 90).dp)
                        .clickable { selectedHospitalId = h.id }
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) PrimaryBlue else CardSurface,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = if (isSel) Color.White else PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(h.name.take(12) + "...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextPrimary)
                        }
                    }
                }
            }

            // Map Controls
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                color = CardSurface,
                shadowElevation = 4.dp
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Recenter", tint = PrimaryBlue)
                }
            }
        }

        // Bottom Selected Hospital Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = CardSurface,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(selectedHospital.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        Text(selectedHospital.address, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                    CrowdBadge(crowdLevel = selectedHospital.crowdLevel)
                }

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${selectedHospital.distanceKm} km", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Est. ${selectedHospital.averageWaitMinutes} mins wait", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onHospitalClick(selectedHospital.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("View Full Hospital Details", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 5. Filter and Sort
@Composable
fun FilterSortScreen(
    viewModel: SmartCareViewModel,
    onApply: () -> Unit
) {
    val currentSort by viewModel.sortBy.collectAsState()
    val maxDist by viewModel.maxDistanceKm.collectAsState()
    val selectedDept by viewModel.selectedDepartmentFilter.collectAsState()

    val sortOptions = listOf("Distance", "Rating", "Wait Time")
    val deptOptions = listOf(null, "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "General Medicine")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(20.dp)
    ) {
        Text("Filter & Sort Hospitals", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Refine your hospital search criteria", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(20.dp))

        // Sort Control
        Text("Sort By", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sortOptions.forEach { opt ->
                val isSel = currentSort == opt
                FilterChip(
                    selected = isSel,
                    onClick = { viewModel.setSortBy(opt) },
                    label = { Text(opt) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Distance Slider
        Text("Maximum Distance: ${maxDist.toInt()} km", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Slider(
            value = maxDist.toFloat(),
            onValueChange = { viewModel.setMaxDistanceKm(it.toDouble()) },
            valueRange = 1f..25f,
            colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
        )

        Spacer(Modifier.height(20.dp))

        // Department Filter
        Text("Department Specialty", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(deptOptions) { dept ->
                val isSel = selectedDept == dept
                FilterChip(
                    selected = isSel,
                    onClick = { viewModel.setDepartmentFilter(dept) },
                    label = { Text(dept ?: "All Specialties") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SecondaryTeal,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = {
                    viewModel.setSortBy("Distance")
                    viewModel.setMaxDistanceKm(10.0)
                    viewModel.setDepartmentFilter(null)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset")
            }

            Button(
                onClick = onApply,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 6. Hospital Details
@Composable
fun HospitalDetailsScreen(
    hospitalId: String,
    viewModel: SmartCareViewModel,
    onBookClick: (Hospital) -> Unit,
    onDepartmentClick: () -> Unit,
    onBestTimeClick: () -> Unit,
    onLiveCrowdClick: () -> Unit
) {
    val hospital = viewModel.repository.getHospitalById(hospitalId) ?: viewModel.hospitals.collectAsState().value.first()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(hospital.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                            Text(hospital.category, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                        CrowdBadge(crowdLevel = hospital.crowdLevel)
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(hospital.address, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = SurfaceLightBlue, modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Avg Wait Time", fontSize = 11.sp, color = TextSecondary)
                                Text("${hospital.averageWaitMinutes} mins", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            }
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = TealLight, modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Beds Free", fontSize = 11.sp, color = TextSecondary)
                                Text("${hospital.liveBedAvailability} available", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SecondaryTeal)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Quick Actions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Quick Intelligence Actions", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onLiveCrowdClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                            Text("Live Crowd", fontSize = 11.sp)
                        }
                        OutlinedButton(onClick = onBestTimeClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                            Text("Best Visit Time", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Facilities Section
            Text("Hospital Facilities", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(hospital.facilities) { facility ->
                    Surface(shape = RoundedCornerShape(12.dp), color = CardSurface, border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)) {
                        Text(facility, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Departments Section
            SectionHeader(
                title = "Departments",
                actionText = "See All",
                onActionClick = onDepartmentClick
            )
            hospital.departments.forEach { dept ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onDepartmentClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = PrimaryBlue)
                        Spacer(Modifier.width(12.dp))
                        Text(dept, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

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
                Text("Book Appointment at Hospital", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 7. Hospital Comparison
@Composable
fun HospitalComparisonScreen(
    viewModel: SmartCareViewModel,
    onBookClick: (Hospital) -> Unit
) {
    val compIds by viewModel.comparisonHospitalIds.collectAsState()
    val allHospitals by viewModel.hospitals.collectAsState()
    val compHospitals = allHospitals.filter { compIds.contains(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Hospital Comparison", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Side-by-side evaluation of facilities, distance, and wait times", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(compHospitals) { h ->
                Card(
                    modifier = Modifier.width(220.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(h.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), maxLines = 2)
                        Text(h.category, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

                        Spacer(Modifier.height(10.dp))

                        CrowdBadge(crowdLevel = h.crowdLevel)

                        Spacer(Modifier.height(12.dp))

                        Text("Distance: ${h.distanceKm} km", fontSize = 12.sp)
                        Text("Rating: ${h.rating} ★ (${h.reviewCount})", fontSize = 12.sp)
                        Text("Est Wait: ~${h.averageWaitMinutes} mins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("Live Beds: ${h.liveBedAvailability}", fontSize = 12.sp, color = SecondaryTeal)

                        Spacer(Modifier.height(12.dp))

                        Text("Facilities:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        h.facilities.take(3).forEach { f ->
                            Text("• $f", fontSize = 11.sp, color = TextSecondary)
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.selectHospitalForBooking(h)
                                onBookClick(h)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Select", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
