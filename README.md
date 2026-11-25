# SmartCareAI

SmartCareAI is an Android healthcare companion that brings hospital discovery, appointment workflows, queue information, and AI-assisted symptom assessment together in one app. The project includes a Kotlin/Jetpack Compose Android client, a PHP REST API with a MySQL database, and a Python FastAPI service for AI and waiting-time endpoints.

> **Health notice:** SmartCareAI is for informational and educational use only. AI-generated assessments are not a diagnosis or a substitute for advice from a qualified healthcare professional. For an emergency, contact local emergency services.

## Features

- Discover and search hospitals, departments, and doctors.
- Register, sign in, and manage patient profile and preferences.
- Book and manage appointments, with notifications and queue-related screens.
- Submit symptom assessments and review assessment history.
- Use a FastAPI service for Gemini-powered symptom analysis and waiting-time estimates.
- Browse a sample hospital dataset and query hospital listings through the PHP API.

Some data and screens may use local sample content until the backend is configured and connected.

## Architecture

```text
Android app (Kotlin, Jetpack Compose)
              |
              +---- PHP REST API ---- MySQL
              |         |
              |         +------------ Google Places API (optional)
              |
              +---- Python FastAPI ---- Google Gemini API (optional)
```

## Tech stack

| Component | Technologies |
| --- | --- |
| Android | Kotlin, Jetpack Compose, AndroidX, Retrofit |
| Backend | PHP, PDO, JSON REST endpoints |
| Database | MySQL 8+ or MariaDB 10.4+ |
| AI service | Python, FastAPI, Pydantic, Google GenAI SDK |

## Repository layout

```text
app/                 Android application
backend/             PHP API, controllers, models, and configuration
database/            MySQL schema and sample data
ai_service/          FastAPI AI and waiting-time service
postman/             Postman API collection
README_BACKEND.md    Additional backend and API setup notes
```

## Prerequisites

- Android Studio and Android SDK 37 (the app is configured for compile SDK 37 and min SDK 24).
- Java 11 or a compatible JDK supported by the configured Gradle build.
- PHP with PDO MySQL enabled.
- MySQL 8+ or MariaDB 10.4+.
- Python 3.10+ for the AI service.
- Optional: Google Gemini API key and Google Places API key for their respective integrations.

## Getting started

### 1. Set up the database

Create the schema and sample records:

```powershell
mysql -u root -p < database/smartcareai.sql
```

> **Caution:** `database/smartcareai.sql` drops and recreates the project tables. Back up any existing `smartcareai` data before importing it.

Configure the PHP backend with environment variables for your database and optional integrations. For example, in PowerShell:

```powershell
$env:DB_HOST = "127.0.0.1"
$env:DB_PORT = "3306"
$env:DB_NAME = "smartcareai"
$env:DB_USER = "root"
$env:DB_PASS = "your-local-database-password"
$env:AI_SERVICE_URL = "http://127.0.0.1:8000"
$env:GOOGLE_PLACES_API_KEY = "your-google-places-key"
```

The backend configuration has development defaults; use your own database credentials and do not commit secrets.

### 2. Start the PHP API

From the repository root, start PHP's built-in server with the public entry point as the router:

```powershell
php -S 127.0.0.1:8080 -t backend/public backend/public/index.php
```

Check the health endpoint at `http://127.0.0.1:8080/health`.

### 3. Start the AI service (optional)

Create a local environment file from the example and add your Gemini API key:

```powershell
Copy-Item ai_service/.env.example ai_service/.env
```

Set `GEMINI_API_KEY` in `ai_service/.env`, then install and run the service:

```powershell
Set-Location ai_service
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -r requirements.txt
python main.py
```

The service listens on `http://127.0.0.1:8000` by default. Its interactive API documentation is at `http://127.0.0.1:8000/docs`; its health endpoint is `http://127.0.0.1:8000/health`.

### 4. Configure and run the Android app

Open the repository in Android Studio and sync the Gradle project. Before running on an emulator or device, update the API base URLs in:

- `app/src/main/java/com/example/smartcareai/data/apiclient.kt` — PHP API.
- `app/src/main/java/com/example/smartcareai/data/RetrofitClient.kt` — waiting-time API.

The checked-in URLs point to a machine-specific network address. For the Android Studio emulator, the host computer is commonly reachable at `10.0.2.2`; with the commands above, the local development URLs would typically be:

```text
PHP API:         http://10.0.2.2:8080/index.php/
AI/wait-time API: http://10.0.2.2:8000/
```

For a physical device, use your development computer's LAN IP and ensure the device can reach the relevant ports. Do not use these development HTTP URLs in production; deploy the services behind HTTPS and configure the app to use the deployed endpoints.

## API overview

The PHP API is served from the public entry point. Common routes include:

| Method | Route | Purpose |
| --- | --- | --- |
| `GET` | `/health` | Backend and database health |
| `POST` | `/auth/register` | Register an account |
| `POST` | `/auth/login` | Sign in |
| `GET` | `/hospitals` | List hospitals |
| `GET` | `/hospitals/search` | Search hospitals |
| `GET` | `/doctors` | List doctors |
| `POST` | `/appointments` | Book an appointment (authentication required) |
| `POST` | `/assessments/analyze` | Submit a symptom assessment (authentication required) |

Protected routes use a bearer token. See `backend/routes/api.php` for the complete route list and import `postman/SmartCareAI.postman_collection.json` into Postman to exercise the API.

The AI service exposes `GET /health`, `POST /analyze`, and `POST /predict-waiting-time`.

## Validation

Run the Android unit tests:

```powershell
.\gradlew.bat testDebugUnitTest
```

Run the Python waiting-time smoke check:

```powershell
python ai_service/test_waiting_time.py
```

## Security and deployment notes

- Keep Gemini, Google Places, and database credentials out of source control. Use local environment variables or secret management in deployment.
- The sample HTTP endpoints and development settings are not suitable for production. Use HTTPS, restrict CORS to trusted origins, and configure production credentials and error reporting.
- Avoid using real patient information in development or when testing AI features.
