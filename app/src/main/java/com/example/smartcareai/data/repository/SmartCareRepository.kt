package com.example.smartcareai.data.repository

import com.example.smartcareai.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SmartCareRepository {

    // Initial Hospitals Dataset (India-Wide Verified Dataset across 12 States / UTs)
    private val initialHospitals = listOf(
        Hospital(
            id = "HOSP-IN-001",
            name = "Apollo Hospitals Greams Road",
            category = "Multispecialty Tertiary Care",
            rating = 4.90,
            reviewCount = 1840,
            distanceKm = 1.8,
            address = "21 Greams Lane, Thousand Lights",
            area = "Thousand Lights",
            city = "Chennai",
            state = "Tamil Nadu",
            postalCode = "600006",
            facilities = listOf("24/7 Emergency & Trauma", "Robotic Surgery", "Proton Therapy", "24/7 Pharmacy", "Organ Transplant Wing"),
            departments = listOf("Cardiology", "Neurology", "Orthopedics", "Oncology", "Emergency Medicine"),
            imageUrl = "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc",
            bestTimeToVisit = "08:30 AM - 10:30 AM (Low Wait)",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 15,
            liveBedAvailability = 42,
            emergencyContact = "+91 44 2829 3333",
            latitude = 13.060422,
            longitude = 80.251244,
            isRecommended = true,
            recommendationReason = "Top recommendation in Tamil Nadu for cardiology and lowest waiting time"
        ),
        Hospital(
            id = "HOSP-IN-002",
            name = "Kovai Medical Center and Hospital (KMCH)",
            category = "Multi Super Specialty Center",
            rating = 4.85,
            reviewCount = 1250,
            distanceKm = 4.2,
            address = "99 Avinashi Road, Civil Aerodrome Post",
            area = "Avinashi Road",
            city = "Coimbatore",
            state = "Tamil Nadu",
            postalCode = "641014",
            facilities = listOf("Level 1 Trauma Center", "Cath Lab", "Pediatric ICU", "Subsidized Transport", "Helipad"),
            departments = listOf("Cardiology", "Orthopedics", "Nephrology", "Gastroenterology", "Pediatrics"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 20,
            liveBedAvailability = 35,
            emergencyContact = "+91 422 2627784",
            latitude = 11.028340,
            longitude = 77.026410,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-003",
            name = "Christian Medical College (CMC)",
            category = "Specialty & Academic Tertiary Hospital",
            rating = 4.95,
            reviewCount = 2450,
            distanceKm = 8.5,
            address = "Ida Scudder Road",
            area = "Vellore Town",
            city = "Vellore",
            state = "Tamil Nadu",
            postalCode = "632004",
            facilities = listOf("Bone Marrow Transplant Unit", "24/7 Emergency", "Advanced Pathology", "Blood Bank"),
            departments = listOf("Hematology", "Gastroenterology", "Neurology", "Endocrinology", "General Surgery"),
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3",
            bestTimeToVisit = "07:30 AM - 10:00 AM",
            crowdLevel = CrowdLevel.HIGH,
            averageWaitMinutes = 40,
            liveBedAvailability = 15,
            emergencyContact = "+91 416 228 2000",
            latitude = 12.923890,
            longitude = 79.135060,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-004",
            name = "Rajiv Gandhi Government General Hospital (RGGGH)",
            category = "Government Apex Teaching Hospital",
            rating = 4.55,
            reviewCount = 980,
            distanceKm = 2.4,
            address = "EVR Periyar Salai, Park Town",
            area = "Park Town",
            city = "Chennai",
            state = "Tamil Nadu",
            postalCode = "600003",
            facilities = listOf("24/7 Free ER Triage", "Dialysis Unit", "Burn Care Wing", "Blood Bank"),
            departments = listOf("General Medicine", "General Surgery", "Cardiology", "Emergency Medicine"),
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09",
            bestTimeToVisit = "08:00 AM - 11:00 AM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 25,
            liveBedAvailability = 60,
            emergencyContact = "+91 44 2530 5111",
            latitude = 13.081820,
            longitude = 80.277830,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-005",
            name = "Manipal Hospital Old Airport Road",
            category = "Multispecialty Super Specialty Center",
            rating = 4.88,
            reviewCount = 1620,
            distanceKm = 3.1,
            address = "98 HAL 2nd Stage, Kodihalli, Old Airport Road",
            area = "HAL Old Airport Road",
            city = "Bengaluru",
            state = "Karnataka",
            postalCode = "560017",
            facilities = listOf("24/7 ER", "Advanced Cardiac ICU", "Organ Transplant Suite", "Robotic Surgery"),
            departments = listOf("Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Oncology"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 18,
            liveBedAvailability = 38,
            emergencyContact = "+91 80 2502 3333",
            latitude = 12.958230,
            longitude = 77.648710,
            isRecommended = true,
            recommendationReason = "Top rated multispecialty center in Bengaluru with fast queue processing"
        ),
        Hospital(
            id = "HOSP-IN-006",
            name = "Narayana Health City",
            category = "Cardiac & Super Specialty Institute",
            rating = 4.90,
            reviewCount = 2100,
            distanceKm = 12.0,
            address = "258/A Bommasandra Industrial Area, Anekal Taluk",
            area = "Bommasandra",
            city = "Bengaluru",
            state = "Karnataka",
            postalCode = "560099",
            facilities = listOf("Pediatric Cardiac Care", "Bone Marrow Unit", "Cancer Institute", "24/7 Pharmacy"),
            departments = listOf("Cardiology", "Cardiothoracic Surgery", "Oncology", "Nephrology"),
            imageUrl = "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc",
            bestTimeToVisit = "08:00 AM - 10:30 AM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 25,
            liveBedAvailability = 50,
            emergencyContact = "+91 80 7122 3333",
            latitude = 12.808380,
            longitude = 77.697420,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-007",
            name = "Fortis Hospital Bannerghatta Road",
            category = "Multi Super Specialty Hospital",
            rating = 4.75,
            reviewCount = 1140,
            distanceKm = 5.8,
            address = "154/9 Bannerghatta Road, Opp IIM-B",
            area = "Bannerghatta Road",
            city = "Bengaluru",
            state = "Karnataka",
            postalCode = "560076",
            facilities = listOf("Cath Lab", "Neuro Critical Care", "Dialysis Suite", "24/7 ER"),
            departments = listOf("Neurology", "Orthopedics", "Urology", "Cardiology"),
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3",
            bestTimeToVisit = "10:00 AM - 01:00 PM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 20,
            liveBedAvailability = 28,
            emergencyContact = "+91 80 6621 4000",
            latitude = 12.894350,
            longitude = 77.598640,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-008",
            name = "Lilavati Hospital and Research Centre",
            category = "Super Specialty Hospital & Research Hub",
            rating = 4.88,
            reviewCount = 1950,
            distanceKm = 4.5,
            address = "A-791 Bandra Reclamation, Bandra West",
            area = "Bandra West",
            city = "Mumbai",
            state = "Maharashtra",
            postalCode = "400050",
            facilities = listOf("Advanced Cardiac ICU", "Robotic Surgery", "Neuro Cath Lab", "24/7 Trauma"),
            departments = listOf("Cardiology", "Neurology", "Gastroenterology", "Orthopedics"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "08:30 AM - 11:00 AM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 22,
            liveBedAvailability = 30,
            emergencyContact = "+91 22 2675 1111",
            latitude = 19.051410,
            longitude = 72.828820,
            isRecommended = true,
            recommendationReason = "Premier super specialty center in Western India"
        ),
        Hospital(
            id = "HOSP-IN-009",
            name = "Tata Memorial Hospital",
            category = "Premier Cancer Care & Research Center",
            rating = 4.92,
            reviewCount = 2800,
            distanceKm = 6.2,
            address = "Dr. Ernest Borges Road, Parel",
            area = "Parel",
            city = "Mumbai",
            state = "Maharashtra",
            postalCode = "400012",
            facilities = listOf("Radiation Oncology Suite", "Bone Marrow Transplant", "Proton Therapy", "Clinical Research"),
            departments = listOf("Oncology", "Radiation Therapy", "Surgical Oncology", "Hematology"),
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3",
            bestTimeToVisit = "07:00 AM - 09:30 AM",
            crowdLevel = CrowdLevel.HIGH,
            averageWaitMinutes = 35,
            liveBedAvailability = 18,
            emergencyContact = "+91 22 2417 7001",
            latitude = 19.002820,
            longitude = 72.842780,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-010",
            name = "Kokilaben Dhirubhai Ambani Hospital",
            category = "Quaternary Care Multi Specialty Hospital",
            rating = 4.89,
            reviewCount = 1420,
            distanceKm = 7.8,
            address = "Rao Saheb Achutrao Patwardhan Marg, Four Bungalows, Andheri West",
            area = "Andheri West",
            city = "Mumbai",
            state = "Maharashtra",
            postalCode = "400053",
            facilities = listOf("Robotic Surgery Suite", "Intraoperative MRI", "Children Heart Center", "24/7 ER"),
            departments = listOf("Cardiology", "Pediatrics", "Neurology", "Orthopedics"),
            imageUrl = "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc",
            bestTimeToVisit = "09:30 AM - 12:00 PM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 18,
            liveBedAvailability = 45,
            emergencyContact = "+91 22 4269 9999",
            latitude = 19.131230,
            longitude = 72.825210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-011",
            name = "Ruby Hall Clinic",
            category = "Multispecialty Super Specialty Hospital",
            rating = 4.82,
            reviewCount = 1180,
            distanceKm = 3.2,
            address = "40 Sassoon Road, Sangamvadi",
            area = "Sassoon Road",
            city = "Pune",
            state = "Maharashtra",
            postalCode = "411001",
            facilities = listOf("Organ Transplant Center", "Cardiac Cath Lab", "Neuro Intensive Care", "24/7 Pharmacy"),
            departments = listOf("Cardiology", "Neurology", "Nephrology", "General Surgery"),
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 20,
            liveBedAvailability = 32,
            emergencyContact = "+91 20 6645 5200",
            latitude = 18.532340,
            longitude = 73.873210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-012",
            name = "All India Institute of Medical Sciences (AIIMS)",
            category = "Apex National Public Medical Institute",
            rating = 4.95,
            reviewCount = 3200,
            distanceKm = 4.8,
            address = "Sri Aurobindo Marg, Ansari Nagar",
            area = "Ansari Nagar",
            city = "New Delhi",
            state = "Delhi",
            postalCode = "110029",
            facilities = listOf("National Emergency & Trauma Block", "Apex Heart Center", "Cancer Institute", "Organ Transplant Wing"),
            departments = listOf("Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Emergency Medicine", "Oncology"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "07:00 AM - 09:30 AM",
            crowdLevel = CrowdLevel.HIGH,
            averageWaitMinutes = 30,
            liveBedAvailability = 80,
            emergencyContact = "+91 11 2658 8700",
            latitude = 28.567210,
            longitude = 77.210020,
            isRecommended = true,
            recommendationReason = "Top national public hospital with complete multispecialty care"
        ),
        Hospital(
            id = "HOSP-IN-013",
            name = "Max Super Speciality Hospital Saket",
            category = "Multi Super Specialty Center",
            rating = 4.86,
            reviewCount = 1750,
            distanceKm = 6.1,
            address = "1 2 Press Enclave Road, Saket",
            area = "Saket",
            city = "New Delhi",
            state = "Delhi",
            postalCode = "110017",
            facilities = listOf("Max Heart & Vascular Institute", "Bone Marrow Suite", "Da Vinci Robotic System", "24/7 ER"),
            departments = listOf("Cardiology", "Oncology", "Orthopedics", "Pulmonology"),
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 25,
            liveBedAvailability = 35,
            emergencyContact = "+91 11 2651 5151",
            latitude = 28.528340,
            longitude = 77.211420,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-014",
            name = "Medanta - The Medicity",
            category = "Multi Super Specialty Institute",
            rating = 4.91,
            reviewCount = 2300,
            distanceKm = 15.0,
            address = "CH Baktawar Singh Road, Sector 38",
            area = "Sector 38",
            city = "Gurugram",
            state = "Haryana",
            postalCode = "122001",
            facilities = listOf("Heart Institute", "Kidney & Urology Institute", "CyberKnife Radiosurgery", "Air Ambulance"),
            departments = listOf("Cardiology", "Urology", "Neurology", "Orthopedics"),
            imageUrl = "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc",
            bestTimeToVisit = "08:30 AM - 11:00 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 18,
            liveBedAvailability = 65,
            emergencyContact = "+91 124 414 1111",
            latitude = 28.438210,
            longitude = 77.042310,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-015",
            name = "Fortis Escorts Heart Institute",
            category = "Premier Cardiac Specialty Center",
            rating = 4.88,
            reviewCount = 1530,
            distanceKm = 7.2,
            address = "Okhla Road, Opp Holy Family Hospital",
            area = "Okhla",
            city = "New Delhi",
            state = "Delhi",
            postalCode = "110025",
            facilities = listOf("Dedicated Cardiac ER", "Pediatric Cardiology", "3D Cath Lab", "Cardiac Rehab"),
            departments = listOf("Cardiology", "Cardiothoracic Surgery", "Pediatric Cardiology"),
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 15,
            liveBedAvailability = 25,
            emergencyContact = "+91 11 4713 5555",
            latitude = 28.560410,
            longitude = 77.272110,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-016",
            name = "Yashoda Hospitals Somajiguda",
            category = "Multi Super Specialty Center",
            rating = 4.84,
            reviewCount = 1320,
            distanceKm = 2.8,
            address = "Raj Bhavan Road, Somajiguda",
            area = "Somajiguda",
            city = "Hyderabad",
            state = "Telangana",
            postalCode = "500082",
            facilities = listOf("Level 1 Emergency Care", "Heart & Lung Transplant", "Surgical Oncology", "24/7 Pharmacy"),
            departments = listOf("Cardiology", "Neurology", "Gastroenterology", "Oncology"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "08:30 AM - 11:00 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 18,
            liveBedAvailability = 40,
            emergencyContact = "+91 40 4567 9999",
            latitude = 17.424310,
            longitude = 78.458210,
            isRecommended = true,
            recommendationReason = "Top multi specialty medical provider in Hyderabad region"
        ),
        Hospital(
            id = "HOSP-IN-017",
            name = "KIMS Hospitals Secunderabad",
            category = "Multi Super Specialty Hospital",
            rating = 4.81,
            reviewCount = 1080,
            distanceKm = 4.5,
            address = "1-8-31/1 Minister Road, Krishna Nagar Colony",
            area = "Secunderabad",
            city = "Hyderabad",
            state = "Telangana",
            postalCode = "500003",
            facilities = listOf("Comprehensive Cancer Center", "Joint Replacement Unit", "Dialysis Suite", "24/7 ER"),
            departments = listOf("Orthopedics", "Oncology", "Nephrology", "General Surgery"),
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3",
            bestTimeToVisit = "09:30 AM - 12:00 PM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 25,
            liveBedAvailability = 30,
            emergencyContact = "+91 40 4488 5111",
            latitude = 17.436210,
            longitude = 78.489110,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-018",
            name = "Aster Medcity",
            category = "Quaternary Care Healthcare Destination",
            rating = 4.90,
            reviewCount = 1720,
            distanceKm = 6.8,
            address = "Kuttisahib Road, Cheranalloor",
            area = "Cheranalloor",
            city = "Kochi",
            state = "Kerala",
            postalCode = "682027",
            facilities = listOf("Minimal Access Surgery", "Astercare ER", "Pediatric Super Specialty", "Helipad Access"),
            departments = listOf("Cardiology", "Neurology", "Pediatrics", "Oncology", "Gastroenterology"),
            imageUrl = "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc",
            bestTimeToVisit = "08:30 AM - 11:00 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 15,
            liveBedAvailability = 50,
            emergencyContact = "+91 484 669 0000",
            latitude = 10.046210,
            longitude = 76.268310,
            isRecommended = true,
            recommendationReason = "High efficiency healthcare destination in Kerala with lowest wait time"
        ),
        Hospital(
            id = "HOSP-IN-019",
            name = "Amrita Institute of Medical Sciences (AIMS)",
            category = "Super Specialty Academic Healthcare Campus",
            rating = 4.92,
            reviewCount = 2150,
            distanceKm = 8.2,
            address = "Amrita Lane, AIMS P.O., Edappally",
            area = "Edappally",
            city = "Kochi",
            state = "Kerala",
            postalCode = "682041",
            facilities = listOf("Robotic Surgery Suite", "Organ Transplant Center", "CyberKnife Radiation", "24/7 Emergency"),
            departments = listOf("Cardiology", "Oncology", "Neurology", "Nephrology", "General Medicine"),
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09",
            bestTimeToVisit = "08:00 AM - 10:30 AM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 25,
            liveBedAvailability = 42,
            emergencyContact = "+91 484 280 1234",
            latitude = 10.032110,
            longitude = 76.298210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-020",
            name = "Government Medical College Thiruvananthapuram",
            category = "State Apex Teaching Medical Center",
            rating = 4.60,
            reviewCount = 890,
            distanceKm = 3.4,
            address = "Medical College PO, Chalakkuzhi",
            area = "Chalakkuzhi",
            city = "Thiruvananthapuram",
            state = "Kerala",
            postalCode = "695011",
            facilities = listOf("24/7 Trauma Triage", "Burn ICU", "Free Public Dialysis", "Blood Bank"),
            departments = listOf("General Medicine", "General Surgery", "Pediatrics", "Emergency Medicine"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "08:00 AM - 11:00 AM",
            crowdLevel = CrowdLevel.HIGH,
            averageWaitMinutes = 35,
            liveBedAvailability = 75,
            emergencyContact = "+91 471 252 8400",
            latitude = 8.524310,
            longitude = 76.928210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-021",
            name = "AMRI Hospital Dhakuria",
            category = "Multi Super Specialty Hospital",
            rating = 4.78,
            reviewCount = 960,
            distanceKm = 4.1,
            address = "P-238 Block A, Scheme LXVIII, Dhakuria",
            area = "Dhakuria",
            city = "Kolkata",
            state = "West Bengal",
            postalCode = "700031",
            facilities = listOf("Level 3 Emergency", "Interventional Cardiology", "Joint Reconstruction", "24/7 Pharmacy"),
            departments = listOf("Cardiology", "Orthopedics", "Gastroenterology", "Neurology"),
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 20,
            liveBedAvailability = 26,
            emergencyContact = "+91 33 6680 1111",
            latitude = 22.512310,
            longitude = 88.368210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-022",
            name = "Apollo Gleneagles Hospitals Kolkata",
            category = "Multispecialty Tertiary Facility",
            rating = 4.85,
            reviewCount = 1420,
            distanceKm = 5.5,
            address = "58 Canal Circular Road, Kadapara",
            area = "Kadapara",
            city = "Kolkata",
            state = "West Bengal",
            postalCode = "700054",
            facilities = listOf("Comprehensive Cancer Center", "Organ Transplant Suite", "Advanced Cath Lab", "24/7 ER"),
            departments = listOf("Cardiology", "Oncology", "Neurology", "Pediatrics"),
            imageUrl = "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc",
            bestTimeToVisit = "08:30 AM - 11:00 AM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 24,
            liveBedAvailability = 34,
            emergencyContact = "+91 33 2320 2122",
            latitude = 22.578210,
            longitude = 88.399210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-023",
            name = "Zydus Hospital Thaltej",
            category = "Quaternary Multi Specialty Hospital",
            rating = 4.87,
            reviewCount = 1280,
            distanceKm = 3.8,
            address = "Zydus Hospital Road, Near Sola Bridge, SG Highway",
            area = "Thaltej",
            city = "Ahmedabad",
            state = "Gujarat",
            postalCode = "380054",
            facilities = listOf("Level 1 Trauma Center", "Cardiac Cath Lab", "Robotic Surgery", "24/7 Pharmacy"),
            departments = listOf("Cardiology", "Neurology", "Orthopedics", "Urology"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 18,
            liveBedAvailability = 36,
            emergencyContact = "+91 79 6670 1111",
            latitude = 23.058210,
            longitude = 72.518210,
            isRecommended = true,
            recommendationReason = "Leading quaternary care hospital in Gujarat"
        ),
        Hospital(
            id = "HOSP-IN-024",
            name = "Sawai Man Singh (SMS) Hospital",
            category = "State Government Apex Teaching Hospital",
            rating = 4.65,
            reviewCount = 1850,
            distanceKm = 2.5,
            address = "JLN Marg, Ashok Nagar",
            area = "Ashok Nagar",
            city = "Jaipur",
            state = "Rajasthan",
            postalCode = "302004",
            facilities = listOf("24/7 Public ER Triage", "Advanced Burn ICU", "Dialysis Center", "Blood Bank"),
            departments = listOf("General Medicine", "General Surgery", "Orthopedics", "Emergency Medicine"),
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09",
            bestTimeToVisit = "08:00 AM - 11:00 AM",
            crowdLevel = CrowdLevel.HIGH,
            averageWaitMinutes = 35,
            liveBedAvailability = 90,
            emergencyContact = "+91 141 256 0292",
            latitude = 26.898210,
            longitude = 75.818210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-025",
            name = "Sanjay Gandhi Postgraduate Institute of Medical Sciences (SGPGI)",
            category = "Apex Super Specialty Institute",
            rating = 4.91,
            reviewCount = 2100,
            distanceKm = 9.2,
            address = "Raebareli Road",
            area = "Raebareli Road",
            city = "Lucknow",
            state = "Uttar Pradesh",
            postalCode = "226014",
            facilities = listOf("Apex Trauma Center", "Gastroenterology Institute", "Renal Transplant Wing", "Advanced Pathology"),
            departments = listOf("Gastroenterology", "Nephrology", "Cardiology", "Neurology"),
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3",
            bestTimeToVisit = "08:00 AM - 10:30 AM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 25,
            liveBedAvailability = 40,
            emergencyContact = "+91 522 266 8800",
            latitude = 26.748210,
            longitude = 80.938210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-026",
            name = "AIIMS Patna",
            category = "National Institute of Medical Excellence",
            rating = 4.80,
            reviewCount = 1150,
            distanceKm = 7.5,
            address = "Phulwari Sharif",
            area = "Phulwari Sharif",
            city = "Patna",
            state = "Bihar",
            postalCode = "801507",
            facilities = listOf("24/7 ER Block", "Trauma Center", "Advanced Diagnostic Suite", "Pediatric ICU"),
            departments = listOf("General Medicine", "Cardiology", "Pediatrics", "Orthopedics"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "08:30 AM - 11:00 AM",
            crowdLevel = CrowdLevel.MODERATE,
            averageWaitMinutes = 28,
            liveBedAvailability = 55,
            emergencyContact = "+91 612 245 1111",
            latitude = 25.562310,
            longitude = 85.048210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-027",
            name = "Kiran Multi Super Specialty Hospital",
            category = "Tertiary Healthcare Institute",
            rating = 4.84,
            reviewCount = 940,
            distanceKm = 5.2,
            address = "Vasta Devdi Road, Near Sumul Dairy, Katargam",
            area = "Katargam",
            city = "Surat",
            state = "Gujarat",
            postalCode = "395004",
            facilities = listOf("Cath Lab", "Joint Replacement Suite", "Level 3 ICU", "24/7 ER"),
            departments = listOf("Cardiology", "Orthopedics", "Urology", "Pediatrics"),
            imageUrl = "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 20,
            liveBedAvailability = 30,
            emergencyContact = "+91 261 716 9999",
            latitude = 21.228210,
            longitude = 72.828210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-028",
            name = "Baby Memorial Hospital",
            category = "Multi Super Specialty Healthcare Hub",
            rating = 4.83,
            reviewCount = 1020,
            distanceKm = 3.9,
            address = "Indira Gandhi Road, Arayidathupalam",
            area = "Arayidathupalam",
            city = "Kozhikode",
            state = "Kerala",
            postalCode = "673004",
            facilities = listOf("Advanced Cardiac Care", "Trauma Suite", "Neonatal ICU", "24/7 Pharmacy"),
            departments = listOf("Pediatrics", "Cardiology", "Neurology", "Orthopedics"),
            imageUrl = "https://images.unsplash.com/photo-1516549655169-df83a0774514",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 18,
            liveBedAvailability = 28,
            emergencyContact = "+91 495 246 8111",
            latitude = 11.258210,
            longitude = 75.789210,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-029",
            name = "Jaypee Hospital",
            category = "Quaternary Care Multi Specialty Center",
            rating = 4.86,
            reviewCount = 1100,
            distanceKm = 8.1,
            address = "Wish Town, Sector 128",
            area = "Sector 128",
            city = "Noida",
            state = "Uttar Pradesh",
            postalCode = "201304",
            facilities = listOf("Organ Transplant Institute", "Robotic Surgery", "Cardiac Cath Lab", "24/7 ER"),
            departments = listOf("Cardiology", "Neurology", "Oncology", "Gastroenterology"),
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d37222d8fce3",
            bestTimeToVisit = "09:00 AM - 11:30 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 18,
            liveBedAvailability = 35,
            emergencyContact = "+91 120 412 3333",
            latitude = 28.513210,
            longitude = 77.368310,
            isRecommended = false
        ),
        Hospital(
            id = "HOSP-IN-030",
            name = "Apollo Hospitals Arilova",
            category = "Multispecialty Super Specialty Center",
            rating = 4.82,
            reviewCount = 880,
            distanceKm = 5.4,
            address = "Health City, Arilova",
            area = "Arilova",
            city = "Visakhapatnam",
            state = "Andhra Pradesh",
            postalCode = "530040",
            facilities = listOf("Cath Lab", "Stroke Care Unit", "Joint Replacement Center", "24/7 ER"),
            departments = listOf("Cardiology", "Neurology", "Orthopedics", "Emergency Medicine"),
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d",
            bestTimeToVisit = "08:30 AM - 11:00 AM",
            crowdLevel = CrowdLevel.LOW,
            averageWaitMinutes = 18,
            liveBedAvailability = 30,
            emergencyContact = "+91 891 272 9999",
            latitude = 17.765310,
            longitude = 83.332110,
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

    fun updateHospitalsList(newList: List<Hospital>) {
        val uniqueList = newList.distinctBy { it.id }
        _hospitals.value = uniqueList
    }

    fun getDoctorById(id: String): Doctor? {
        return doctors.find { it.id == id } ?: doctors.firstOrNull()
    }

    fun getAppointmentById(id: String): Appointment? {
        return _appointments.value.find { it.id == id } ?: _appointments.value.firstOrNull()
    }

    fun updateAppointmentsList(newList: List<Appointment>) {
        val uniqueList = newList.distinctBy { it.id }.sortedByDescending { it.createdTimestamp }
        _appointments.value = uniqueList
    }

    fun addAppointment(
        appointmentType: AppointmentType,
        hospital: Hospital,
        doctor: Doctor,
        date: String,
        timeSlot: String,
        patientName: String,
        patientPhone: String,
        accessibilityNeeds: String,
        overrideRef: String? = null,
        overrideId: String? = null
    ): Appointment {
        val ref = overrideRef ?: ("SCA-" + (100000..999999).random())
        val tokenNum = "A-" + (102..120).random()
        val details = "Consultation for ${doctor.specialization} at ${hospital.name}. Please arrive 10 minutes prior to $timeSlot."

        val newApp = Appointment(
            id = overrideId ?: ("app_" + UUID.randomUUID().toString().take(6)),
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
            referenceNumber = ref,
            tokenNumber = tokenNum,
            consultationDetails = details
        )

        // Avoid duplicates and keep latest first
        _appointments.value = (listOf(newApp) + _appointments.value).distinctBy { it.id }.sortedByDescending { it.createdTimestamp }

        // Auto assign a demo Queue Token for this booking
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
