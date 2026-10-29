package com.example.smartcareai.ui.screens.crowd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
        Text("Live Crowd Monitoring", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Real-time department capacity & queue occupancy", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

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
                        Text(hospital.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Updated ${crowdData.lastUpdated}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                    CrowdBadge(crowdLevel = crowdData.crowdLevel)
                }

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(shape = RoundedCornerShape(12.dp), color = SurfaceLightBlue, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("People Waiting", fontSize = 11.sp, color = TextSecondary)
                            Text("${crowdData.peopleWaiting}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }

                    Surface(shape = RoundedCornerShape(12.dp), color = TealLight, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Est Wait Range", fontSize = 11.sp, color = TextSecondary)
                            Text(crowdData.estimatedWaitRange, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SecondaryTeal)
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

// 14. Waiting-Time Prediction
@Composable
fun WaitingTimePredictionScreen(
    viewModel: SmartCareViewModel
) {
    val selHospital by viewModel.selectedHospital.collectAsState()
    val hospitals by viewModel.hospitals.collectAsState()
    val hospital = selHospital ?: hospitals.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Wait-Time Prediction", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Predictive waiting analytics powered by SmartCareAI", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLightBlue)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ESTIMATED CONSULTATION DELAY", style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue, fontWeight = FontWeight.Bold))
                Text("12 - 18 Mins", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = PrimaryBlue))
                Text("High confidence prediction based on active registration logs", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("How This Estimate Was Calculated", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(8.dp))
                Text("• Active patients currently checked-in: 14", fontSize = 12.sp, color = TextSecondary)
                Text("• Average doctor consultation pace: 8 mins/patient", fontSize = 12.sp, color = TextSecondary)
                Text("• Live emergency triage count: 1 urgent case", fontSize = 12.sp, color = TextSecondary)
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
        Text("Crowd Trend Analytics", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text(hospital.name, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Historical Daily Patient Volume", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
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
                                        if (trend.isOptimalWindow) SecondaryTeal else PrimaryBlue,
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                    )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(trend.timeLabel.take(2), fontSize = 10.sp, color = TextSecondary)
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
        Text("Hospital Data Sources & Transparency", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("How SmartCareAI ensures accurate healthcare data", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        val sources = listOf(
            Triple("Live Hospital Queue API", "Direct integration with hospital EHR/HIS queue engines", "LIVE"),
            Triple("AI Predictive Model", "Statistical machine learning estimation for un-integrated clinics", "ESTIMATED"),
            Triple("Patient Check-in Telemetry", "Anonymized location & queue token updates from app users", "LIVE"),
            Triple("Demo / Sandbox Simulation Data", "Clearly marked sample data when no live backend connection exists", "DEMO")
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
                            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (badge == "LIVE") TealLight else SurfaceLightBlue
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badge == "LIVE") SecondaryTeal else PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}
