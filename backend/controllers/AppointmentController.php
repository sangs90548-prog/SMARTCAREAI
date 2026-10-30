<?php
/**
 * SmartCareAI - Appointment Controller
 */

require_once __DIR__ . '/../models/Appointment.php';
require_once __DIR__ . '/../models/Doctor.php';
require_once __DIR__ . '/../models/Hospital.php';
require_once __DIR__ . '/../models/Notification.php';
require_once __DIR__ . '/../utils/Response.php';

class AppointmentController {
    private Appointment $appointmentModel;
    private Doctor $doctorModel;
    private Hospital $hospitalModel;
    private Notification $notificationModel;

    public function __construct() {
        $this->appointmentModel = new Appointment();
        $this->doctorModel = new Doctor();
        $this->hospitalModel = new Hospital();
        $this->notificationModel = new Notification();
    }

    public function book(array $currentUser, array $request): void {
        $userId = (int)$currentUser['id'];

        $hospitalId = trim($request['hospital_id'] ?? '');
        $doctorId = trim($request['doctor_id'] ?? '');
        $date = trim($request['date'] ?? '');
        $timeSlot = trim($request['time_slot'] ?? '');
        $patientName = trim($request['patient_name'] ?? $currentUser['name']);
        $patientPhone = trim($request['patient_phone'] ?? ($currentUser['phone'] ?? ''));

        $errors = [];
        if (empty($hospitalId)) $errors['hospital_id'] = 'Hospital ID is required';
        if (empty($doctorId)) $errors['doctor_id'] = 'Doctor ID is required';
        if (empty($date) || !preg_match('/^\d{4}-\d{2}-\d{2}$/', $date)) $errors['date'] = 'Valid appointment date (YYYY-MM-DD) is required';
        if (empty($timeSlot)) $errors['time_slot'] = 'Appointment time slot is required';
        if (empty($patientName)) $errors['patient_name'] = 'Patient name is required';
        if (empty($patientPhone)) $errors['patient_phone'] = 'Patient phone number is required';

        if (!empty($errors)) {
            Response::error('Validation failed', 422, $errors);
        }

        // Validate date is not in past
        if (strtotime($date) < strtotime(date('Y-m-d'))) {
            Response::error('Appointment date cannot be in the past', 422);
        }

        // Validate hospital & doctor
        $hospital = $this->hospitalModel->findById($hospitalId);
        if (!$hospital) {
            Response::notFound('Specified hospital does not exist');
        }

        $doctor = $this->doctorModel->findById($doctorId);
        if (!$doctor) {
            Response::notFound('Specified doctor does not exist');
        }

        // Check for duplicate booking by user
        if ($this->appointmentModel->checkDuplicate($userId, $doctorId, $date, $timeSlot)) {
            Response::error('You already have an active appointment scheduled with this doctor at the selected date and time', 409);
        }

        // Check if doctor slot is available
        if (!$this->doctorModel->isSlotAvailable($doctorId, $date, $timeSlot)) {
            Response::error('The selected time slot is already booked or unavailable. Please choose another slot.', 409);
        }

        $appointmentId = $this->appointmentModel->create([
            'user_id'             => $userId,
            'hospital_id'         => $hospitalId,
            'doctor_id'           => $doctorId,
            'appointment_type'    => $request['appointment_type'] ?? 'IN_PERSON',
            'date'                => $date,
            'time_slot'           => $timeSlot,
            'patient_name'        => $patientName,
            'patient_phone'       => $patientPhone,
            'accessibility_needs' => $request['accessibility_needs'] ?? '',
            'notes'               => $request['notes'] ?? null
        ]);

        $appointment = $this->appointmentModel->findById($appointmentId, $userId);

        // Create in-app notification
        $this->notificationModel->create(
            $userId,
            'Appointment Confirmed',
            "Your appointment with {$doctor['name']} at {$hospital['name']} on $date at $timeSlot has been confirmed. Ref: {$appointment['reference_number']}.",
            'Appointment',
            'booking_history'
        );

        Response::success($appointment, 'Appointment booked successfully', 201);
    }

    public function listByUser(array $currentUser): void {
        $userId = (int)$currentUser['id'];
        $appointments = $this->appointmentModel->getByUserId($userId);
        Response::success($appointments, 'User appointments retrieved');
    }

    public function getDetails(array $currentUser, string $id): void {
        $userId = (int)$currentUser['id'];
        $appointment = $this->appointmentModel->findById($id, $userId);
        if (!$appointment) {
            Response::notFound("Appointment with ID '$id' was not found or does not belong to you");
        }
        Response::success($appointment, 'Appointment details retrieved');
    }

    public function cancel(array $currentUser, string $id): void {
        $userId = (int)$currentUser['id'];
        $appointment = $this->appointmentModel->findById($id, $userId);

        if (!$appointment) {
            Response::notFound("Appointment with ID '$id' not found");
        }

        if ($appointment['status'] === 'CANCELLED') {
            Response::error('This appointment is already cancelled', 400);
        }

        $success = $this->appointmentModel->cancel($id, $userId);
        if (!$success) {
            Response::error('Unable to cancel appointment. Only upcoming appointments can be cancelled.', 400);
        }

        // Create notification
        $this->notificationModel->create(
            $userId,
            'Appointment Cancelled',
            "Your appointment on {$appointment['date']} at {$appointment['time_slot']} (Ref: {$appointment['reference_number']}) was cancelled.",
            'Appointment',
            'booking_history'
        );

        Response::success(null, 'Appointment cancelled successfully');
    }
}
