<?php
/**
 * SmartCareAI - Public Entry Point (Front Controller)
 */

// Enable CORS
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Authorization, Content-Type, Accept, X-Requested-With');
header('Access-Control-Max-Age: 86400');

// Handle OPTIONS preflight request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

require_once __DIR__ . '/../routes/api.php';

try {
    $method = strtoupper($_SERVER['REQUEST_METHOD']);
    $uri = $_SERVER['REQUEST_URI'];

    // Parse JSON request body
    $rawInput = file_get_contents('php://input');
    $body = [];
    if (!empty($rawInput)) {
        $decoded = json_decode($rawInput, true);
        if (json_last_error() === JSON_ERROR_NONE && is_array($decoded)) {
            $body = $decoded;
        }
    }
    // Merge standard $_POST if body was empty form-urlencoded
    if (empty($body) && !empty($_POST)) {
        $body = $_POST;
    }

    $query = $_GET ?? [];

    Router::dispatch($method, $uri, $body, $query);

} catch (Throwable $e) {
    Response::serverError($e->getMessage());
}
