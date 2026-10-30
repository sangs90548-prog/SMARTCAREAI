<?php
/**
 * SmartCareAI - Doctor Model
 */

require_once __DIR__ . '/../config/database.php';

class Doctor {
    private PDO $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

    public function getAll(int $limit = 50, int $offset = 0): array {
        $stmt = $this->pdo->prepare('
            SELECT d.*, h.name AS hospital_name, h.address AS hospital_address
            FROM doctors d
            INNER JOIN hospitals h ON h.id = d.hospital_id
            ORDER BY d.rating DESC
            LIMIT :limit OFFSET :offset
        ');
        $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
        $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
        $stmt->execute();

        return array_map([$this, 'formatDoctor'], $stmt->fetchAll());
    }

    public function findById(string $id): ?array {
        $stmt = $this->pdo->prepare('
            SELECT d.*, h.name AS hospital_name, h.address AS hospital_address, h.phone AS hospital_phone
            FROM doctors d
            INNER JOIN hospitals h ON h.id = d.hospital_id
            WHERE d.id = :id
            LIMIT 1
        ');
        $stmt->execute([':id' => $id]);
        $row = $stmt->fetch();
        return $row ? $this->formatDoctor($row) : null;
    }

    public function search(string $query, ?string $specialization = null, ?string $hospitalId = null): array {
        $sql = '
            SELECT d.*, h.name AS hospital_name, h.address AS hospital_address
            FROM doctors d
            INNER JOIN hospitals h ON h.id = d.hospital_id
            WHERE (d.name LIKE :q OR d.specialization LIKE :q OR d.department_name LIKE :q)
        ';
        $params = [':q' => '%' . trim($query) . '%'];

        if (!empty($specialization)) {
            $sql .= ' AND d.specialization = :spec';
            $params[':spec'] = trim($specialization);
        }

        if (!empty($hospitalId)) {
            $sql .= ' AND d.hospital_id = :hosp';
            $params[':hosp'] = trim($hospitalId);
        }

        $sql .= ' ORDER BY d.rating DESC';
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);

        return array_map([$this, 'formatDoctor'], $stmt->fetchAll());
    }

    public function isSlotAvailable(string $doctorId, string $date, string $timeSlot): bool {
        // 1. Check if the doctor offers this slot
        $doctor = $this->findById($doctorId);
        if (!$doctor) return false;

        $slots = $doctor['available_slots'] ?? [];
        if (!in_array($timeSlot, $slots)) {
            return false;
        }

        // 2. Check if already booked and not cancelled
        $stmt = $this->pdo->prepare('
            SELECT COUNT(*) FROM appointments 
            WHERE doctor_id = :doctor_id 
              AND date = :date 
              AND time_slot = :time_slot 
              AND status != "CANCELLED"
        ');
        $stmt->execute([
            ':doctor_id' => $doctorId,
            ':date'      => $date,
            ':time_slot' => $timeSlot
        ]);

        return ((int)$stmt->fetchColumn()) === 0;
    }

    private function formatDoctor(array $row): array {
        $row['available_slots'] = !empty($row['available_slots']) ? json_decode($row['available_slots'], true) : [];
        $row['languages'] = !empty($row['languages']) ? json_decode($row['languages'], true) : [];
        $row['experience_years'] = (int)$row['experience_years'];
        $row['rating'] = (float)$row['rating'];
        $row['review_count'] = (int)$row['review_count'];
        $row['consultation_fee'] = (float)$row['consultation_fee'];
        return $row;
    }
}
