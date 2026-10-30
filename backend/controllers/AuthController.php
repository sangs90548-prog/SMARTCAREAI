<?php
/**
 * SmartCareAI - Authentication Controller
 */

require_once __DIR__ . '/../models/User.php';
require_once __DIR__ . '/../middleware/AuthMiddleware.php';
require_once __DIR__ . '/../utils/Response.php';

class AuthController {
    private User $userModel;

    public function __construct() {
        $this->userModel = new User();
    }

    public function register(array $request): void {
        $name = trim($request['name'] ?? '');
        $email = trim($request['email'] ?? '');
        $password = $request['password'] ?? '';

        $errors = [];
        if (empty($name)) $errors['name'] = 'Full name is required';
        if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) $errors['email'] = 'Valid email address is required';
        if (empty($password) || strlen($password) < 6) $errors['password'] = 'Password must be at least 6 characters';

        if (!empty($errors)) {
            Response::error('Validation failed', 422, $errors);
        }

        if ($this->userModel->findByEmail($email)) {
            Response::error('An account with this email already exists', 409);
        }

        $userId = $this->userModel->create([
            'name'                     => $name,
            'email'                    => $email,
            'password'                 => $password,
            'age'                      => $request['age'] ?? 30,
            'gender'                   => $request['gender'] ?? 'Prefer not to say',
            'blood_type'               => $request['blood_type'] ?? 'Unknown',
            'phone'                    => $request['phone'] ?? null,
            'emergency_contact'        => $request['emergency_contact'] ?? null,
            'health_reminders_enabled' => $request['health_reminders_enabled'] ?? 1,
            'ai_tips_enabled'          => $request['ai_tips_enabled'] ?? 1
        ]);

        $token = $this->userModel->createToken($userId);
        $user = $this->userModel->findById($userId);

        Response::success([
            'user'  => $user,
            'token' => $token
        ], 'Registration successful', 201);
    }

    public function login(array $request): void {
        $email = trim($request['email'] ?? '');
        $password = $request['password'] ?? '';

        if (empty($email) || empty($password)) {
            Response::error('Email and password are required', 422);
        }

        $user = $this->userModel->findByEmail($email);
        if (!$user || !password_verify($password, $user['password_hash'])) {
            Response::error('Invalid email or password', 401);
        }

        $token = $this->userModel->createToken((int)$user['id']);
        $profile = $this->userModel->findById((int)$user['id']);

        Response::success([
            'user'  => $profile,
            'token' => $token
        ], 'Login successful');
    }

    public function logout(): void {
        $token = AuthMiddleware::getBearerToken();
        if ($token) {
            $this->userModel->revokeToken($token);
        }
        Response::success(null, 'Successfully logged out');
    }

    public function me(array $currentUser): void {
        $profile = $this->userModel->findById((int)$currentUser['id']);
        Response::success($profile, 'User profile retrieved');
    }
}
