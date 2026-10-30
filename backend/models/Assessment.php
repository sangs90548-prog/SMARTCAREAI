<?php
/**
 * SmartCareAI - Assessment Model
 */

require_once __DIR__ . '/../config/database.php';

class Assessment {
    private PDO $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

    public function create(array $data): int {
        $stmt = $this->pdo->prepare('
            INSERT INTO assessments (
                user_id, symptoms, duration, severity, urgency_level,
                possible_causes, health_information, seek_care_advice,
                emergency_warning, raw_ai_response
            ) VALUES (
                :user_id, :symptoms, :duration, :severity, :urgency_level,
                :possible_causes, :health_information, :seek_care_advice,
                :emergency_warning, :raw_ai_response
            )
        ');

        $stmt->execute([
            ':user_id'            => $data['user_id'],
            ':symptoms'           => trim($data['symptoms']),
            ':duration'           => $data['duration'] ?? null,
            ':severity'           => $data['severity'] ?? null,
            ':urgency_level'      => $data['urgency_level'] ?? 'Mild',
            ':possible_causes'    => is_array($data['possible_causes']) ? json_encode($data['possible_causes']) : $data['possible_causes'],
            ':health_information' => $data['health_information'] ?? null,
            ':seek_care_advice'   => $data['seek_care_advice'] ?? null,
            ':emergency_warning'  => $data['emergency_warning'] ?? null,
            ':raw_ai_response'    => is_array($data['raw_ai_response']) ? json_encode($data['raw_ai_response']) : $data['raw_ai_response']
        ]);

        return (int)$this->pdo->lastInsertId();
    }

    public function getByUserId(int $userId, int $limit = 30, int $offset = 0): array {
        $stmt = $this->pdo->prepare('
            SELECT * FROM assessments 
            WHERE user_id = :user_id 
            ORDER BY created_at DESC 
            LIMIT :limit OFFSET :offset
        ');
        $stmt->bindValue(':user_id', $userId, PDO::PARAM_INT);
        $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
        $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
        $stmt->execute();

        return array_map([$this, 'formatAssessment'], $stmt->fetchAll());
    }

    public function findById(int $id, ?int $userId = null): ?array {
        $sql = 'SELECT * FROM assessments WHERE id = :id';
        $params = [':id' => $id];

        if ($userId !== null) {
            $sql .= ' AND user_id = :user_id';
            $params[':user_id'] = $userId;
        }

        $sql .= ' LIMIT 1';
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        $row = $stmt->fetch();

        return $row ? $this->formatAssessment($row) : null;
    }

    private function formatAssessment(array $row): array {
        $row['possible_causes'] = !empty($row['possible_causes']) ? json_decode($row['possible_causes'], true) : [];
        $row['raw_ai_response'] = !empty($row['raw_ai_response']) ? json_decode($row['raw_ai_response'], true) : null;
        $row['id'] = (int)$row['id'];
        $row['user_id'] = (int)$row['user_id'];
        return $row;
    }
}
