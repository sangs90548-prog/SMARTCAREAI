<?php
/**
 * SmartCareAI - Backend Configuration
 * Centralized settings for database, Python AI service, and security.
 */

// Error Reporting (adjust in production)
error_reporting(E_ALL);
ini_set('display_errors', '0');
ini_set('log_errors', '1');

// Database Settings
define('DB_HOST', getenv('DB_HOST') ?: '127.0.0.1');
define('DB_PORT', getenv('DB_PORT') ?: '3306');
define('DB_NAME', getenv('DB_NAME') ?: 'smartcareai');
define('DB_USER', getenv('DB_USER') ?: 'root');
define('DB_PASS', getenv('DB_PASS') !== false ? getenv('DB_PASS') : 'root'); // Falls back to 'root' for MySQL 8; set empty for standard XAMPP if no pass

// Python AI Service (FastAPI)
define('AI_SERVICE_URL', getenv('AI_SERVICE_URL') ?: 'http://127.0.0.1:8000');
define('AI_REQUEST_TIMEOUT', 20); // seconds

// Application & Security
define('APP_NAME', 'SmartCareAI Backend');
define('APP_VERSION', '1.0.0');
define('TOKEN_EXPIRY_DAYS', 30);
define('CORS_ALLOWED_ORIGINS', '*');
