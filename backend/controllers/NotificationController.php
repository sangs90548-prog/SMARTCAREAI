<?php
/**
 * SmartCareAI - Notification Controller
 */

require_once __DIR__ . '/../models/Notification.php';
require_once __DIR__ . '/../utils/Response.php';

class NotificationController {
    private Notification $notificationModel;

    public function __construct() {
        $this->notificationModel = new Notification();
    }

    public function list(array $currentUser, array $queryParams): void {
        $userId = (int)$currentUser['id'];
        $limit = isset($queryParams['limit']) ? max(1, min(100, (int)$queryParams['limit'])) : 50;

        $notifications = $this->notificationModel->getByUserId($userId, $limit);
        Response::success($notifications, 'Notifications retrieved');
    }

    public function markRead(array $currentUser, int $id): void {
        $userId = (int)$currentUser['id'];
        $success = $this->notificationModel->markAsRead($id, $userId);

        if (!$success) {
            Response::notFound("Notification #$id not found or already read");
        }

        Response::success(null, 'Notification marked as read');
    }

    public function markAllRead(array $currentUser): void {
        $userId = (int)$currentUser['id'];
        $this->notificationModel->markAllAsRead($userId);
        Response::success(null, 'All notifications marked as read');
    }
}
