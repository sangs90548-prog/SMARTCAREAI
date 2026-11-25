-- =====================================================================
-- SMARTCAREAI - MySQL Database Schema & Initial Data
-- Database Name: smartcareai
-- Compatible with MySQL 8.0+ / MariaDB 10.4+ (XAMPP / phpMyAdmin)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `smartcareai` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `smartcareai`;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `assessments`;
DROP TABLE IF EXISTS `appointments`;
DROP TABLE IF EXISTS `doctors`;
DROP TABLE IF EXISTS `departments`;
DROP TABLE IF EXISTS `hospitals`;
DROP TABLE IF EXISTS `api_tokens`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 1. Table: users
-- ---------------------------------------------------------------------
CREATE TABLE `users` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(120) NOT NULL,
    `email` VARCHAR(180) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `age` INT UNSIGNED DEFAULT 30,
    `gender` ENUM('Male', 'Female', 'Other', 'Prefer not to say') DEFAULT 'Prefer not to say',
    `blood_type` VARCHAR(10) DEFAULT 'Unknown',
    `phone` VARCHAR(30) DEFAULT NULL,
    `emergency_contact` VARCHAR(30) DEFAULT NULL,
    `health_reminders_enabled` TINYINT(1) DEFAULT 1,
    `ai_tips_enabled` TINYINT(1) DEFAULT 1,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 2. Table: api_tokens
