<?php
/**
 * SmartCareAI - Database Connection Manager
 * Manages PDO connections using prepared statements and UTF-8mb4.
 */

require_once __DIR__ . '/config.php';

class Database {
    private static ?PDO $instance = null;

    public static function getConnection(): PDO {
        if (self::$instance === null) {
            $dsn = sprintf(
                'mysql:host=%s;port=%s;dbname=%s;charset=utf8mb4',
                DB_HOST,
                DB_PORT,
                DB_NAME
            );

            $options = [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES   => false,
                PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"
            ];

            try {
                self::$instance = new PDO($dsn, DB_USER, DB_PASS, $options);
            } catch (PDOException $e) {
                // Try fallback to empty password if connection failed and DB_PASS was 'root'
                if (DB_PASS === 'root') {
                    try {
                        self::$instance = new PDO($dsn, DB_USER, '', $options);
                        return self::$instance;
                    } catch (PDOException $fallbackEx) {
                        // Keep original error message
                    }
                }

                header('Content-Type: application/json; charset=UTF-8');
                http_response_code(500);
                echo json_encode([
                    'success' => false,
                    'error'   => 'Database connection error: ' . $e->getMessage()
                ]);
                exit;
            }
        }

        return self::$instance;
    }
}
