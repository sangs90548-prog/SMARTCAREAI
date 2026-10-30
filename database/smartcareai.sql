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
    `city` VARCHAR(100) NOT NULL DEFAULT 'Metro City',
    `state` VARCHAR(100) DEFAULT '',
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
    `is_recommended` TINYINT(1) DEFAULT 0,
    `recommendation_reason` VARCHAR(255) DEFAULT '',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_hospitals_name` (`name`),
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
    `specialization` VARCHAR(120) NOT NULL,
    `department_name` VARCHAR(100) NOT NULL,
    `qualifications` VARCHAR(255) DEFAULT 'MBBS, MD',
    `experience_years` INT UNSIGNED DEFAULT 10,
    `rating` DECIMAL(3, 2) DEFAULT 4.80,
    `review_count` INT UNSIGNED DEFAULT 40,
    `consultation_fee` DECIMAL(8, 2) DEFAULT 75.00,
    `available_slots` JSON DEFAULT NULL,
    `bio` TEXT DEFAULT NULL,
    `languages` JSON DEFAULT NULL,
    `photo_url` VARCHAR(255) DEFAULT '',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_doctors_hospital` (`hospital_id`),
    INDEX `idx_doctors_specialization` (`specialization`),
    CONSTRAINT `fk_doctors_hospital` FOREIGN KEY (`hospital_id`) REFERENCES `hospitals` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_doctors_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL
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
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_assessments_user` (`user_id`),
    CONSTRAINT `fk_assessments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 8. Table: notifications
