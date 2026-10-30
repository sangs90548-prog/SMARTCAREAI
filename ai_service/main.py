"""
SmartCareAI - Python AI Service (FastAPI)
Microservice delivering intelligent symptom analysis powered by Google Gemini API.
"""

import os
import logging
from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv

from symptom_analyzer import SymptomRequest, SymptomAnalysisResponse
from gemini_service import GeminiService

load_dotenv()

# Logging configuration
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s"
)
logger = logging.getLogger("SmartCareAI.Main")

app = FastAPI(
    title="SmartCareAI AI Service",
    description="Intelligent Health & Symptom Triage Microservice powered by Google Gemini API",
    version="1.0.0"
)

# CORS Middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize Gemini Service
gemini_service = GeminiService()


@app.get("/")
def root():
    return {
        "service": "SmartCareAI AI Microservice",
        "status": "operational",
        "documentation": "/docs",
        "health": "/health"
    }


@app.get("/health")
def health_check():
    has_key = bool(os.getenv("GEMINI_API_KEY", "").strip())
    return {
        "status": "healthy",
        "gemini_api_configured": has_key,
        "model": os.getenv("GEMINI_MODEL", "gemini-2.5-flash")
    }


@app.post(
    "/analyze",
    response_model=SymptomAnalysisResponse,
    status_code=status.HTTP_200_OK,
    summary="Analyze patient symptoms with Gemini AI"
)
async def analyze_symptoms_endpoint(payload: SymptomRequest):
    try:
        if not payload.symptoms or len(payload.symptoms.strip()) < 2:
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Symptoms field cannot be empty."
            )

        logger.info(f"Received symptom assessment request: '{payload.symptoms[:50]}...'")
        result = await gemini_service.analyze_symptoms(payload)
        return result

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error during symptom analysis: {e}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"An error occurred while assessing symptoms: {str(e)}"
        )


if __name__ == "__main__":
    import uvicorn
    host = os.getenv("HOST", "127.0.0.1")
    port = int(os.getenv("PORT", 8000))
    logger.info(f"Starting SmartCareAI FastAPI service on http://{host}:{port}")
    uvicorn.run("main:app", host=host, port=port, reload=False)
