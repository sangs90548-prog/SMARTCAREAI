<?php
/**
 * SmartCareAI - Notification Model
 */

require_once __DIR__ . '/../config/database.php';

class Notification {
    private PDO $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

    public function create(int $userId, string $title, string $message, string $category = 'System', string $targetRoute = ''): int {
        $stmt = $this->pdo->prepare('
            INSERT INTO notifications (user_id, title, message, category, is_read, target_route)
            VALUES (:user_id, :title, :message, :category, 0, :target_route)
        ');
        $stmt->execute([
            ':user_id'      => $userId,
            ':title'        => $title,
            ':message'      => $message,
            ':category'     => $category,
            ':target_route' => $targetRoute
        ]);
        return (int)$this->pdo->lastInsertId();
    }

    public function getByUserId(int $userId, int $limit = 50): array {
        $stmt = $this->pdo->prepare('
            SELECT * FROM notifications 
            WHERE user_id = :user_id 
            ORDER BY created_at DESC 
            LIMIT :limit
        ');
        $stmt->bindValue(':user_id', $userId, PDO::PARAM_INT);
        $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();

        $rows = $stmt->fetchAll();
        return array_map(function($n) {
            $n['id'] = (int)$n['id'];
            $n['user_id'] = (int)$n['user_id'];
            $n['is_read'] = (bool)$n['is_read'];
            return $n;
        }, $rows);
    }

    public function markAsRead(int $id, int $userId): bool {
        $stmt = $this->pdo->prepare('
            UPDATE notifications 
            SET is_read = 1 
            WHERE id = :id AND user_id = :user_id
        ');
        $stmt->execute([':id' => $id, ':user_id' => $userId]);
        return $stmt->rowCount() > 0;
    }

    public function markAllAsRead(int $userId): bool {
        $stmt = $this->pdo->prepare('
            UPDATE notifications 
            SET is_read = 1 
            WHERE user_id = :user_id
        ');
        $stmt->execute([':user_id' => $userId]);
        return $stmt->rowCount() > 0;
    }
}