-- ---------------------------------------------------------------------
CREATE TABLE `notifications` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT UNSIGNED NOT NULL,
    `title` VARCHAR(160) NOT NULL,
    `message` TEXT NOT NULL,
    `category` ENUM('Appointment', 'Assessment', 'Hospital', 'Queue', 'System') DEFAULT 'System',
    `is_read` TINYINT(1) DEFAULT 0,
    `target_route` VARCHAR(120) DEFAULT '',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_notifications_user` (`user_id`),
    CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- INITIAL VERIFIED DEMO DATA
-- Note: Replace with production clinical databases when deploying.
-- Default Demo User Password: Password123!
-- =====================================================================

-- 1. Insert Demo Users (Alex Johnson & Sarah Jenkins)
INSERT INTO `users` (`id`, `name`, `email`, `password_hash`, `age`, `gender`, `blood_type`, `phone`, `emergency_contact`, `health_reminders_enabled`, `ai_tips_enabled`)
VALUES
(1, 'Alex Johnson', 'alex.johnson@example.com', '$2y$10$Az0jxt81Wr5rD/bCyb7.ceQI6UXJsmKE3V.5UxGBZTgKLbsqu8Yam', 32, 'Male', 'O+', '+1 (555) 019-2834', '+1 (555) 911-0022', 1, 1),
(2, 'Sarah Jenkins', 'sarah.jenkins@example.com', '$2y$10$Az0jxt81Wr5rD/bCyb7.ceQI6UXJsmKE3V.5UxGBZTgKLbsqu8Yam', 34, 'Female', 'A+', '+1 (555) 234-5678', '+1 (555) 911-0033', 1, 1);

-- 2. Insert Hospitals
INSERT INTO `hospitals` (`id`, `name`, `category`, `rating`, `review_count`, `distance_km`, `address`, `city`, `state`, `postal_code`, `phone`, `emergency_contact`, `facilities`, `departments_json`, `image_url`, `best_time_to_visit`, `crowd_level`, `average_wait_minutes`, `live_bed_availability`, `is_recommended`, `recommendation_reason`)
VALUES
('HOSP-001', 'City General Medical Center', 'Multispecialty Tertiary Care', 4.85, 342, 1.80, '1200 Healthcare Blvd, Metro City', 'Metro City', 'CA', '94103', '+1 (555) 234-5000', '+1 (555) 911-1001', 
 JSON_ARRAY('24/7 Emergency', 'Advanced ICU', 'MRI/CT Diagnostic Suite', 'Cardiac Catheterization Lab', 'Onsite Pharmacy'), 
 JSON_ARRAY('Cardiology', 'Neurology', 'Orthopedics', 'Emergency Medicine', 'Pediatrics'),
 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800', '08:00 AM - 11:00 AM', 'LOW', 15, 24, 1, 'Shortest wait times and highest patient satisfaction index this week'),

('HOSP-002', 'St. Jude Healthcare & Research Institute', 'Specialty & Academic Hospital', 4.90, 512, 3.40, '450 University Way, North District', 'Metro City', 'CA', '94105', '+1 (555) 345-6000', '+1 (555) 911-1002', 
 JSON_ARRAY('Comprehensive Cancer Center', 'Organ Transplant Wing', 'Robotic Surgery Theater', 'Clinical Research Labs'), 
 JSON_ARRAY('Oncology', 'Cardiology', 'Gastroenterology', 'Pulmonology', 'Dermatology'),
 'https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=800', '01:00 PM - 03:30 PM', 'MODERATE', 30, 14, 0, ''),

('HOSP-003', 'Metro West Community Hospital', 'Community Healthcare Facility', 4.65, 198, 4.20, '88 Sunset Parkway, West Valley', 'Metro City', 'CA', '94110', '+1 (555) 456-7000', '+1 (555) 911-1003', 
 JSON_ARRAY('Express Urgent Care', 'Maternity & Neonatal Ward', 'Physical Rehabilitation', 'Dialysis Center'), 
 JSON_ARRAY('Family Medicine', 'Obstetrics & Gynecology', 'Pediatrics', 'Radiology'),
 'https://images.unsplash.com/photo-1516549655169-df83a0774514?w=800', '09:00 AM - 12:00 PM', 'HIGH', 45, 8, 0, '');

-- 3. Insert Departments
INSERT INTO `departments` (`id`, `hospital_id`, `name`, `description`, `doctor_count`, `icon_name`)
VALUES
('DEP-001', 'HOSP-001', 'Cardiology', 'Comprehensive cardiovascular diagnostics, interventional procedures, and heart health management.', 6, 'Favorite'),
('DEP-002', 'HOSP-001', 'Neurology', 'Advanced diagnostic and therapeutic care for neurological and cerebrovascular conditions.', 4, 'Psychology'),
('DEP-003', 'HOSP-001', 'Orthopedics', 'Joint replacement, sports medicine, trauma rehabilitation, and spinal health.', 5, 'AccessibilityNew'),
('DEP-004', 'HOSP-002', 'Oncology', 'State-of-the-art hematology and medical oncology, targeted chemotherapy, and immunotherapy.', 8, 'Healing'),
('DEP-005', 'HOSP-002', 'Gastroenterology', 'Endoscopic procedures, liver disorders, inflammatory bowel care, and digestive health.', 4, 'Medication'),
('DEP-006', 'HOSP-003', 'Family Medicine', 'Preventative outpatient healthcare, annual physicals, chronic condition monitoring, and vaccinations.', 7, 'People');

-- 4. Insert Doctors
INSERT INTO `doctors` (`id`, `hospital_id`, `department_id`, `name`, `specialization`, `department_name`, `qualifications`, `experience_years`, `rating`, `review_count`, `consultation_fee`, `available_slots`, `bio`, `languages`, `photo_url`)
VALUES
('DOC-001', 'HOSP-001', 'DEP-001', 'Dr. Evelyn Carter', 'Cardiology Specialist', 'Cardiology', 'MD, FACC, Harvard Medical School', 14, 4.92, 128, 90.00,
 JSON_ARRAY('09:00 AM', '10:00 AM', '11:30 AM', '02:00 PM', '03:30 PM'),
 'Board-certified cardiologist specializing in preventive cardiology, echocardiography, and coronary artery disease management.',
 JSON_ARRAY('English', 'Spanish'),
 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400'),

('DOC-002', 'HOSP-001', 'DEP-002', 'Dr. Marcus Vance', 'Senior Neurologist', 'Neurology', 'MD, PhD, Johns Hopkins Medicine', 18, 4.88, 95, 110.00,
 JSON_ARRAY('10:00 AM', '11:00 AM', '01:30 PM', '04:00 PM'),
 'Specializes in neuro-degenerative treatment, migraine management, EEG analysis, and neuro-rehabilitation.',
 JSON_ARRAY('English', 'German'),
 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=400'),

('DOC-003', 'HOSP-001', 'DEP-003', 'Dr. Priya Sharma', 'Orthopedic Surgeon', 'Orthopedics', 'MS Ortho, FRCS, Oxford University', 12, 4.85, 114, 85.00,
 JSON_ARRAY('08:30 AM', '10:30 AM', '02:30 PM', '03:30 PM'),
 'Expertise in minimally invasive arthroscopy, sports injuries, knee and hip replacements.',
 JSON_ARRAY('English', 'Hindi'),
 'https://images.unsplash.com/photo-1594824813583-a44df8861623?w=400'),

('DOC-004', 'HOSP-002', 'DEP-004', 'Dr. Jonathan Reynolds', 'Medical Oncologist', 'Oncology', 'MD, Sloan Kettering Institute', 16, 4.95, 140, 125.00,
 JSON_ARRAY('09:30 AM', '11:30 AM', '02:00 PM', '04:30 PM'),
 'Pioneer in molecular targeted therapy and clinical trial immunotherapy treatments.',
 JSON_ARRAY('English', 'French'),
 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=400'),

('DOC-005', 'HOSP-003', 'DEP-006', 'Dr. Sarah Lin', 'Primary Care Physician', 'Family Medicine', 'MD, Stanford University', 9, 4.79, 88, 60.00,
 JSON_ARRAY('08:00 AM', '09:00 AM', '10:30 AM', '01:00 PM', '02:30 PM'),
 'Dedicated to holistic wellness, pediatric development checks, geriatric health, and lifestyle coaching.',
 JSON_ARRAY('English', 'Mandarin'),
 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400');

-- 5. Insert Sample Initial Appointment
INSERT INTO `appointments` (`id`, `user_id`, `hospital_id`, `doctor_id`, `appointment_type`, `date`, `time_slot`, `patient_name`, `patient_phone`, `accessibility_needs`, `status`, `reference_number`, `notes`)
VALUES
('APT-1001', 1, 'HOSP-001', 'DOC-001', 'IN_PERSON', DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00 AM', 'Alex Johnson', '+1 (555) 019-2834', 'None', 'UPCOMING', 'SC-2026-94812', 'Annual routine cardiac wellness checkup');

-- 6. Insert Sample Past Assessment
INSERT INTO `assessments` (`id`, `user_id`, `symptoms`, `duration`, `severity`, `urgency_level`, `possible_causes`, `health_information`, `seek_care_advice`, `emergency_warning`, `raw_ai_response`)
VALUES
(1, 1, 'Mild headache and dry throat after working long hours', '2 days', 'Mild', 'Mild', 
 JSON_ARRAY('Dehydration', 'Tension Headache', 'Digital Eye Strain'),
 'Symptoms are consistent with mild dehydration and muscle tension from prolonged screen exposure. Maintain adequate hydration and take scheduled ocular breaks.',
 'If headache worsens into intense throbbing accompanied by light sensitivity, fever, or persists beyond 48 hours, consult a physician.',
 'Seek immediate emergency attention if experiencing sudden numbness, slurred speech, or vision loss.',
 JSON_OBJECT('provider', 'SmartCareAI Gemini Core', 'status', 'SUCCESS'));

-- 7. Insert Initial Notifications
INSERT INTO `notifications` (`id`, `user_id`, `title`, `message`, `category`, `is_read`, `target_route`)
VALUES
(1, 1, 'Appointment Confirmed', 'Your consultation with Dr. Evelyn Carter is confirmed for 10:00 AM.', 'Appointment', 0, 'booking_history'),
(2, 1, 'Health Tip of the Day', 'Stay hydrated: Drinking 2.5L of water daily significantly improves focus and reduces fatigue.', 'System', 1, 'welcome_preferences');
