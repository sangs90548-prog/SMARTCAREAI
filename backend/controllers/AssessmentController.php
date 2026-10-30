<?php
/**
 * SmartCareAI - Assessment Controller
 * Coordinates AI symptom assessment via Python FastAPI + Google Gemini,
 * records results into MySQL, and enforces patient-level security.
 */

require_once __DIR__ . '/../models/Assessment.php';
require_once __DIR__ . '/../models/Notification.php';
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../config/config.php';

class AssessmentController {
    private Assessment $assessmentModel;
    private Notification $notificationModel;

    public function __construct() {
        $this->assessmentModel = new Assessment();
        $this->notificationModel = new Notification();
    }

    public function analyze(array $currentUser, array $request): void {
        $userId = (int)$currentUser['id'];
        $symptoms = trim($request['symptoms'] ?? '');

        if (empty($symptoms)) {
            Response::error('Symptoms description is required for assessment', 422);
        }

        $age = isset($request['age']) ? (int)$request['age'] : ($currentUser['age'] ?? 30);
        $duration = trim($request['duration'] ?? 'Recent');
        $severity = trim($request['severity'] ?? 'Moderate');

        // 1. Call Python AI FastAPI Service
        $aiPayload = [
            'symptoms' => $symptoms,
            'age'      => $age,
            'duration' => $duration,
            'severity' => $severity
        ];

        $aiResult = $this->callPythonAiService($aiPayload);

        // 2. Persist in MySQL assessments table
        $assessmentId = $this->assessmentModel->create([
            'user_id'            => $userId,
            'symptoms'           => $symptoms,
            'duration'           => $duration,
            'severity'           => $severity,
            'urgency_level'      => $aiResult['urgency_level'] ?? 'Mild',
            'possible_causes'    => $aiResult['possible_causes'] ?? [],
            'health_information' => $aiResult['health_information'] ?? '',
            'seek_care_advice'   => $aiResult['seek_care_advice'] ?? '',
            'emergency_warning'  => $aiResult['emergency_warning'] ?? '',
            'raw_ai_response'    => $aiResult
        ]);

        $savedRecord = $this->assessmentModel->findById($assessmentId, $userId);

        // 3. Dispatch Notification
        $urgency = $aiResult['urgency_level'] ?? 'Mild';
        $this->notificationModel->create(
            $userId,
            "AI Health Assessment ($urgency Risk)",
            "Your symptom analysis has been completed. Urgency level: $urgency.",
            'Assessment',
            'assessment_details'
        );

        Response::success($savedRecord, 'Symptom assessment completed successfully', 201);
    }

    public function history(array $currentUser, array $queryParams): void {
        $userId = (int)$currentUser['id'];
        $limit = isset($queryParams['limit']) ? max(1, min(100, (int)$queryParams['limit'])) : 30;
        $offset = isset($queryParams['offset']) ? max(0, (int)$queryParams['offset']) : 0;

        $history = $this->assessmentModel->getByUserId($userId, $limit, $offset);
        Response::success($history, 'Assessment history retrieved successfully');
    }

    public function getById(array $currentUser, int $id): void {
        $userId = (int)$currentUser['id'];
        $assessment = $this->assessmentModel->findById($id, $userId);

        if (!$assessment) {
            Response::notFound("Assessment record #$id was not found or is unauthorized");
        }

        Response::success($assessment, 'Assessment details retrieved');
    }

    /**
     * Sends cURL request to Python FastAPI microservice with fallback safety
     */
    private function callPythonAiService(array $payload): array {
        $url = rtrim(AI_SERVICE_URL, '/') . '/analyze';
        $jsonPayload = json_encode($payload);

        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $jsonPayload);
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            'Content-Type: application/json',
            'Accept: application/json'
        ]);
        curl_setopt($ch, CURLOPT_TIMEOUT, AI_REQUEST_TIMEOUT);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 3);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlError = curl_error($ch);
        curl_close($ch);

        if ($response !== false && $httpCode >= 200 && $httpCode < 300) {
            $decoded = json_decode($response, true);
            if (is_array($decoded) && isset($decoded['urgency_level'])) {
                return $decoded;
            }
        }

        // Graceful Fallback Triage when Python service is offline or starting up
        return $this->generateFallbackTriage($payload, $curlError ?: "HTTP status $httpCode");
    }

    /**
     * Clinical fallback engine ensuring the user always receives structured guidance
     */
    private function generateFallbackTriage(array $payload, string $fallbackReason): array {
        $symptomsLower = strtolower($payload['symptoms']);

        // Check for immediate emergency flags
        $emergencyKeywords = ['chest pain', 'heart attack', 'cannot breathe', 'difficulty breathing', 'shortness of breath', 'stroke', 'unconscious', 'seizure', 'severe bleeding', 'paralysis'];
        $isEmergency = false;
        foreach ($emergencyKeywords as $kw) {
            if (str_contains($symptomsLower, $kw)) {
                $isEmergency = true;
                break;
            }
        }

        if ($isEmergency) {
            return [
                'urgency_level'      => 'Emergency',
                'possible_causes'    => ['Acute Cardiopulmonary Distress', 'Severe Respiratory Compromise', 'Medical Emergency'],
                'health_information' => 'The symptoms you described may indicate a critical medical emergency requiring immediate attention.',
                'seek_care_advice'   => 'Call your local emergency number (such as 911 or 112) or proceed immediately to the nearest Emergency Department.',
                'emergency_warning'  => 'CRITICAL: Do not drive yourself. Await emergency medical personnel.',
                'disclaimer'         => 'SmartCareAI provides health information for reference only, not a clinical diagnosis.',
                'source'             => 'Clinical Safety Fallback Rule Engine'
            ];
        }

        // High / Moderate vs Mild
        if (str_contains($symptomsLower, 'fever') || str_contains($symptomsLower, 'vomiting') || str_contains($symptomsLower, 'pain')) {
            $urgency = 'Moderate';
            $causes = ['Viral Infection', 'Upper Respiratory Tract Infection', 'Gastrointestinal Irritation'];
            $advice = 'Monitor body temperature, ensure electrolyte fluid replacement, and schedule a consultation with a physician if symptoms do not improve within 48 hours.';
        } else {
            $urgency = 'Mild';
            $causes = ['Dehydration', 'Tension / Fatigue', 'Mild Seasonal Allergy'];
            $advice = 'Rest in a quiet, well-ventilated room, stay adequately hydrated, and avoid strenuous physical exertion.';
        }

        return [
            'urgency_level'      => $urgency,
            'possible_causes'    => $causes,
            'health_information' => "Based on your reported symptoms of '{$payload['symptoms']}', your condition appears to be in the $urgency range.",
            'seek_care_advice'   => $advice,
            'emergency_warning'  => 'If you experience sudden severe pain, high persistent fever, or breathing difficulty, seek emergency medical care immediately.',
            'disclaimer'         => 'SmartCareAI provides health guidance for educational and triage reference only. It does not replace professional medical evaluation.',
            'source'             => 'Clinical Safety Rule Engine'
        ];
    }
}
