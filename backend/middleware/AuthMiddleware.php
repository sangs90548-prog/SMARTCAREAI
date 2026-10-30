<?php
/**
 * SmartCareAI - Authentication Middleware
 * Validates Bearer token headers, checks expiry, and returns authenticated user.
 */

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';

class AuthMiddleware {
    public static function authenticate(): array {
        $token = self::getBearerToken();

        if (!$token) {
            Response::unauthorized('Bearer token is required in Authorization header');
        }

        $pdo = Database::getConnection();
        $tokenHash = hash('sha256', $token);

        $stmt = $pdo->prepare('
            SELECT t.id AS token_id, t.expires_at, u.*
            FROM api_tokens t
            INNER JOIN users u ON u.id = t.user_id
            WHERE t.token_hash = :hash
            LIMIT 1
        ');
        $stmt->execute([':hash' => $tokenHash]);
        $row = $stmt->fetch();

        if (!$row) {
            Response::unauthorized('Invalid or revoked token');
        }

        if (strtotime($row['expires_at']) < time()) {
            // Delete expired token
            $del = $pdo->prepare('DELETE FROM api_tokens WHERE id = :id');
            $del->execute([':id' => $row['token_id']]);
            Response::unauthorized('Token has expired. Please log in again.');
        }

        // Update last used timestamp
        $updateStmt = $pdo->prepare('UPDATE api_tokens SET last_used_at = NOW() WHERE id = :id');
        $updateStmt->execute([':id' => $row['token_id']]);

        // Remove sensitive fields
        unset($row['password_hash']);
        return $row;
    }

    public static function getBearerToken(): ?string {
        $headers = null;
        if (isset($_SERVER['Authorization'])) {
            $headers = trim($_SERVER['Authorization']);
        } elseif (isset($_SERVER['HTTP_AUTHORIZATION'])) {
            $headers = trim($_SERVER['HTTP_AUTHORIZATION']);
        } elseif (function_exists('apache_request_headers')) {
            $requestHeaders = apache_request_headers();
            $requestHeaders = array_combine(
                array_map('ucwords', array_keys($requestHeaders)),
                array_values($requestHeaders)
            );
            if (isset($requestHeaders['Authorization'])) {
                $headers = trim($requestHeaders['Authorization']);
            }
        }

        if (!empty($headers)) {
            if (preg_match('/Bearer\s+(\S+)/i', $headers, $matches)) {
                return $matches[1];
            }
        }

        return null;
    }
}
