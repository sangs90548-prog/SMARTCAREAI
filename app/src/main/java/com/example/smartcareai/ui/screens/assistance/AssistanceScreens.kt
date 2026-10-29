package com.example.smartcareai.ui.screens.assistance

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcareai.ui.components.FAQAccordion
import com.example.smartcareai.ui.theme.*
import com.example.smartcareai.viewmodel.SmartCareViewModel

// 28. Accessibility Assistance
@Composable
fun AccessibilityAssistanceScreen(
    viewModel: SmartCareViewModel
) {
    val largeText by viewModel.largeTextEnabled.collectAsState()
    val mobilityAssist by viewModel.mobilityAssist.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Accessibility Options", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Customize display and mobility support preferences", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Large Text Preference", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("Increase text scale for high legibility", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                    Switch(checked = largeText, onCheckedChange = { viewModel.setLargeTextEnabled(it) })
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Wheelchair / Mobility Assist", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("Request ramp and porter support upon clinic entry", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                    Switch(checked = mobilityAssist, onCheckedChange = { viewModel.setMobilityAssistSetting(it) })
                }
            }
        }
    }
}

// 29. Emergency Guide
@Composable
fun EmergencyGuideScreen(
    viewModel: SmartCareViewModel
) {
    val context = LocalContext.current
    val emergencyNumber = "+1 (800) 555-0199"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmergencyRed)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(64.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Emergency, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("EMERGENCY 24/7 HOTLINE", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                Text(emergencyNumber, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color.White))

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$emergencyNumber"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = EmergencyRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Call Emergency Response Now", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Emergency Triage Instructions", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("1. Chest Pain / Stroke Symptoms: Call 911 or proceed immediately to nearest ER.", fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text("2. Severe Bleeding or Fracture: Keep patient calm, apply pressure, notify ER desk on arrival.", fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text("3. High Fever in Infants: Direct registration at Pediatric ER counter.", fontSize = 13.sp)
            }
        }
    }
}

// 30. Patient Profile
@Composable
fun PatientProfileScreen(
    viewModel: SmartCareViewModel
) {
    val profile by viewModel.patientProfile.collectAsState()

    var name by remember { mutableStateOf(profile.name) }
    var phone by remember { mutableStateOf(profile.phone) }
    var email by remember { mutableStateOf(profile.email) }
    var emergencyContact by remember { mutableStateOf(profile.emergencyContact) }
    var bloodType by remember { mutableStateOf(profile.bloodType) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Patient Profile", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("View and update personal medical profile details", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = emergencyContact, onValueChange = { emergencyContact = it }, label = { Text("Emergency Contact Phone") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = bloodType, onValueChange = { bloodType = it }, label = { Text("Blood Group") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.updatePatientProfile(name, phone, email, emergencyContact, bloodType)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Save Profile Updates", fontWeight = FontWeight.Bold)
        }
    }
}

// 31. Settings and Privacy
@Composable
fun SettingsPrivacyScreen(
    viewModel: SmartCareViewModel,
    onNavigateToAccessibility: () -> Unit
) {
    val notifEnabled by viewModel.notificationsEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Settings & Privacy", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Application preferences & data controls", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Push Notifications", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Switch(checked = notifEnabled, onCheckedChange = { viewModel.setNotificationsEnabled(it) })
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAccessibility() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Accessibility Settings", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}

// 32. Help Center
@Composable
fun HelpCenterScreen(
    onNavigateToFaqs: () -> Unit,
    onNavigateToSupport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Help Center", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("How can we assist you today?", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { onNavigateToFaqs() },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = PrimaryBlue)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Frequently Asked Questions", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Explore instant answers to common questions", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { onNavigateToSupport() },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = SecondaryTeal)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Contact Patient Support", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Submit a ticket to SmartCareAI support team", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }
    }
}

// 33. FAQs
@Composable
fun FaqsScreen(
    viewModel: SmartCareViewModel
) {
    val faqs by viewModel.faqs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Frequently Asked Questions", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Searchable answers to app features & clinic workflows", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(faqs) { faq ->
                FAQAccordion(faq = faq, onToggle = { viewModel.toggleFaqExpand(faq.id) })
            }
        }
    }
}

// 34. Contact Support
@Composable
fun ContactSupportScreen(
    viewModel: SmartCareViewModel,
    onSubmitted: () -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Appointments & Queue") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Text("Contact Support", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text("Send a message to our 24/7 patient support team", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Describe your query or issue...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (subject.isNotBlank() && message.isNotBlank()) {
                    val p = viewModel.patientProfile.value
                    viewModel.submitSupport(p.name, p.email, category, subject, message)
                    onSubmitted()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Submit Support Ticket", fontWeight = FontWeight.Bold)
        }
    }
}

// 35. Notifications Center
@Composable
fun NotificationsCenterScreen(
    viewModel: SmartCareViewModel,
    onNotificationTargetClick: (String) -> Unit
) {
    val notifs by viewModel.notifications.collectAsState()

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
                Text("Notifications Center", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text("Alerts, queue updates, and appointment reminders", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }

            TextButton(onClick = { viewModel.clearNotifications() }) {
                Text("Clear All")
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notifs) { n ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.markNotificationRead(n.id)
                            if (n.targetRoute.isNotBlank()) {
                                onNotificationTargetClick(n.targetRoute)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (n.isRead) CardSurface else SurfaceLightBlue)
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
