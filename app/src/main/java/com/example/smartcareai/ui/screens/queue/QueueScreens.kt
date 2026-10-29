package com.example.smartcareai.ui.screens.queue

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcareai.ui.components.QueueTokenCard
import com.example.smartcareai.ui.theme.*
import com.example.smartcareai.viewmodel.SmartCareViewModel

// 24. Queue Token Assignment
@Composable
fun QueueTokenAssignmentScreen(
    viewModel: SmartCareViewModel,
    onTrackClick: () -> Unit
) {
    val activeToken by viewModel.activeQueueToken.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Text("Live Queue Token Generated", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Your position in line has been assigned automatically", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(20.dp))

        if (activeToken != null) {
            QueueTokenCard(
                token = activeToken!!,
                onRefresh = { viewModel.refreshQueue() },
                onViewUpdates = onTrackClick
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Text("No active queue token found.", modifier = Modifier.padding(20.dp), color = TextSecondary)
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onTrackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Go to Live Queue Tracker", fontWeight = FontWeight.Bold)
        }
    }
}

// 25. Live Queue Tracking
@Composable
fun LiveQueueTrackingScreen(
    viewModel: SmartCareViewModel,
    onViewLogClick: () -> Unit
) {
    val activeToken by viewModel.activeQueueToken.collectAsState()

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
                Text("Live Queue Tracker", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text("Monitored in real-time by SmartCareAI", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }

            IconButton(onClick = { viewModel.refreshQueue() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PrimaryBlue)
            }
        }

        Spacer(Modifier.height(16.dp))

        if (activeToken != null) {
            QueueTokenCard(
                token = activeToken!!,
                onRefresh = { viewModel.refreshQueue() },
                onViewUpdates = onViewLogClick
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Queue Instructions", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(8.dp))
                    Text("• Please remain within 100m of the hospital waiting lounge.", fontSize = 12.sp, color = TextSecondary)
                    Text("• Audio chime and vibration will signal when 2 patients remain ahead.", fontSize = 12.sp, color = TextSecondary)
                    Text("• Have your Digital Ref Code ready for room entry.", fontSize = 12.sp, color = TextSecondary)
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No Active Queue Token", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Book an appointment or register at clinic triage to get a token.", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                }
            }
        }
    }
}

// 26. Waiting-Time Updates
@Composable
fun WaitingTimeUpdatesScreen(
    viewModel: SmartCareViewModel
) {
    val activeToken by viewModel.activeQueueToken.collectAsState()

    val logs = listOf(
        Pair("10:15 AM", "Queue position moved to #3 ahead. Estimated wait reduced by 5 mins."),
        Pair("10:02 AM", "Dr. Amanda Richardson completed previous patient consultation."),
        Pair("09:45 AM", "Queue Token A-104 assigned. Initial estimated wait: 25 mins.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Wait-Time Log", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Token: ${activeToken?.tokenNumber ?: "A-104"}", style = MaterialTheme.typography.bodySmall.copy(color = PrimaryBlue, fontWeight = FontWeight.Bold))

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(logs) { (time, msg) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Surface(shape = RoundedCornerShape(6.dp), color = SurfaceLightBlue) {
                            Text(time, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(msg, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary), modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// 27. Queue Notifications
@Composable
fun QueueNotificationsScreen(
    viewModel: SmartCareViewModel
) {
    val notifs by viewModel.notifications.collectAsState()
    val queueNotifs = notifs.filter { it.category == "Queue" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Queue Alerts", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Live alerts for active queue movements", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(queueNotifs) { n ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(n.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(n.timestamp, fontSize = 11.sp, color = TextSecondary)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(n.message, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
