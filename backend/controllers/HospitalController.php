<?php
/**
 * SmartCareAI - Hospital Controller
 */

require_once __DIR__ . '/../models/Hospital.php';
require_once __DIR__ . '/../utils/Response.php';

class HospitalController {
    private Hospital $hospitalModel;

    public function __construct() {
        $this->hospitalModel = new Hospital();
    }

    public function getAll(array $queryParams): void {
        $limit = isset($queryParams['limit']) ? max(1, min(100, (int)$queryParams['limit'])) : 50;
        $offset = isset($queryParams['offset']) ? max(0, (int)$queryParams['offset']) : 0;

        $hospitals = $this->hospitalModel->getAll($limit, $offset);
        Response::success($hospitals, 'Hospitals retrieved successfully');
    }

    public function getById(string $id): void {
        $hospital = $this->hospitalModel->findById($id);
        if (!$hospital) {
            Response::notFound("Hospital with ID '$id' was not found");
        }
        Response::success($hospital, 'Hospital details retrieved');
    }

    public function search(array $queryParams): void {
        $query = $queryParams['q'] ?? '';
        $crowd = $queryParams['crowd'] ?? null;

        $results = $this->hospitalModel->search($query, $crowd);
        Response::success($results, 'Search results retrieved');
    }

    public function placesSearch(array $queryParams): void {
        $query = $queryParams['q'] ?? '';
        $nextToken = $queryParams['next_page_token'] ?? null;

        require_once __DIR__ . '/../services/GooglePlacesService.php';
        $service = new GooglePlacesService();
        $googleResult = $service->searchHospitals($query, $nextToken);

        if ($googleResult['success'] && !empty($googleResult['hospitals'])) {
            Response::success([
                'source' => 'google_places',
                'next_page_token' => $googleResult['next_page_token'],
                'hospitals' => $googleResult['hospitals']
            ], 'Google Places hospitals retrieved');
            return;
        }

        $localHospitals = $this->hospitalModel->search($query);
        Response::success([
            'source' => 'local_database',
            'next_page_token' => null,
            'hospitals' => $localHospitals
        ], 'Local database hospitals retrieved');
    }

    public function getDoctors(string $hospitalId): void {
        $hospital = $this->hospitalModel->findById($hospitalId);
        if (!$hospital) {
            Response::notFound("Hospital with ID '$hospitalId' was not found");
        }
        $doctors = $this->hospitalModel->getDoctors($hospitalId);
        Response::success($doctors, 'Hospital doctors retrieved');
    }
}
