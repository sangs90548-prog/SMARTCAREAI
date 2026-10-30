<?php
/**
 * SmartCareAI - JSON Response Utility
 * Formats standardized API responses.
 */

class Response {
    public static function json($data, int $statusCode = 200): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=UTF-8');
        echo json_encode($data, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
        exit;
    }

    public static function success($data = null, string $message = 'Success', int $statusCode = 200): void {
        self::json([
            'success' => true,
            'message' => $message,
            'data'    => $data
        ], $statusCode);
    }

    public static function error(string $message = 'An error occurred', int $statusCode = 400, array $errors = []): void {
        $payload = [
            'success' => false,
            'message' => $message
        ];
        if (!empty($errors)) {
            $payload['errors'] = $errors;
        }
        self::json($payload, $statusCode);
    }

    public static function unauthorized(string $message = 'Unauthorized access'): void {
        self::error($message, 401);
    }

    public static function forbidden(string $message = 'Forbidden'): void {
        self::error($message, 403);
    }

    public static function notFound(string $message = 'Resource not found'): void {
        self::error($message, 404);
    }

    public static function serverError(string $message = 'Internal server error'): void {
        self::error($message, 500);
    }
}
