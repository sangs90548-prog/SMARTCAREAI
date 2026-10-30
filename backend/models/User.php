<?php
/**
 * SmartCareAI - User Model
 */

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';

class User {
    private PDO $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

    public function findByEmail(string $email): ?array {
        $stmt = $this->pdo->prepare('SELECT * FROM users WHERE email = :email LIMIT 1');
        $stmt->execute([':email' => strtolower(trim($email))]);
        $user = $stmt->fetch();
        return $user ?: null;
    }

    public function findById(int $id): ?array {
        $stmt = $this->pdo->prepare('
            SELECT id, name, email, age, gender, blood_type, phone, emergency_contact, 
                   health_reminders_enabled, ai_tips_enabled, created_at, updated_at
            FROM users WHERE id = :id LIMIT 1
        ');
        $stmt->execute([':id' => $id]);
        $user = $stmt->fetch();
        return $user ?: null;
    }

    public function create(array $data): int {
        $stmt = $this->pdo->prepare('
            INSERT INTO users (name, email, password_hash, age, gender, blood_type, phone, emergency_contact, health_reminders_enabled, ai_tips_enabled)
            VALUES (:name, :email, :password_hash, :age, :gender, :blood_type, :phone, :emergency_contact, :reminders, :tips)
        ');
        $stmt->execute([
            ':name'              => trim($data['name']),
            ':email'             => strtolower(trim($data['email'])),
            ':password_hash'     => password_hash($data['password'], PASSWORD_BCRYPT),
            ':age'               => $data['age'] ?? 30,
            ':gender'            => $data['gender'] ?? 'Prefer not to say',
            ':blood_type'        => $data['blood_type'] ?? 'Unknown',
            ':phone'             => $data['phone'] ?? null,
            ':emergency_contact' => $data['emergency_contact'] ?? null,
            ':reminders'         => isset($data['health_reminders_enabled']) ? (int)$data['health_reminders_enabled'] : 1,
            ':tips'              => isset($data['ai_tips_enabled']) ? (int)$data['ai_tips_enabled'] : 1
        ]);
        return (int)$this->pdo->lastInsertId();
    }

    public function updateProfile(int $id, array $data): bool {
        $fields = [];
        $params = [':id' => $id];

        $allowed = ['name', 'age', 'gender', 'blood_type', 'phone', 'emergency_contact'];
        foreach ($allowed as $field) {
            if (array_key_exists($field, $data)) {
                $fields[] = "`$field` = :$field";
                $params[":$field"] = $data[$field];
            }
        }

        if (empty($fields)) {
            return false;
        }

        $sql = 'UPDATE users SET ' . implode(', ', $fields) . ' WHERE id = :id';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function updatePreferences(int $id, ?int $reminders, ?int $tips): bool {
        $fields = [];
        $params = [':id' => $id];

        if ($reminders !== null) {
            $fields[] = 'health_reminders_enabled = :reminders';
            $params[':reminders'] = $reminders;
        }
        if ($tips !== null) {
            $fields[] = 'ai_tips_enabled = :tips';
            $params[':tips'] = $tips;
        }

        if (empty($fields)) {
            return false;
        }

        $sql = 'UPDATE users SET ' . implode(', ', $fields) . ' WHERE id = :id';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function createToken(int $userId): string {
        $plainToken = bin2hex(random_bytes(32));
        $tokenHash = hash('sha256', $plainToken);
        $expiresAt = date('Y-m-d H:i:s', strtotime('+' . TOKEN_EXPIRY_DAYS . ' days'));

        $stmt = $this->pdo->prepare('
            INSERT INTO api_tokens (user_id, token_hash, expires_at)
            VALUES (:user_id, :token_hash, :expires_at)
        ');
        $stmt->execute([
            ':user_id'    => $userId,
            ':token_hash' => $tokenHash,
            ':expires_at' => $expiresAt
        ]);

        return $plainToken;
    }

    public function revokeToken(string $plainToken): bool {
        $tokenHash = hash('sha256', $plainToken);
        $stmt = $this->pdo->prepare('DELETE FROM api_tokens WHERE token_hash = :hash');
        return $stmt->execute([':hash' => $tokenHash]);
    }

    public function revokeAllTokens(int $userId): bool {
        $stmt = $this->pdo->prepare('DELETE FROM api_tokens WHERE user_id = :user_id');
        return $stmt->execute([':user_id' => $userId]);
    }
}
