<?php
/**
 * SmartCareAI - Appointment Model
 */

require_once __DIR__ . '/../config/database.php';

class Appointment {
    private PDO $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

    public function create(array $data): string {
        $id = 'APT-' . strtoupper(bin2hex(random_bytes(4)));
        $refNumber = 'SC-' . date('Y') . '-' . strtoupper(substr(bin2hex(random_bytes(3)), 0, 5));

        $stmt = $this->pdo->prepare('
            INSERT INTO appointments (
                id, user_id, hospital_id, doctor_id, appointment_type, 
                date, time_slot, patient_name, patient_phone, 
                accessibility_needs, status, reference_number, notes
            ) VALUES (
                :id, :user_id, :hospital_id, :doctor_id, :appointment_type,
                :date, :time_slot, :patient_name, :patient_phone,
                :accessibility_needs, "UPCOMING", :reference_number, :notes
            )
        ');

        $stmt->execute([
            ':id'                  => $id,
            ':user_id'             => $data['user_id'],
            ':hospital_id'         => $data['hospital_id'],
            ':doctor_id'           => $data['doctor_id'],
            ':appointment_type'    => $data['appointment_type'] ?? 'IN_PERSON',
            ':date'                => $data['date'],
            ':time_slot'           => $data['time_slot'],
            ':patient_name'        => trim($data['patient_name']),
            ':patient_phone'       => trim($data['patient_phone']),
            ':accessibility_needs' => $data['accessibility_needs'] ?? '',
            ':reference_number'    => $refNumber,
            ':notes'               => $data['notes'] ?? null
        ]);

        return $id;
    }

    public function getByUserId(int $userId): array {
        $stmt = $this->pdo->prepare('
            SELECT a.*, 
                   h.name AS hospital_name, h.address AS hospital_address, h.phone AS hospital_phone,
                   d.name AS doctor_name, d.specialization AS doctor_specialization, 
                   d.department_name, d.photo_url AS doctor_photo_url, d.consultation_fee
            FROM appointments a
            INNER JOIN hospitals h ON h.id = a.hospital_id
            INNER JOIN doctors d ON d.id = a.doctor_id
            WHERE a.user_id = :user_id
            ORDER BY a.date DESC, a.time_slot DESC
        ');
        $stmt->execute([':user_id' => $userId]);
        return $stmt->fetchAll();
    }

    public function findById(string $id, ?int $userId = null): ?array {
        $sql = '
            SELECT a.*, 
                   h.name AS hospital_name, h.address AS hospital_address, h.phone AS hospital_phone,
                   d.name AS doctor_name, d.specialization AS doctor_specialization, 
                   d.department_name, d.photo_url AS doctor_photo_url, d.consultation_fee
            FROM appointments a
            INNER JOIN hospitals h ON h.id = a.hospital_id
            INNER JOIN doctors d ON d.id = a.doctor_id
            WHERE a.id = :id
        ';
        $params = [':id' => $id];

        if ($userId !== null) {
            $sql .= ' AND a.user_id = :user_id';
            $params[':user_id'] = $userId;
        }

        $sql .= ' LIMIT 1';
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        $row = $stmt->fetch();
        return $row ?: null;
    }

    public function cancel(string $id, int $userId): bool {
        $stmt = $this->pdo->prepare('
            UPDATE appointments 
            SET status = "CANCELLED" 
            WHERE id = :id AND user_id = :user_id AND status = "UPCOMING"
        ');
        $stmt->execute([':id' => $id, ':user_id' => $userId]);
        return $stmt->rowCount() > 0;
    }

    public function checkDuplicate(int $userId, string $doctorId, string $date, string $timeSlot): bool {
        $stmt = $this->pdo->prepare('
            SELECT COUNT(*) FROM appointments
            WHERE user_id = :user_id 
              AND doctor_id = :doctor_id 
              AND date = :date 
              AND time_slot = :time_slot 
              AND status != "CANCELLED"
        ');
        $stmt->execute([
            ':user_id'   => $userId,
            ':doctor_id' => $doctorId,
            ':date'      => $date,
            ':time_slot' => $timeSlot
        ]);
        return ((int)$stmt->fetchColumn()) > 0;
    }
}
