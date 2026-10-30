<?php
/**
 * SmartCareAI - Hospital Model
 */

require_once __DIR__ . '/../config/database.php';

class Hospital {
    private PDO $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

    public function getAll(int $limit = 50, int $offset = 0): array {
        $stmt = $this->pdo->prepare('
            SELECT * FROM hospitals 
            ORDER BY is_recommended DESC, rating DESC 
            LIMIT :limit OFFSET :offset
        ');
        $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
        $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
        $stmt->execute();

        $rows = $stmt->fetchAll();
        return array_map([$this, 'formatHospital'], $rows);
    }

    public function findById(string $id): ?array {
        $stmt = $this->pdo->prepare('SELECT * FROM hospitals WHERE id = :id LIMIT 1');
        $stmt->execute([':id' => $id]);
        $row = $stmt->fetch();
        if (!$row) return null;

        $hospital = $this->formatHospital($row);
        $hospital['departments'] = $this->getDepartments($id);
        $hospital['doctors'] = $this->getDoctors($id);
        return $hospital;
    }

    public function search(string $query, ?string $crowdLevel = null): array {
        $sql = 'SELECT * FROM hospitals WHERE (name LIKE :q OR category LIKE :q OR address LIKE :q)';
        $params = [':q' => '%' . trim($query) . '%'];

        if (!empty($crowdLevel)) {
            $sql .= ' AND crowd_level = :crowd';
            $params[':crowd'] = strtoupper(trim($crowdLevel));
        }

        $sql .= ' ORDER BY rating DESC';
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);

        $rows = $stmt->fetchAll();
        return array_map([$this, 'formatHospital'], $rows);
    }

    public function getDepartments(string $hospitalId): array {
        $stmt = $this->pdo->prepare('SELECT * FROM departments WHERE hospital_id = :id ORDER BY name ASC');
        $stmt->execute([':id' => $hospitalId]);
        return $stmt->fetchAll();
    }

    public function getDoctors(string $hospitalId): array {
        $stmt = $this->pdo->prepare('
            SELECT d.*, dept.name AS department_name
            FROM doctors d
            LEFT JOIN departments dept ON dept.id = d.department_id
            WHERE d.hospital_id = :id
            ORDER BY d.rating DESC
        ');
        $stmt->execute([':id' => $hospitalId]);
        $rows = $stmt->fetchAll();

        return array_map(function($doc) {
            $doc['available_slots'] = !empty($doc['available_slots']) ? json_decode($doc['available_slots'], true) : [];
            $doc['languages'] = !empty($doc['languages']) ? json_decode($doc['languages'], true) : [];
            return $doc;
        }, $rows);
    }

    private function formatHospital(array $row): array {
        $row['facilities'] = !empty($row['facilities']) ? json_decode($row['facilities'], true) : [];
        $row['departments_json'] = !empty($row['departments_json']) ? json_decode($row['departments_json'], true) : [];
        $row['rating'] = (float)$row['rating'];
        $row['distance_km'] = (float)$row['distance_km'];
        $row['review_count'] = (int)$row['review_count'];
        $row['average_wait_minutes'] = (int)$row['average_wait_minutes'];
        $row['live_bed_availability'] = (int)$row['live_bed_availability'];
        $row['is_recommended'] = (bool)$row['is_recommended'];
        return $row;
    }
}