-- ---------------------------------------------------------------------
CREATE TABLE `api_tokens` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT UNSIGNED NOT NULL,
    `token_hash` VARCHAR(64) NOT NULL UNIQUE,
    `expires_at` DATETIME NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `last_used_at` DATETIME DEFAULT NULL,
    INDEX `idx_token_hash` (`token_hash`),
    INDEX `idx_token_user_id` (`user_id`),
    CONSTRAINT `fk_tokens_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. Table: hospitals
-- ---------------------------------------------------------------------
CREATE TABLE `hospitals` (
    `id` VARCHAR(36) PRIMARY KEY,
    `name` VARCHAR(200) NOT NULL,
    `category` VARCHAR(100) NOT NULL DEFAULT 'General Hospital',
    `rating` DECIMAL(3, 2) DEFAULT 4.50,
    `review_count` INT UNSIGNED DEFAULT 0,
    `distance_km` DECIMAL(5, 2) DEFAULT 2.50,
    `address` VARCHAR(255) NOT NULL,
    `city` VARCHAR(100) NOT NULL DEFAULT 'Chennai',
    `state` VARCHAR(100) DEFAULT 'Tamil Nadu',
    `postal_code` VARCHAR(20) DEFAULT '',
    `phone` VARCHAR(50) NOT NULL,
    `emergency_contact` VARCHAR(50) NOT NULL,
    `facilities` JSON DEFAULT NULL,
    `departments_json` JSON DEFAULT NULL,
    `image_url` VARCHAR(255) DEFAULT '',
    `best_time_to_visit` VARCHAR(100) DEFAULT 'Morning (08:00 AM - 11:00 AM)',
    `crowd_level` ENUM('LOW', 'MODERATE', 'HIGH', 'CRITICAL') DEFAULT 'MODERATE',
    `average_wait_minutes` INT UNSIGNED DEFAULT 25,
    `live_bed_availability` INT UNSIGNED DEFAULT 18,
    `latitude` DECIMAL(10, 8) DEFAULT 13.060422,
    `longitude` DECIMAL(11, 8) DEFAULT 80.251244,
    `is_recommended` TINYINT(1) DEFAULT 0,
    `recommendation_reason` VARCHAR(255) DEFAULT '',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_hospitals_name` (`name`),
    INDEX `idx_hospitals_city` (`city`),
    INDEX `idx_hospitals_state` (`state`),
    INDEX `idx_hospitals_crowd` (`crowd_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4. Table: departments
-- ---------------------------------------------------------------------
CREATE TABLE `departments` (
    `id` VARCHAR(36) PRIMARY KEY,
    `hospital_id` VARCHAR(36) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `doctor_count` INT UNSIGNED DEFAULT 1,
    `icon_name` VARCHAR(50) DEFAULT 'LocalHospital',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_dept_hospital` (`hospital_id`),
    CONSTRAINT `fk_dept_hospital` FOREIGN KEY (`hospital_id`) REFERENCES `hospitals` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 5. Table: doctors
-- ---------------------------------------------------------------------
CREATE TABLE `doctors` (
    `id` VARCHAR(36) PRIMARY KEY,
    `hospital_id` VARCHAR(36) NOT NULL,
    `department_id` VARCHAR(36) DEFAULT NULL,
    `name` VARCHAR(150) NOT NULL,
    `specialization` VARCHAR(150) NOT NULL,
    `department_name` VARCHAR(100) DEFAULT '',
    `qualifications` VARCHAR(200) DEFAULT '',
    `experience_years` INT UNSIGNED DEFAULT 5,
    `rating` DECIMAL(3, 2) DEFAULT 4.50,
    `review_count` INT UNSIGNED DEFAULT 0,
    `consultation_fee` DECIMAL(8, 2) DEFAULT 500.00,
    `available_slots` JSON DEFAULT NULL,
    `bio` TEXT DEFAULT NULL,
    `languages` JSON DEFAULT NULL,
    `photo_url` VARCHAR(255) DEFAULT '',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_doctors_hospital` (`hospital_id`),
    INDEX `idx_doctors_spec` (`specialization`),
    CONSTRAINT `fk_doc_hospital` FOREIGN KEY (`hospital_id`) REFERENCES `hospitals` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 6. Table: appointments
-- ---------------------------------------------------------------------
CREATE TABLE `appointments` (
    `id` VARCHAR(36) PRIMARY KEY,
    `user_id` INT UNSIGNED NOT NULL,
    `hospital_id` VARCHAR(36) NOT NULL,
    `doctor_id` VARCHAR(36) NOT NULL,
    `appointment_type` ENUM('IN_PERSON', 'TELEHEALTH', 'EMERGENCY_TRIAGE', 'FOLLOW_UP') DEFAULT 'IN_PERSON',
    `date` DATE NOT NULL,
    `time_slot` VARCHAR(30) NOT NULL,
    `patient_name` VARCHAR(120) NOT NULL,
    `patient_phone` VARCHAR(30) NOT NULL,
    `accessibility_needs` VARCHAR(255) DEFAULT '',
    `status` ENUM('UPCOMING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
    `reference_number` VARCHAR(30) NOT NULL UNIQUE,
    `notes` TEXT DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_appointments_user` (`user_id`),
    INDEX `idx_appointments_doctor_date` (`doctor_id`, `date`, `time_slot`),
    CONSTRAINT `fk_app_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_app_hospital` FOREIGN KEY (`hospital_id`) REFERENCES `hospitals` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_app_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 7. Table: assessments
-- ---------------------------------------------------------------------
CREATE TABLE `assessments` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT UNSIGNED NOT NULL,
    `symptoms` TEXT NOT NULL,
    `duration` VARCHAR(80) DEFAULT NULL,
    `severity` VARCHAR(50) DEFAULT NULL,
    `urgency_level` ENUM('Emergency', 'High', 'Moderate', 'Mild', 'Low') NOT NULL DEFAULT 'Mild',
    `possible_causes` JSON DEFAULT NULL,
    `health_information` TEXT DEFAULT NULL,
    `seek_care_advice` TEXT DEFAULT NULL,
    `emergency_warning` TEXT DEFAULT NULL,
    `raw_ai_response` JSON DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_assessments_user` (`user_id`),
    CONSTRAINT `fk_assess_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 8. Table: notifications
-- ---------------------------------------------------------------------
CREATE TABLE `notifications` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT UNSIGNED NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `message` TEXT NOT NULL,
    `category` VARCHAR(50) DEFAULT 'System',
    `target_route` VARCHAR(100) DEFAULT '',
    `is_read` TINYINT(1) DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_notif_user` (`user_id`),
    CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- INITIAL DATA INSERTION
-- =====================================================================

-- 1. Insert Demo Users (Alex Johnson & Sarah Jenkins)
INSERT INTO `users` (`id`, `name`, `email`, `password_hash`, `age`, `gender`, `blood_type`, `phone`, `emergency_contact`, `health_reminders_enabled`, `ai_tips_enabled`)
VALUES
(1, 'Alex Johnson', 'alex.johnson@example.com', '$2y$10$Az0jxt81Wr5rD/bCyb7.ceQI6UXJsmKE3V.5UxGBZTgKLbsqu8Yam', 32, 'Male', 'O+', '+91 98765 43210', '+91 91100 22000', 1, 1),
(2, 'Sarah Jenkins', 'sarah.jenkins@example.com', '$2y$10$Az0jxt81Wr5rD/bCyb7.ceQI6UXJsmKE3V.5UxGBZTgKLbsqu8Yam', 34, 'Female', 'A+', '+91 98765 12345', '+91 91100 33000', 1, 1);

-- 2. Insert Hospitals (India-Wide Verified Dataset across 12 States / UTs)
INSERT INTO `hospitals` (`id`, `name`, `category`, `rating`, `review_count`, `distance_km`, `address`, `city`, `state`, `postal_code`, `phone`, `emergency_contact`, `facilities`, `departments_json`, `image_url`, `best_time_to_visit`, `crowd_level`, `average_wait_minutes`, `live_bed_availability`, `latitude`, `longitude`, `is_recommended`, `recommendation_reason`)
VALUES
('HOSP-IN-001', 'Apollo Hospitals Greams Road', 'Multispecialty Tertiary Care', 4.90, 1840, 1.80, '21 Greams Lane, Thousand Lights', 'Chennai', 'Tamil Nadu', '600006', '+91 44 2829 0200', '+91 44 2829 3333',
 JSON_ARRAY('24/7 Emergency & Trauma', 'Robotic Surgery', 'Proton Therapy', '24/7 Pharmacy', 'Organ Transplant Wing'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Orthopedics', 'Oncology', 'Emergency Medicine'),
 'https://images.unsplash.com/photo-1587351021759-3e566b6af7cc?w=800', '08:30 AM - 10:30 AM', 'LOW', 15, 42, 13.06042200, 80.25124400, 1, 'Top recommendation in Tamil Nadu for cardiology and lowest waiting time'),

('HOSP-IN-002', 'Kovai Medical Center and Hospital (KMCH)', 'Multi Super Specialty Center', 4.85, 1250, 4.20, '99 Avinashi Road, Civil Aerodrome Post', 'Coimbatore', 'Tamil Nadu', '641014', '+91 422 4323800', '+91 422 2627784',
 JSON_ARRAY('Level 1 Trauma Center', 'Cath Lab', 'Pediatric ICU', 'Subsidized Transport', 'Helipad'),
 JSON_ARRAY('Cardiology', 'Orthopedics', 'Nephrology', 'Gastroenterology', 'Pediatrics'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '09:00 AM - 11:30 AM', 'MODERATE', 20, 35, 11.02834000, 77.02641000, 0, ''),

('HOSP-IN-003', 'Christian Medical College (CMC)', 'Specialty & Academic Tertiary Hospital', 4.95, 2450, 8.50, 'Ida Scudder Road', 'Vellore', 'Tamil Nadu', '632004', '+91 416 228 1000', '+91 416 228 2000',
 JSON_ARRAY('Bone Marrow Transplant Unit', '24/7 Emergency', 'Advanced Pathology', 'Blood Bank'),
 JSON_ARRAY('Hematology', 'Gastroenterology', 'Neurology', 'Endocrinology', 'General Surgery'),
 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=800', '07:30 AM - 10:00 AM', 'HIGH', 40, 15, 12.92389000, 79.13506000, 0, ''),

('HOSP-IN-004', 'Rajiv Gandhi Government General Hospital (RGGGH)', 'Government Apex Teaching Hospital', 4.55, 980, 2.40, 'EVR Periyar Salai, Park Town', 'Chennai', 'Tamil Nadu', '600003', '+91 44 2530 5000', '+91 44 2530 5111',
 JSON_ARRAY('24/7 Free ER Triage', 'Dialysis Unit', 'Burn Care Wing', 'Blood Bank'),
 JSON_ARRAY('General Medicine', 'General Surgery', 'Cardiology', 'Emergency Medicine'),
 'https://images.unsplash.com/photo-1629909613654-28e377c37b09?w=800', '08:00 AM - 11:00 AM', 'MODERATE', 25, 60, 13.08182000, 80.27783000, 0, ''),

('HOSP-IN-005', 'Manipal Hospital Old Airport Road', 'Multispecialty Super Specialty Center', 4.88, 1620, 3.10, '98 HAL 2nd Stage, Kodihalli, Old Airport Road', 'Bengaluru', 'Karnataka', '560017', '+91 80 2502 4444', '+91 80 2502 3333',
 JSON_ARRAY('24/7 ER', 'Advanced Cardiac ICU', 'Organ Transplant Suite', 'Robotic Surgery'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Orthopedics', 'Pediatrics', 'Oncology'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '09:00 AM - 11:30 AM', 'LOW', 18, 38, 12.95823000, 77.64871000, 1, 'Top rated multispecialty center in Bengaluru with fast queue processing'),

('HOSP-IN-006', 'Narayana Health City', 'Cardiac & Super Specialty Institute', 4.90, 2100, 12.00, '258/A Bommasandra Industrial Area, Anekal Taluk', 'Bengaluru', 'Karnataka', '560099', '+91 80 7122 2222', '+91 80 7122 3333',
 JSON_ARRAY('Pediatric Cardiac Care', 'Bone Marrow Unit', 'Cancer Institute', '24/7 Pharmacy'),
 JSON_ARRAY('Cardiology', 'Cardiothoracic Surgery', 'Oncology', 'Nephrology'),
 'https://images.unsplash.com/photo-1587351021759-3e566b6af7cc?w=800', '08:00 AM - 10:30 AM', 'MODERATE', 25, 50, 12.80838000, 77.69742000, 0, ''),

('HOSP-IN-007', 'Fortis Hospital Bannerghatta Road', 'Multi Super Specialty Hospital', 4.75, 1140, 5.80, '154/9 Bannerghatta Road, Opp IIM-B', 'Bengaluru', 'Karnataka', '560076', '+91 80 6621 4444', '+91 80 6621 4000',
 JSON_ARRAY('Cath Lab', 'Neuro Critical Care', 'Dialysis Suite', '24/7 ER'),
 JSON_ARRAY('Neurology', 'Orthopedics', 'Urology', 'Cardiology'),
 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=800', '10:00 AM - 01:00 PM', 'LOW', 20, 28, 12.89435000, 77.59864000, 0, ''),

('HOSP-IN-008', 'Lilavati Hospital and Research Centre', 'Super Specialty Hospital & Research Hub', 4.88, 1950, 4.50, 'A-791 Bandra Reclamation, Bandra West', 'Mumbai', 'Maharashtra', '400050', '+91 22 2675 1000', '+91 22 2675 1111',
 JSON_ARRAY('Advanced Cardiac ICU', 'Robotic Surgery', 'Neuro Cath Lab', '24/7 Trauma'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Gastroenterology', 'Orthopedics'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '08:30 AM - 11:00 AM', 'MODERATE', 22, 30, 19.05141000, 72.82882000, 1, 'Premier super specialty center in Western India'),

('HOSP-IN-009', 'Tata Memorial Hospital', 'Premier Cancer Care & Research Center', 4.92, 2800, 6.20, 'Dr. Ernest Borges Road, Parel', 'Mumbai', 'Maharashtra', '400012', '+91 22 2417 7000', '+91 22 2417 7001',
 JSON_ARRAY('Radiation Oncology Suite', 'Bone Marrow Transplant', 'Proton Therapy', 'Clinical Research'),
 JSON_ARRAY('Oncology', 'Radiation Therapy', 'Surgical Oncology', 'Hematology'),
 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=800', '07:00 AM - 09:30 AM', 'HIGH', 35, 18, 19.00282000, 72.84278000, 0, ''),

('HOSP-IN-010', 'Kokilaben Dhirubhai Ambani Hospital', 'Quaternary Care Multi Specialty Hospital', 4.89, 1420, 7.80, 'Rao Saheb Achutrao Patwardhan Marg, Four Bungalows, Andheri West', 'Mumbai', 'Maharashtra', '400053', '+91 22 4269 6969', '+91 22 4269 9999',
 JSON_ARRAY('Robotic Surgery Suite', 'Intraoperative MRI', 'Children Heart Center', '24/7 ER'),
 JSON_ARRAY('Cardiology', 'Pediatrics', 'Neurology', 'Orthopedics'),
 'https://images.unsplash.com/photo-1587351021759-3e566b6af7cc?w=800', '09:30 AM - 12:00 PM', 'LOW', 18, 45, 19.13123000, 72.82521000, 0, ''),

('HOSP-IN-011', 'Ruby Hall Clinic', 'Multispecialty Super Specialty Hospital', 4.82, 1180, 3.20, '40 Sassoon Road, Sangamvadi', 'Pune', 'Maharashtra', '411001', '+91 20 6645 5100', '+91 20 6645 5200',
 JSON_ARRAY('Organ Transplant Center', 'Cardiac Cath Lab', 'Neuro Intensive Care', '24/7 Pharmacy'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Nephrology', 'General Surgery'),
 'https://images.unsplash.com/photo-1629909613654-28e377c37b09?w=800', '09:00 AM - 11:30 AM', 'LOW', 20, 32, 18.53234000, 73.87321000, 0, ''),

('HOSP-IN-012', 'All India Institute of Medical Sciences (AIIMS)', 'Apex National Public Medical Institute', 4.95, 3200, 4.80, 'Sri Aurobindo Marg, Ansari Nagar', 'New Delhi', 'Delhi', '110029', '+91 11 2658 8500', '+91 11 2658 8700',
 JSON_ARRAY('National Emergency & Trauma Block', 'Apex Heart Center', 'Cancer Institute', 'Organ Transplant Wing'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Orthopedics', 'Pediatrics', 'Emergency Medicine', 'Oncology'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '07:00 AM - 09:30 AM', 'HIGH', 30, 80, 28.56721000, 77.21002000, 1, 'Top national public hospital with complete multispecialty care'),

('HOSP-IN-013', 'Max Super Speciality Hospital Saket', 'Multi Super Specialty Center', 4.86, 1750, 6.10, '1 2 Press Enclave Road, Saket', 'New Delhi', 'Delhi', '110017', '+91 11 2651 5050', '+91 11 2651 5151',
 JSON_ARRAY('Max Heart & Vascular Institute', 'Bone Marrow Suite', 'Da Vinci Robotic System', '24/7 ER'),
 JSON_ARRAY('Cardiology', 'Oncology', 'Orthopedics', 'Pulmonology'),
 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=800', '09:00 AM - 11:30 AM', 'MODERATE', 25, 35, 28.52834000, 77.21142000, 0, ''),

('HOSP-IN-014', 'Medanta - The Medicity', 'Multi Super Specialty Institute', 4.91, 2300, 15.00, 'CH Baktawar Singh Road, Sector 38', 'Gurugram', 'Haryana', '122001', '+91 124 414 1414', '+91 124 414 1111',
 JSON_ARRAY('Heart Institute', 'Kidney & Urology Institute', 'CyberKnife Radiosurgery', 'Air Ambulance'),
 JSON_ARRAY('Cardiology', 'Urology', 'Neurology', 'Orthopedics'),
 'https://images.unsplash.com/photo-1587351021759-3e566b6af7cc?w=800', '08:30 AM - 11:00 AM', 'LOW', 18, 65, 28.43821000, 77.04231000, 0, ''),

('HOSP-IN-015', 'Fortis Escorts Heart Institute', 'Premier Cardiac Specialty Center', 4.88, 1530, 7.20, 'Okhla Road, Opp Holy Family Hospital', 'New Delhi', 'Delhi', '110025', '+91 11 4713 5000', '+91 11 4713 5555',
 JSON_ARRAY('Dedicated Cardiac ER', 'Pediatric Cardiology', '3D Cath Lab', 'Cardiac Rehab'),
 JSON_ARRAY('Cardiology', 'Cardiothoracic Surgery', 'Pediatric Cardiology'),
 'https://images.unsplash.com/photo-1629909613654-28e377c37b09?w=800', '09:00 AM - 11:30 AM', 'LOW', 15, 25, 28.56041000, 77.27211000, 0, ''),

('HOSP-IN-016', 'Yashoda Hospitals Somajiguda', 'Multi Super Specialty Center', 4.84, 1320, 2.80, 'Raj Bhavan Road, Somajiguda', 'Hyderabad', 'Telangana', '500082', '+91 40 4567 4567', '+91 40 4567 9999',
 JSON_ARRAY('Level 1 Emergency Care', 'Heart & Lung Transplant', 'Surgical Oncology', '24/7 Pharmacy'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Gastroenterology', 'Oncology'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '08:30 AM - 11:00 AM', 'LOW', 18, 40, 17.42431000, 78.45821000, 1, 'Top multi specialty medical provider in Hyderabad region'),

('HOSP-IN-017', 'KIMS Hospitals Secunderabad', 'Multi Super Specialty Hospital', 4.81, 1080, 4.50, '1-8-31/1 Minister Road, Krishna Nagar Colony', 'Hyderabad', 'Telangana', '500003', '+91 40 4488 5000', '+91 40 4488 5111',
 JSON_ARRAY('Comprehensive Cancer Center', 'Joint Replacement Unit', 'Dialysis Suite', '24/7 ER'),
 JSON_ARRAY('Orthopedics', 'Oncology', 'Nephrology', 'General Surgery'),
 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=800', '09:30 AM - 12:00 PM', 'MODERATE', 25, 30, 17.43621000, 78.48911000, 0, ''),

('HOSP-IN-018', 'Aster Medcity', 'Quaternary Care Healthcare Destination', 4.90, 1720, 6.80, 'Kuttisahib Road, Cheranalloor', 'Kochi', 'Kerala', '682027', '+91 484 669 9999', '+91 484 669 0000',
 JSON_ARRAY('Minimal Access Surgery', 'Astercare ER', 'Pediatric Super Specialty', 'Helipad Access'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Pediatrics', 'Oncology', 'Gastroenterology'),
 'https://images.unsplash.com/photo-1587351021759-3e566b6af7cc?w=800', '08:30 AM - 11:00 AM', 'LOW', 15, 50, 10.04621000, 76.26831000, 1, 'High efficiency healthcare destination in Kerala with lowest wait time'),

('HOSP-IN-019', 'Amrita Institute of Medical Sciences (AIMS)', 'Super Specialty Academic Healthcare Campus', 4.92, 2150, 8.20, 'Amrita Lane, AIMS P.O., Edappally', 'Kochi', 'Kerala', '682041', '+91 484 285 1234', '+91 484 280 1234',
 JSON_ARRAY('Robotic Surgery Suite', 'Organ Transplant Center', 'CyberKnife Radiation', '24/7 Emergency'),
 JSON_ARRAY('Cardiology', 'Oncology', 'Neurology', 'Nephrology', 'General Medicine'),
 'https://images.unsplash.com/photo-1629909613654-28e377c37b09?w=800', '08:00 AM - 10:30 AM', 'MODERATE', 25, 42, 10.03211000, 76.29821000, 0, ''),

('HOSP-IN-020', 'Government Medical College Thiruvananthapuram', 'State Apex Teaching Medical Center', 4.60, 890, 3.40, 'Medical College PO, Chalakkuzhi', 'Thiruvananthapuram', 'Kerala', '695011', '+91 471 252 8300', '+91 471 252 8400',
 JSON_ARRAY('24/7 Trauma Triage', 'Burn ICU', 'Free Public Dialysis', 'Blood Bank'),
 JSON_ARRAY('General Medicine', 'General Surgery', 'Pediatrics', 'Emergency Medicine'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '08:00 AM - 11:00 AM', 'HIGH', 35, 75, 8.52431000, 76.92821000, 0, ''),

('HOSP-IN-021', 'AMRI Hospital Dhakuria', 'Multi Super Specialty Hospital', 4.78, 960, 4.10, 'P-238 Block A, Scheme LXVIII, Dhakuria', 'Kolkata', 'West Bengal', '700031', '+91 33 6680 0000', '+91 33 6680 1111',
 JSON_ARRAY('Level 3 Emergency', 'Interventional Cardiology', 'Joint Reconstruction', '24/7 Pharmacy'),
 JSON_ARRAY('Cardiology', 'Orthopedics', 'Gastroenterology', 'Neurology'),
 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=800', '09:00 AM - 11:30 AM', 'LOW', 20, 26, 22.51231000, 88.36821000, 0, ''),

('HOSP-IN-022', 'Apollo Gleneagles Hospitals Kolkata', 'Multispecialty Tertiary Facility', 4.85, 1420, 5.50, '58 Canal Circular Road, Kadapara', 'Kolkata', 'West Bengal', '700054', '+91 33 2320 3040', '+91 33 2320 2122',
 JSON_ARRAY('Comprehensive Cancer Center', 'Organ Transplant Suite', 'Advanced Cath Lab', '24/7 ER'),
 JSON_ARRAY('Cardiology', 'Oncology', 'Neurology', 'Pediatrics'),
 'https://images.unsplash.com/photo-1587351021759-3e566b6af7cc?w=800', '08:30 AM - 11:00 AM', 'MODERATE', 24, 34, 22.57821000, 88.39921000, 0, ''),

('HOSP-IN-023', 'Zydus Hospital Thaltej', 'Quaternary Multi Specialty Hospital', 4.87, 1280, 3.80, 'Zydus Hospital Road, Near Sola Bridge, SG Highway', 'Ahmedabad', 'Gujarat', '380054', '+91 79 6670 0000', '+91 79 6670 1111',
 JSON_ARRAY('Level 1 Trauma Center', 'Cardiac Cath Lab', 'Robotic Surgery', '24/7 Pharmacy'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Orthopedics', 'Urology'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '09:00 AM - 11:30 AM', 'LOW', 18, 36, 23.05821000, 72.51821000, 1, 'Leading quaternary care hospital in Gujarat'),

('HOSP-IN-024', 'Sawai Man Singh (SMS) Hospital', 'State Government Apex Teaching Hospital', 4.65, 1850, 2.50, 'JLN Marg, Ashok Nagar', 'Jaipur', 'Rajasthan', '302004', '+91 141 256 0291', '+91 141 256 0292',
 JSON_ARRAY('24/7 Public ER Triage', 'Advanced Burn ICU', 'Dialysis Center', 'Blood Bank'),
 JSON_ARRAY('General Medicine', 'General Surgery', 'Orthopedics', 'Emergency Medicine'),
 'https://images.unsplash.com/photo-1629909613654-28e377c37b09?w=800', '08:00 AM - 11:00 AM', 'HIGH', 35, 90, 26.89821000, 75.81821000, 0, ''),

('HOSP-IN-025', 'Sanjay Gandhi Postgraduate Institute of Medical Sciences (SGPGI)', 'Apex Super Specialty Institute', 4.91, 2100, 9.20, 'Raebareli Road', 'Lucknow', 'Uttar Pradesh', '226014', '+91 522 266 8700', '+91 522 266 8800',
 JSON_ARRAY('Apex Trauma Center', 'Gastroenterology Institute', 'Renal Transplant Wing', 'Advanced Pathology'),
 JSON_ARRAY('Gastroenterology', 'Nephrology', 'Cardiology', 'Neurology'),
 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=800', '08:00 AM - 10:30 AM', 'MODERATE', 25, 40, 26.74821000, 80.93821000, 0, ''),

('HOSP-IN-026', 'AIIMS Patna', 'National Institute of Medical Excellence', 4.80, 1150, 7.50, 'Phulwari Sharif', 'Patna', 'Bihar', '801507', '+91 612 245 1000', '+91 612 245 1111',
 JSON_ARRAY('24/7 ER Block', 'Trauma Center', 'Advanced Diagnostic Suite', 'Pediatric ICU'),
 JSON_ARRAY('General Medicine', 'Cardiology', 'Pediatrics', 'Orthopedics'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '08:30 AM - 11:00 AM', 'MODERATE', 28, 55, 25.56231000, 85.04821000, 0, ''),

('HOSP-IN-027', 'Kiran Multi Super Specialty Hospital', 'Tertiary Healthcare Institute', 4.84, 940, 5.20, 'Vasta Devdi Road, Near Sumul Dairy, Katargam', 'Surat', 'Gujarat', '395004', '+91 261 716 1111', '+91 261 716 9999',
 JSON_ARRAY('Cath Lab', 'Joint Replacement Suite', 'Level 3 ICU', '24/7 ER'),
 JSON_ARRAY('Cardiology', 'Orthopedics', 'Urology', 'Pediatrics'),
 'https://images.unsplash.com/photo-1587351021759-3e566b6af7cc?w=800', '09:00 AM - 11:30 AM', 'LOW', 20, 30, 21.22821000, 72.82821000, 0, ''),

('HOSP-IN-028', 'Baby Memorial Hospital', 'Multi Super Specialty Healthcare Hub', 4.83, 1020, 3.90, 'Indira Gandhi Road, Arayidathupalam', 'Kozhikode', 'Kerala', '673004', '+91 495 246 8000', '+91 495 246 8111',
 JSON_ARRAY('Advanced Cardiac Care', 'Trauma Suite', 'Neonatal ICU', '24/7 Pharmacy'),
 JSON_ARRAY('Pediatrics', 'Cardiology', 'Neurology', 'Orthopedics'),
 'https://images.unsplash.com/photo-1516549655169-df83a0774514?w=800', '09:00 AM - 11:30 AM', 'LOW', 18, 28, 11.25821000, 75.78921000, 0, ''),

('HOSP-IN-029', 'Jaypee Hospital', 'Quaternary Care Multi Specialty Center', 4.86, 1100, 8.10, 'Wish Town, Sector 128', 'Noida', 'Uttar Pradesh', '201304', '+91 120 412 2222', '+91 120 412 3333',
 JSON_ARRAY('Organ Transplant Institute', 'Robotic Surgery', 'Cardiac Cath Lab', '24/7 ER'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Oncology', 'Gastroenterology'),
 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=800', '09:00 AM - 11:30 AM', 'LOW', 18, 35, 28.51321000, 77.36831000, 0, ''),

('HOSP-IN-030', 'Apollo Hospitals Arilova', 'Multispecialty Super Specialty Center', 4.82, 880, 5.40, 'Health City, Arilova', 'Visakhapatnam', 'Andhra Pradesh', '530040', '+91 891 272 7272', '+91 891 272 9999',
 JSON_ARRAY('Cath Lab', 'Stroke Care Unit', 'Joint Replacement Center', '24/7 ER'),
 JSON_ARRAY('Cardiology', 'Neurology', 'Orthopedics', 'Emergency Medicine'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '08:30 AM - 11:00 AM', 'LOW', 18, 30, 17.76531000, 83.33211000, 0, '');

-- 3. Insert Departments
INSERT INTO `departments` (`id`, `hospital_id`, `name`, `description`, `doctor_count`, `icon_name`)
VALUES
('DEP-001', 'HOSP-IN-001', 'Cardiology', 'Comprehensive cardiovascular diagnostics, interventional procedures, and heart health management.', 12, 'Favorite'),
('DEP-002', 'HOSP-IN-001', 'Neurology', 'Advanced diagnostic and therapeutic care for neurological and cerebrovascular conditions.', 8, 'Psychology'),
('DEP-003', 'HOSP-IN-001', 'Orthopedics', 'Joint replacement, sports medicine, trauma rehabilitation, and spinal health.', 10, 'AccessibilityNew'),
('DEP-004', 'HOSP-IN-005', 'Oncology', 'State-of-the-art hematology and medical oncology, targeted chemotherapy, and immunotherapy.', 8, 'Healing'),
('DEP-005', 'HOSP-IN-008', 'Gastroenterology', 'Endoscopic procedures, liver disorders, inflammatory bowel care, and digestive health.', 6, 'Medication'),
('DEP-006', 'HOSP-IN-012', 'General Medicine', 'Preventative outpatient healthcare, annual physicals, chronic condition monitoring, and vaccinations.', 15, 'People');

-- 4. Insert Doctors
INSERT INTO `doctors` (`id`, `hospital_id`, `department_id`, `name`, `specialization`, `department_name`, `qualifications`, `experience_years`, `rating`, `review_count`, `consultation_fee`, `available_slots`, `bio`, `languages`, `photo_url`)
VALUES
('DOC-001', 'HOSP-IN-001', 'DEP-001', 'Dr. Amanda Richardson', 'Senior Cardiologist', 'Cardiology', 'MD, DM Cardiology, FACC', 16, 4.92, 312, 800.00,
 JSON_ARRAY('09:00 AM', '10:30 AM', '01:30 PM', '03:00 PM'),
 'Senior Cardiologist specializing in preventive cardiac health, hypertension, and non-invasive diagnostics.',
 JSON_ARRAY('English', 'Tamil', 'Hindi'), 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=800'),

('DOC-002', 'HOSP-IN-001', 'DEP-002', 'Dr. Robert Vance', 'Neurologist & Spine Specialist', 'Neurology', 'MD, DM Neurology', 14, 4.85, 245, 900.00,
 JSON_ARRAY('09:30 AM', '11:00 AM', '02:00 PM', '04:15 PM'),
 'Leads the Neuro-spine Team with over 14 years of clinical experience in headache and neuropathy management.',
 JSON_ARRAY('English', 'Tamil'), 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=800'),

('DOC-003', 'HOSP-IN-005', 'DEP-001', 'Dr. Marcus Chen', 'Orthopedic Surgeon', 'Orthopedics', 'MS Orthopedics, MCh', 12, 4.78, 198, 850.00,
 JSON_ARRAY('10:00 AM', '11:30 AM', '02:30 PM', '05:00 PM'),
 'Specialist in joint reconstruction, sports injury rehabilitation, and arthroscopic procedures.',
 JSON_ARRAY('English', 'Kannada', 'Hindi'), 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=800'),

('DOC-004', 'HOSP-IN-018', 'DEP-001', 'Dr. Sophia Patel', 'Pediatric Consultant', 'Pediatrics', 'MD Pediatrics, DCH', 10, 4.90, 420, 700.00,
 JSON_ARRAY('08:30 AM', '10:00 AM', '01:00 PM', '03:30 PM'),
 'Compassionate pediatric consultant passionate about early childhood development and allergy prevention.',
 JSON_ARRAY('English', 'Malayalam', 'Hindi'), 'https://images.unsplash.com/photo-1594824813566-78a08a28f804?w=800');
