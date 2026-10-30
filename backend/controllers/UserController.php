<?php
/**
 * SmartCareAI - User Controller
 */

require_once __DIR__ . '/../models/User.php';
require_once __DIR__ . '/../utils/Response.php';

class UserController {
    private User $userModel;

    public function __construct() {
        $this->userModel = new User();
    }

    public function getProfile(array $currentUser): void {
        $profile = $this->userModel->findById((int)$currentUser['id']);
        if (!$profile) {
            Response::notFound('User profile not found');
        }
        Response::success($profile, 'Profile retrieved successfully');
    }

    public function updateProfile(array $currentUser, array $request): void {
        $userId = (int)$currentUser['id'];

        $updateData = [];
        if (isset($request['name']) && trim($request['name']) !== '') {
            $updateData['name'] = trim($request['name']);
        }
        if (isset($request['age'])) {
            $updateData['age'] = (int)$request['age'];
        }
        if (isset($request['gender'])) {
            $updateData['gender'] = trim($request['gender']);
        }
        if (isset($request['blood_type'])) {
            $updateData['blood_type'] = trim($request['blood_type']);
        }
        if (isset($request['phone'])) {
            $updateData['phone'] = trim($request['phone']);
        }
        if (isset($request['emergency_contact'])) {
            $updateData['emergency_contact'] = trim($request['emergency_contact']);
        }

        if (empty($updateData)) {
            Response::error('No update parameters provided', 400);
        }

        $this->userModel->updateProfile($userId, $updateData);
        $updated = $this->userModel->findById($userId);

        Response::success($updated, 'Profile updated successfully');
    }

    public function updatePreferences(array $currentUser, array $request): void {
        $userId = (int)$currentUser['id'];

        $reminders = isset($request['health_reminders_enabled']) ? (int)$request['health_reminders_enabled'] : null;
        $tips = isset($request['ai_tips_enabled']) ? (int)$request['ai_tips_enabled'] : null;

        if ($reminders === null && $tips === null) {
            Response::error('Provide at least one preference to update', 400);
        }

        $this->userModel->updatePreferences($userId, $reminders, $tips);
        $updated = $this->userModel->findById($userId);

        Response::success($updated, 'Preferences updated successfully');
    }
}
