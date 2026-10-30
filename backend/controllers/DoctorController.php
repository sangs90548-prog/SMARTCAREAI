<?php
/**
 * SmartCareAI - Doctor Controller
 */

require_once __DIR__ . '/../models/Doctor.php';
require_once __DIR__ . '/../utils/Response.php';

class DoctorController {
    private Doctor $doctorModel;

    public function __construct() {
        $this->doctorModel = new Doctor();
    }

    public function getAll(array $queryParams): void {
        $limit = isset($queryParams['limit']) ? max(1, min(100, (int)$queryParams['limit'])) : 50;
        $offset = isset($queryParams['offset']) ? max(0, (int)$queryParams['offset']) : 0;

        $doctors = $this->doctorModel->getAll($limit, $offset);
        Response::success($doctors, 'Doctors retrieved successfully');
    }

    public function getById(string $id): void {
        $doctor = $this->doctorModel->findById($id);
        if (!$doctor) {
            Response::notFound("Doctor with ID '$id' was not found");
        }
        Response::success($doctor, 'Doctor profile retrieved');
    }

    public function search(array $queryParams): void {
        $query = $queryParams['q'] ?? '';
        $specialization = $queryParams['specialization'] ?? null;
        $hospitalId = $queryParams['hospital_id'] ?? null;

        $results = $this->doctorModel->search($query, $specialization, $hospitalId);
        Response::success($results, 'Doctor search results');
    }

    public function getAvailability(string $doctorId, array $queryParams): void {
        $date = $queryParams['date'] ?? '';
        $timeSlot = $queryParams['time_slot'] ?? '';

        if (empty($date) || empty($timeSlot)) {
            Response::error('Both date (YYYY-MM-DD) and time_slot query parameters are required', 422);
        }

        $doctor = $this->doctorModel->findById($doctorId);
        if (!$doctor) {
            Response::notFound("Doctor with ID '$doctorId' was not found");
        }

        $isAvailable = $this->doctorModel->isSlotAvailable($doctorId, $date, $timeSlot);
        Response::success([
            'doctor_id'    => $doctorId,
            'doctor_name'  => $doctor['name'],
            'date'         => $date,
            'time_slot'    => $timeSlot,
            'is_available' => $isAvailable
        ], $isAvailable ? 'Time slot is available' : 'Time slot is unavailable');
    }
}
