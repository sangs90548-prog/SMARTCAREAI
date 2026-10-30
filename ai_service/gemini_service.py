"""
SmartCareAI - Google Gemini AI Service
Integrates Google Gemini API to analyze patient symptoms and return structured triage guidance.
"""

import os
import json
import logging
from typing import Dict, Any
from dotenv import load_dotenv

from symptom_analyzer import SymptomRequest, SymptomAnalysisResponse, detect_emergency_signs

# Load environment variables
load_dotenv()

logger = logging.getLogger("SmartCareAI.Gemini")

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "").strip()
GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-2.5-flash")

SYSTEM_INSTRUCTION = """You are the SmartCareAI Clinical Triage Assistant.
Your mission is to provide safe, objective, and responsible health information to patients reporting symptoms.

STRICT MEDICAL & ETHICAL MANDATES:
1. NEVER declare a definitive medical diagnosis. Phrase potential causes cautiously as "possible causes" or "differential considerations".
2. NEVER prescribe medications, dosages, or drug treatments.
3. CLEARLY classify urgency level into exactly one of: ["Emergency", "High", "Moderate", "Mild", "Low"].
4. Provide actionable guidance on when and where the patient should seek professional medical evaluation.
5. If any symptoms indicate potential life-threatening emergencies (e.g., severe chest pressure, breathing distress, stroke symptoms), set urgency_level to "Emergency" and provide an urgent warning.
6. Always return a valid JSON object matching the requested schema. Do NOT include markdown code fences (```json), return raw JSON only.

JSON Output Schema:
{
  "urgency_level": "Emergency" | "High" | "Moderate" | "Mild" | "Low",
  "possible_causes": ["Cause 1", "Cause 2", "Cause 3"],
  "health_information": "Objective explanation of what may cause these symptoms",
  "seek_care_advice": "Recommendation on when to visit a doctor or clinic",
  "emergency_warning": "Warning text if urgent, or null if mild/moderate",
  "disclaimer": "SmartCareAI provides health assessment guidance for educational purposes only. It is not a medical diagnosis or treatment plan.",
  "source": "Google Gemini AI"
}
"""


