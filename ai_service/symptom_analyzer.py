"""
SmartCareAI - Symptom Analyzer Data Structures and Clinical Pre-Screening
"""

from typing import List, Optional
from pydantic import BaseModel, Field


class SymptomRequest(BaseModel):
    symptoms: str = Field(..., description="Description of symptoms reported by the patient", min_length=2)
    age: Optional[int] = Field(default=30, ge=0, le=125, description="Patient age in years")
    duration: Optional[str] = Field(default="Recent", description="Duration of symptoms, e.g. 2 days, 1 week")
    severity: Optional[str] = Field(default="Moderate", description="Self-reported severity: Mild, Moderate, Severe")


class SymptomAnalysisResponse(BaseModel):
    urgency_level: str = Field(..., description="Urgency classification: Emergency, High, Moderate, Mild, Low")
    possible_causes: List[str] = Field(default_factory=list, description="Non-definitive potential underlying causes")
    health_information: str = Field(..., description="Objective educational context regarding the symptoms")
    seek_care_advice: str = Field(..., description="Actionable recommendation on clinical consultation timing")
    emergency_warning: Optional[str] = Field(default=None, description="Critical warning if emergency signs are present")
    disclaimer: str = Field(
        default="SmartCareAI provides educational health assessment information only. It is NOT a medical diagnosis and cannot prescribe treatments. Always consult a qualified healthcare professional.",
        description="Mandatory medical legal disclaimer"
    )
    source: str = Field(default="Gemini-AI", description="Analysis engine source identifier")


CRITICAL_EMERGENCY_SIGNS = [
    "chest pain", "crushing pressure", "left arm pain", "difficulty breathing",
    "cannot breathe", "shortness of breath", "severe shortness of breath",
    "slurred speech", "facial drooping", "sudden numbness", "sudden paralysis",
    "loss of consciousness", "unresponsive", "coughing up blood", "severe head injury",
    "anaphylaxis", "swelling of tongue", "throat closing", "uncontrolled bleeding"
]


def detect_emergency_signs(symptoms_text: str) -> Optional[SymptomAnalysisResponse]:
    """
    Pre-screens the input for immediate life-threatening emergency keywords.
    Ensures safety prior to or in conjunction with AI generation.
    """
    lower = symptoms_text.lower()
    for sign in CRITICAL_EMERGENCY_SIGNS:
        if sign in lower:
            return SymptomAnalysisResponse(
                urgency_level="Emergency",
                possible_causes=[
                    "Acute Cardiopulmonary or Neurological Event",
                    "Critical Medical Emergency"
                ],
                health_information=(
                    f"The symptoms described ('{sign}') are commonly associated with critical "
                    "medical emergencies that require immediate, on-site clinical intervention."
                ),
                seek_care_advice=(
                    "Call emergency services immediately (911, 112, or local ambulance). "
                    "Do NOT wait to see if symptoms improve. Do NOT drive yourself to the hospital."
                ),
                emergency_warning=(
                    "EMERGENCY ALERT: Immediate professional medical care is critical. "
                    "Have someone stay with the patient while emergency services arrive."
                ),
                source="Pre-Screen Emergency Safety Protocol"
            )
    return None
