# SmartCareAI - Complete Backend & AI System

This directory and the accompanying modules provide the complete backend, database, and AI service architecture for **SmartCareAI**.

---

## 1. Architecture Overview

```
                      +-----------------------------+
                      |     Android Application     |
                      |   (XML / Kotlin Frontends)  |
                      +--------------+--------------+
                                     |
                                REST APIs (JSON)
                                     |
                                     v
                      +-----------------------------+
                      |      PHP 8.2+ Backend       |
                      | (PDO, Auth, Router, Models) |
                      |    Hosted on XAMPP/Apache   |
                      +-------+-------------+-------+
                              |             |
           MySQL 8 Prepared   |             | HTTP REST
             Transactions     |             | (cURL JSON)
                              v             v
             +------------------+   +----------------------+
             | MySQL 8 Database |   |   Python AI Service  |
             |   smartcareai    |   |       (FastAPI)      |
             +------------------+   +----------+-----------+
                                               |
                                        Google Gemini API
                                               v
                                    +--------------------+
                                    |  Google AI Studio  |
                                    |  gemini-2.5-flash  |
                                    +--------------------+
```

---

## 2. Directory Structure

```
SmartCareAI/
├── database/
│   └── smartcareai.sql                     # Full MySQL schema & verified demo data
├── backend/
│   ├── config/
│   │   ├── config.php                      # DB credentials, AI Service URL, security
│   │   └── database.php                    # PDO connection singleton (UTF-8mb4)
│   ├── models/
│   │   ├── User.php                        # Auth, profile, preferences, API tokens
│   │   ├── Hospital.php                    # Discovery, directory, filters, ratings
│   │   ├── Doctor.php                      # Specializations, availability check
│   │   ├── Appointment.php                 # Booking, duplicate prevention, cancellations
│   │   ├── Assessment.php                  # AI triage persistence & patient history
│   │   └── Notification.php                # System & appointment notifications
│   ├── controllers/
│   │   ├── AuthController.php              # Register, login, logout, me
│   │   ├── UserController.php              # Profile management & preference toggles
│   │   ├── HospitalController.php          # Hospital endpoints
│   │   ├── DoctorController.php            # Doctor endpoints
│   │   ├── AppointmentController.php       # Appointment operations & notifications
│   │   ├── AssessmentController.php        # AI triage coordination & history
│   │   └── NotificationController.php      # User notification retrieval & read flags
│   ├── middleware/
│   │   └── AuthMiddleware.php              # Bearer token validation & user injection
│   ├── routes/
│   │   └── api.php                         # REST router dispatching endpoints
│   ├── public/
│   │   ├── index.php                       # Single entry point with CORS support
│   │   └── .htaccess                       # URL rewriting for Apache
│   └── utils/
│       └── Response.php                    # Standard JSON responses
├── ai_service/
│   ├── main.py                             # FastAPI microservice on port 8000
│   ├── gemini_service.py                   # Google Gemini API integration & fallbacks
│   ├── symptom_analyzer.py                 # Pydantic schemas & emergency screening
│   ├── requirements.txt                    # Python dependencies
│   └── .env.example                        # Environment variable template
└── postman/
    └── SmartCareAI.postman_collection.json  # Complete Postman v2.1 test collection
```

---

## 3. Database Setup (MySQL 8 / phpMyAdmin)

1. Open **XAMPP Control Panel** or run MySQL Server on port `3306`.
2. Open **phpMyAdmin** (`http://localhost/phpmyadmin`) or MySQL Command Line.
3. Import `database/smartcareai.sql`:
   ```bash
   mysql -u root -p < database/smartcareai.sql
   ```
4. Tables created:
   - `users`: Patient accounts with encrypted bcrypt passwords.
   - `api_tokens`: Secure SHA-256 hashed API tokens with expiration dates.
   - `hospitals`: Verified hospital profiles, wait times, and crowd levels.
   - `departments`: Hospital clinical departments.
   - `doctors`: Specialists, available consultation slots, and fees.
   - `appointments`: Patient bookings with unique reference numbers.
   - `assessments`: Persisted symptom assessments linked to user accounts.
   - `notifications`: User notifications with read statuses.

### Demo Credentials
- **Email**: `alex.johnson@example.com`
- **Password**: `Password123!`

---

## 4. Running the PHP Backend

The backend is deployed to:
- Workspace: `backend/`
- XAMPP Web Root: `C:\xampp\htdocs\smartcareai\backend`

### Access URL
- Via XAMPP Apache: `http://localhost/smartcareai/backend/public/api/`
- Via PHP CLI Built-in Server:
  ```bash
  cd backend/public
  php -S 127.0.0.1:8080
  ```

---

## 5. Running the Python AI Microservice (FastAPI)

1. Navigate to the AI service directory:
   ```bash
   cd ai_service
   ```
2. Install Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. Copy `.env.example` to `.env` and set your Google Gemini API key:
   ```env
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   GEMINI_MODEL=gemini-2.5-flash
   PORT=8000
   ```
4. Run the FastAPI microservice:
   ```bash
   python main.py
   ```
   Or with Uvicorn:
   ```bash
   uvicorn main:app --host 127.0.0.1 --port 8000 --reload
   ```
5. Interactive OpenAPI Documentation is accessible at:
   - `http://127.0.0.1:8000/docs`

---

## 6. Testing with Postman

1. Open Postman.
2. Click **Import** and select `postman/SmartCareAI.postman_collection.json`.
3. Set the collection variable `baseUrl` (defaults to `http://localhost/smartcareai/backend/public`).
4. Execute **User Login** or **User Registration**. The test script will **automatically** extract and save the `authToken` variable.
5. All subsequent protected requests (Assessments, Profile, Appointments) will authenticate automatically using the Bearer token.
