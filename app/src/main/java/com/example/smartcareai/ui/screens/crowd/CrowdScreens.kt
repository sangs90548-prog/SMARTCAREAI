package com.example.smartcareai.ui.screens.crowd

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.smartcareai.ui.components.CrowdBadge
import com.example.smartcareai.ui.theme.*
import com.example.smartcareai.viewmodel.SmartCareViewModel

// 13. Live Crowd Monitoring
@Composable
fun LiveCrowdMonitoringScreen(
    viewModel: SmartCareViewModel
) {
    val selHospital by viewModel.selectedHospital.collectAsState()
    val hospitals by viewModel.hospitals.collectAsState()
    val hospital = selHospital ?: hospitals.first()
    val crowdData = viewModel.repository.getCrowdDataForHospital(hospital.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text(
            "Live Crowd Monitoring",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Text(
            "Real-time department capacity & queue occupancy",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            hospital.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Updated ${crowdData.lastUpdated}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }

                    CrowdBadge(crowdLevel = crowdData.crowdLevel)
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceLightBlue,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("People Waiting", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                "${crowdData.peopleWaiting}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TealLight,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Est Wait Range", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                crowdData.estimatedWaitRange,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryTeal
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { viewModel.refreshQueue() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Force Live Refresh Now", fontWeight = FontWeight.Bold)
        }
    }
}

// 14. Waiting-Time Prediction & AI Healthcare Assistance
@Composable
fun WaitingTimePredictionScreen(
    viewModel: SmartCareViewModel
) {
    val hospitals by viewModel.hospitals.collectAsState()
    var selectedHospitalId by remember { mutableStateOf(hospitals.firstOrNull()?.id ?: "HOSP-IN-001") }
    val selectedHospital = hospitals.find { it.id == selectedHospitalId } ?: hospitals.first()

    var selectedDept by remember(selectedHospitalId) { mutableStateOf(selectedHospital.departments.firstOrNull() ?: "Cardiology") }

    var waitingPatients by remember { mutableFloatStateOf(20f) }
    var availableDoctors by remember { mutableFloatStateOf(2f) }
    var avgConsultationMins by remember { mutableFloatStateOf(15f) }
    var priorityPatients by remember { mutableFloatStateOf(2f) }

    var symptomsText by remember { mutableStateOf("") }
    val symptomResult by viewModel.symptomResult.collectAsState()
    val symptomLoading by viewModel.symptomLoading.collectAsState()
    val symptomError by viewModel.symptomError.collectAsState()

    val result by viewModel.waitingTimeResult.collectAsState()
    val loading by viewModel.waitingTimeLoading.collectAsState()
    val error by viewModel.waitingTimeError.collectAsState()

    LaunchedEffect(selectedHospitalId, selectedDept, waitingPatients, availableDoctors, avgConsultationMins, priorityPatients) {
        viewModel.predictWaitingTime(
            waitingPatients = waitingPatients.toInt(),
            availableDoctors = availableDoctors.toInt(),
            avgConsultationMinutes = avgConsultationMins.toInt(),
            priorityPatients = priorityPatients.toInt()
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        item {
            Text(
                "AI Healthcare Assistant",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Text(
                "SmartCareAI Predictive Wait-Time Analytics & Symptom Assessment",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(Modifier.height(16.dp))

            // Hospital & Department Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("1. Select Hospital & Department", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue))
                    Spacer(Modifier.height(8.dp))

                    Text("Select Hospital", fontSize = 11.sp, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(hospitals.take(8)) { h ->
                            val isSel = h.id == selectedHospitalId
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    selectedHospitalId = h.id
                                    viewModel.selectHospitalForBooking(h)
                                },
                                label = { Text(h.name.take(18) + "...") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text("Select Department Specialty", fontSize = 11.sp, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedHospital.departments) { dept ->
                            val isSel = dept == selectedDept
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedDept = dept },
                                label = { Text(dept) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SecondaryTeal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Live Queue Inputs & Analytics
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("2. Live Queue Parameters", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue))
                        Surface(shape = RoundedCornerShape(8.dp), color = TealLight) {
                            Text("LIVE DATA", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SecondaryTeal)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text("Total Patients Waiting: ${waitingPatients.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = waitingPatients,
                        onValueChange = { waitingPatients = it },
                        valueRange = 1f..50f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                    )

                    Text("Available Doctors on Duty: ${availableDoctors.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = availableDoctors,
                        onValueChange = { availableDoctors = it },
                        valueRange = 1f..10f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                    )

                    Text("Avg Consultation Pace: ${avgConsultationMins.toInt()} mins", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = avgConsultationMins,
                        onValueChange = { avgConsultationMins = it },
                        valueRange = 5f..30f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                    )

                    Text("Priority Emergency Cases: ${priorityPatients.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = priorityPatients,
                        onValueChange = { priorityPatients = it },
                        valueRange = 0f..10f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                    )

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.predictWaitingTime(
                                waitingPatients = waitingPatients.toInt(),
                                availableDoctors = availableDoctors.toInt(),
                                avgConsultationMinutes = avgConsultationMins.toInt(),
                                priorityPatients = priorityPatients.toInt()
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Recalculate AI Waiting Time", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // AI Wait Time Prediction Results
            Text(
                "3. AI Wait Time Prediction Result",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLightBlue)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "ESTIMATED CONSULTATION DELAY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(Modifier.height(6.dp))

                    if (loading) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = result?.let { "${it.estimated_wait_minutes} Mins" } ?: "25 Mins",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Queue Status: ", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = result?.status ?: "MODERATE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if ((result?.status ?: "MODERATE") == "LOW") SecondaryTeal else WarningAmber
                            )
                        }
                    }

                    error?.let { errorMessage ->
                        Spacer(Modifier.height(4.dp))
                        Text(text = errorMessage, color = EmergencyRed, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "${selectedHospital.name} • $selectedDept",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        "Live sync • Updated just now",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 4. Gemini Multimodal AI Assistant (Text, Photo, Video Chat)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Gemini AI Healthcare Assistant", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue))
                            Text("Analyze symptoms with text, photos or video clips", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    val chatMessages by viewModel.chatMessages.collectAsState()
                    val chatSending by viewModel.chatSending.collectAsState()
                    val listState = rememberLazyListState()

                    var inputQuery by remember { mutableStateOf("") }
                    var attachedPhoto by remember { mutableStateOf<String?>(null) }
                    var attachedVideo by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(chatMessages.size) {
                        if (chatMessages.isNotEmpty()) {
                            listState.animateScrollToItem(chatMessages.size - 1)
                        }
                    }

                    // Chat History List
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(SurfaceLightBlue, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(chatMessages) { msg ->
                                val isUser = msg.sender == "user"
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (isUser) 12.dp else 2.dp,
                                            bottomEnd = if (isUser) 2.dp else 12.dp
                                        ),
                                        color = if (isUser) PrimaryBlue else CardSurface,
                                        shadowElevation = 2.dp,
                                        modifier = Modifier.widthIn(max = 280.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            if (msg.photoUri != null) {
                                                Surface(shape = RoundedCornerShape(8.dp), color = TealLight, modifier = Modifier.padding(bottom = 6.dp)) {
                                                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(16.dp))
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("Medical Photo Attached", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryTeal)
                                                    }
                                                }
                                            }
                                            if (msg.videoUri != null) {
                                                Surface(shape = RoundedCornerShape(8.dp), color = WarningAmberLight, modifier = Modifier.padding(bottom = 6.dp)) {
                                                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Videocam, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("Medical Video Attached", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                                                    }
                                                }
                                            }

                                            Text(
                                                text = msg.text,
                                                fontSize = 12.sp,
                                                color = if (isUser) Color.White else TextPrimary
                                            )

                                            if (msg.urgencyLevel != null) {
                                                Spacer(Modifier.height(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = when (msg.urgencyLevel) {
                                                        "EMERGENCY" -> EmergencyRed
                                                        "HIGH" -> WarningAmber
                                                        else -> SecondaryTeal
                                                    }
                                                ) {
                                                    Text(
                                                        "Urgency: ${msg.urgencyLevel}",
                                                        color = Color.White,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            if (msg.suggestedDoctors.isNotEmpty()) {
                                                Spacer(Modifier.height(8.dp))
                                                Text("Suggested Specialists:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                                msg.suggestedDoctors.forEach { doc ->
                                                    Text("• ${doc.name} (${doc.specialization})", fontSize = 11.sp, color = TextSecondary)
                                                }
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(msg.timestamp, fontSize = 9.sp, color = TextMuted)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Attachment indicator pills
                    if (attachedPhoto != null || attachedVideo != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 6.dp)) {
                            attachedPhoto?.let {
                                InputChip(
                                    selected = true,
                                    onClick = { attachedPhoto = null },
                                    label = { Text("Photo Attached", fontSize = 10.sp) },
                                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                )
                            }
                            attachedVideo?.let {
                                InputChip(
                                    selected = true,
                                    onClick = { attachedVideo = null },
                                    label = { Text("Video Attached", fontSize = 10.sp) },
                                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                )
                            }
                        }
                    }

                    // Keyboard-aware Bottom Input Field Bar with Photo/Video Triggers
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CardSurface,
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { attachedPhoto = "sample_photo.jpg" }) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Attach Photo", tint = PrimaryBlue)
                            }

                            IconButton(onClick = { attachedVideo = "sample_video.mp4" }) {
                                Icon(Icons.Default.Videocam, contentDescription = "Attach Video", tint = SecondaryTeal)
                            }

                            OutlinedTextField(
                                value = inputQuery,
                                onValueChange = { inputQuery = it },
                                placeholder = { Text("Describe symptoms...", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(20.dp),
                                singleLine = true
                            )

                            Spacer(Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    if (inputQuery.isNotBlank() || attachedPhoto != null || attachedVideo != null) {
                                        viewModel.sendGeminiChatMessage(inputQuery, attachedPhoto, attachedVideo)
                                        inputQuery = ""
                                        attachedPhoto = null
                                        attachedVideo = null
                                    }
                                },
                                enabled = !chatSending
                            ) {
                                if (chatSending) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = "Send", tint = PrimaryBlue)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 15. Crowd Trend Chart
@Composable
fun CrowdTrendChartScreen(
    viewModel: SmartCareViewModel
) {
    val selHospital by viewModel.selectedHospital.collectAsState()
    val hospitals by viewModel.hospitals.collectAsState()
    val hospital = selHospital ?: hospitals.first()
    val crowdData = viewModel.repository.getCrowdDataForHospital(hospital.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text(
            "Crowd Trend Analytics",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Text(
            hospital.name,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Historical Daily Patient Volume",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Simulated Bar Chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    crowdData.hourlyTrends.forEach { trend ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height((trend.crowdPercentage * 1.2).dp)
                                    .background(
                                        if (trend.isOptimalWindow) SecondaryTeal
                                        else PrimaryBlue,
                                        RoundedCornerShape(
                                            topStart = 4.dp,
                                            topEnd = 4.dp
                                        )
                                    )
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                trend.timeLabel.take(2),
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// 16. Hospital Data Sources
@Composable
fun HospitalDataSourcesScreen(
    viewModel: SmartCareViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text(
            "Hospital Data Sources & Transparency",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Text(
            "How SmartCareAI ensures accurate healthcare data",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(Modifier.height(16.dp))

        val sources = listOf(
            Triple(
                "Live Hospital Queue API",
                "Direct integration with hospital EHR/HIS queue engines",
                "LIVE"
            ),
            Triple(
                "AI Predictive Model",
                "Statistical machine learning estimation for un-integrated clinics",
                "ESTIMATED"
            ),
            Triple(
                "Patient Check-in Telemetry",
                "Anonymized location & queue token updates from app users",
                "LIVE"
            ),
            Triple(
                "Demo / Sandbox Simulation Data",
                "Clearly marked sample data when no live backend connection exists",
                "DEMO"
            )
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(sources) { (title, desc, badge) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                desc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (badge == "LIVE") TealLight
                            else SurfaceLightBlue
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badge == "LIVE") SecondaryTeal
                                else PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}