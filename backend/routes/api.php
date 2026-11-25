<?php
/**
 * SmartCareAI - API Route Dispatcher
 */

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../middleware/AuthMiddleware.php';

require_once __DIR__ . '/../controllers/AuthController.php';
require_once __DIR__ . '/../controllers/UserController.php';
require_once __DIR__ . '/../controllers/HospitalController.php';
require_once __DIR__ . '/../controllers/DoctorController.php';
require_once __DIR__ . '/../controllers/AppointmentController.php';
require_once __DIR__ . '/../controllers/AssessmentController.php';
require_once __DIR__ . '/../controllers/NotificationController.php';

class Router {
    public static function dispatch(string $method, string $uri, array $body, array $query): void {
        // Strip base path prefix (e.g. /smartcareai/backend/public or /backend/public or /api)
        $path = parse_url($uri, PHP_URL_PATH);
        $path = preg_replace('#^.*index\.php#i', '', $path);
        $path = preg_replace('#^/+(api/)?#i', '/', $path);
        $path = '/' . trim($path, '/');

        // -------------------------------------------------------------
        // Public Endpoints
        // -------------------------------------------------------------
        if ($method === 'GET' && ($path === '/' || $path === '/health')) {
            try {
                $pdo = Database::getConnection();
                $dbStatus = 'connected';
            } catch (Exception $e) {
                $dbStatus = 'disconnected';
            }
            Response::success([
                'status'    => 'healthy',
                'service'   => APP_NAME,
                'version'   => APP_VERSION,
                'database'  => $dbStatus,
                'timestamp' => date('c')
            ], 'SmartCareAI Backend is operational');
        }

        // Auth
        if ($method === 'POST' && $path === '/auth/register') {
            (new AuthController())->register($body);
        }
        if ($method === 'POST' && $path === '/auth/login') {
            (new AuthController())->login($body);
        }

        // Hospitals (Public Discovery)
        if ($method === 'GET' && $path === '/hospitals') {
            (new HospitalController())->getAll($query);
        }
        if ($method === 'GET' && ($path === '/hospitals/places-search' || $path === '/hospitals/places')) {
            (new HospitalController())->placesSearch($query);
        }
        if ($method === 'GET' && $path === '/hospitals/search') {
            (new HospitalController())->search($query);
        }
        if ($method === 'GET' && preg_match('#^/hospitals/([^/]+)/doctors$#', $path, $m)) {
            (new HospitalController())->getDoctors($m[1]);
        }
        if ($method === 'GET' && preg_match('#^/hospitals/([^/]+)$#', $path, $m)) {
            (new HospitalController())->getById($m[1]);
        }

        // Doctors
        if ($method === 'GET' && $path === '/doctors') {
            (new DoctorController())->getAll($query);
        }
        if ($method === 'GET' && $path === '/doctors/search') {
            (new DoctorController())->search($query);
        }
        if ($method === 'GET' && preg_match('#^/doctors/([^/]+)/availability$#', $path, $m)) {
            (new DoctorController())->getAvailability($m[1], $query);
        }
        if ($method === 'GET' && preg_match('#^/doctors/([^/]+)$#', $path, $m)) {
            (new DoctorController())->getById($m[1]);
        }

        // -------------------------------------------------------------
        // Protected Endpoints (Requires Bearer Token)
        // -------------------------------------------------------------
        $user = AuthMiddleware::authenticate();

        if ($method === 'POST' && $path === '/auth/logout') {
            (new AuthController())->logout();
        }
        if ($method === 'GET' && $path === '/auth/me') {
            (new AuthController())->me($user);
        }

        // User Profile & Preferences
        if ($method === 'GET' && $path === '/user/profile') {
            (new UserController())->getProfile($user);
        }
        if (($method === 'PUT' || $method === 'POST') && $path === '/user/profile') {
            (new UserController())->updateProfile($user, $body);
        }
        if (($method === 'PUT' || $method === 'POST') && $path === '/user/preferences') {
            (new UserController())->updatePreferences($user, $body);
        }

        // Appointments
        if ($method === 'POST' && $path === '/appointments') {
            (new AppointmentController())->book($user, $body);
        }
        if ($method === 'GET' && $path === '/appointments') {
            (new AppointmentController())->listByUser($user);
        }
        if ($method === 'GET' && preg_match('#^/appointments/([^/]+)$#', $path, $m)) {
            (new AppointmentController())->getDetails($user, $m[1]);
        }
        if ($method === 'POST' && preg_match('#^/appointments/([^/]+)/cancel$#', $path, $m)) {
            (new AppointmentController())->cancel($user, $m[1]);
        }

        // AI Symptom Assessment
        if ($method === 'POST' && ($path === '/assessments/analyze' || $path === '/assessments')) {
            (new AssessmentController())->analyze($user, $body);
        }
        if ($method === 'GET' && ($path === '/assessments/history' || $path === '/assessments')) {
            (new AssessmentController())->history($user, $query);
        }
        if ($method === 'GET' && preg_match('#^/assessments/(\d+)$#', $path, $m)) {
            (new AssessmentController())->getById($user, (int)$m[1]);
        }

        // Notifications
        if ($method === 'GET' && $path === '/notifications') {
            (new NotificationController())->list($user, $query);
        }
        if ($method === 'POST' && preg_match('#^/notifications/(\d+)/read$#', $path, $m)) {
            (new NotificationController())->markRead($user, (int)$m[1]);
        }
        if ($method === 'POST' && $path === '/notifications/read-all') {
            (new NotificationController())->markAllRead($user);
        }

        // If no route matched
        Response::notFound("Endpoint '$method $path' not found on SmartCareAI API");
    }
}