class GeminiService:
    def __init__(self):
        self.api_key = os.getenv("GEMINI_API_KEY", "").strip()
        self.model = os.getenv("GEMINI_MODEL", "gemini-2.5-flash")
        self.client = None

        if self.api_key:
            try:
                from google import genai
                self.client = genai.Client(api_key=self.api_key)
                logger.info("Initialized Google GenAI client successfully.")
            except Exception as e:
                logger.warning(f"Could not initialize GenAI client: {e}. Will attempt fallback.")

    async def analyze_symptoms(self, req: SymptomRequest) -> SymptomAnalysisResponse:
        # 1. Pre-screen for immediate critical emergencies
        emergency_hit = detect_emergency_signs(req.symptoms)
        if emergency_hit:
            logger.info("Emergency signs triggered immediate safety protocol.")
            return emergency_hit

        # 2. Attempt Google Gemini Generation if client is configured
        if self.client:
            try:
                prompt = (
                    f"Patient Information:\n"
                    f"- Reported Symptoms: {req.symptoms}\n"
                    f"- Age: {req.age}\n"
                    f"- Duration: {req.duration}\n"
                    f"- Patient-Perceived Severity: {req.severity}\n\n"
                    f"Analyze these symptoms and provide structured JSON triage output following your system instructions."
                )

                response = self.client.models.generate_content(
                    model=self.model,
                    contents=prompt,
                    config={
                        "system_instruction": SYSTEM_INSTRUCTION,
                        "response_mime_type": "application/json",
                        "temperature": 0.2,
                    }
                )

                if response and response.text:
                    clean_text = response.text.strip()
                    # Strip any potential markdown wrappers if model added them
                    if clean_text.startswith("```"):
                        lines = clean_text.splitlines()
                        if lines[0].startswith("```"):
                            lines = lines[1:]
                        if lines and lines[-1].startswith("```"):
                            lines = lines[:-1]
                        clean_text = "\n".join(lines).strip()

                    parsed = json.loads(clean_text)
                    return SymptomAnalysisResponse(
                        urgency_level=parsed.get("urgency_level", "Moderate"),
                        possible_causes=parsed.get("possible_causes", ["General Health Concern"]),
                        health_information=parsed.get("health_information", "No additional detail provided."),
                        seek_care_advice=parsed.get("seek_care_advice", "Consult your physician for personalized evaluation."),
                        emergency_warning=parsed.get("emergency_warning"),
                        disclaimer=parsed.get("disclaimer", "SmartCareAI assessment is for information only."),
                        source=f"Google Gemini ({self.model})"
                    )
            except Exception as ex:
                logger.error(f"Gemini API request failed: {ex}. Falling back to clinical safety rule engine.")

        # 3. Resilient Fallback Clinical Logic
        return self._generate_heuristic_assessment(req)

    def _generate_heuristic_assessment(self, req: SymptomRequest) -> SymptomAnalysisResponse:
        s = req.symptoms.lower()

        if any(w in s for w in ["fever", "chills", "sweats"]):
            urgency = "Moderate"
            causes = ["Viral Syndrome", "Upper Respiratory Infection", "Seasonal Influenza"]
            info = "Elevated temperature and chills typically indicate the body's immune system is actively responding to an inflammatory or infectious stimulus."
            advice = "Rest, maintain adequate electrolyte and water intake. Seek clinical evaluation if fever exceeds 102°F (38.9°C), persists beyond 3 days, or is accompanied by stiff neck."
            warning = "If confusion, lethargy, or extreme shortness of breath occurs, seek emergency care immediately."

        elif any(w in s for w in ["cough", "throat", "congestion", "sinus", "runny nose"]):
            urgency = "Mild"
            causes = ["Common Cold (Rhinovirus)", "Allergic Rhinitis", "Pharyngitis"]
            info = "Upper respiratory tract symptoms often resolve with supportive home care and hydration."
            advice = "Humidify air, stay hydrated, and rest. Consult a physician if persistent coughing lasts longer than 10-14 days or causes chest pain."
            warning = None

        elif any(w in s for w in ["stomach", "nausea", "vomit", "diarrhea", "abdominal"]):
            urgency = "Moderate"
            causes = ["Gastroenteritis", "Foodborne Indigestion", "Acid Reflux / Gastritis"]
            info = "Gastrointestinal disturbances are commonly caused by viral gastroenteritis or dietary irritation."
            advice = "Sip clear fluids, oral rehydration solutions (ORS), and follow a bland diet (BRAT). See a doctor if you cannot retain fluids for 24 hours."
            warning = "Seek immediate emergency help if experiencing severe localized abdominal pain, bloody stool, or persistent vomiting."

        elif any(w in s for w in ["headache", "migraine", "dizziness"]):
            urgency = "Moderate" if req.severity.lower() == "severe" else "Mild"
            causes = ["Tension Headache", "Cervical Strain", "Dehydration / Lack of Sleep"]
            info = "Headaches frequently relate to muscle strain, stress, dehydration, or prolonged computer display usage."
            advice = "Rest in a dark, quiet room and drink water. If headaches recur frequently, schedule a doctor consultation."
            warning = "A sudden 'thunderclap' headache or headache with visual changes warrants immediate emergency attention."

        else:
            urgency = "Mild" if req.severity.lower() == "mild" else "Moderate"
            causes = ["Fatigue or Stress Response", "Mild Musculoskeletal Strain", "Underlying Metabolic Imbalance"]
            info = f"Reported symptoms of '{req.symptoms}' are classified as {urgency.lower()} priority based on initial triage criteria."
            advice = "Keep a log of when symptoms occur. If they persist or worsen over the next 48 to 72 hours, book an appointment with your doctor."
            warning = "Always seek immediate medical care if you develop unexpected sharp pain or difficulty breathing."

        return SymptomAnalysisResponse(
            urgency_level=urgency,
            possible_causes=causes,
            health_information=info,
            seek_care_advice=advice,
            emergency_warning=warning,
            disclaimer="SmartCareAI provides health assessment guidance for educational purposes only. It is not a clinical diagnosis.",
            source="Clinical Safety Heuristic Engine"
        )
