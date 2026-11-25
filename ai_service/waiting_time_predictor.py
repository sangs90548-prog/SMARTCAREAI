
"""
SmartCareAI - Hospital Waiting Time Prediction Engine
Estimates patient waiting time based on queue and doctor availability.
"""

import math


def predict_waiting_time(
    waiting_patients: int,
    available_doctors: int,
    avg_consultation_minutes: int = 15,
    priority_patients: int = 0
) -> dict:

    # Validate inputs
    if waiting_patients < 0:
        raise ValueError("Waiting patients cannot be negative.")

    if available_doctors < 0:
        raise ValueError("Available doctors cannot be negative.")

    if avg_consultation_minutes <= 0:
        raise ValueError("Consultation time must be greater than zero.")

    if priority_patients < 0 or priority_patients > waiting_patients:
        raise ValueError("Invalid priority patient count.")

    # Handle no available doctors
    if available_doctors == 0:
        return {
            "estimated_wait_minutes": None,
            "status": "Doctors unavailable",
            "message": "No doctors are currently available. Please check with hospital staff."
        }

    if waiting_patients == 0:
        return {
            "estimated_wait_minutes": 0,
            "status": "Low",
            "message": "No waiting queue. You may proceed for consultation."
        }

    # Estimate queue workload
    regular_patients = waiting_patients - priority_patients

    # Priority patients are assumed to be handled first
    effective_queue = regular_patients + (priority_patients * 0.5)

    # Calculate estimated waiting time
    raw_wait = (effective_queue * avg_consultation_minutes) / available_doctors

    estimated_wait = math.ceil(raw_wait)

    # Classify waiting time
    if estimated_wait <= 15:
        status = "Low"
    elif estimated_wait <= 30:
        status = "Moderate"
    elif estimated_wait <= 60:
        status = "High"
    else:
        status = "Very High"

    return {
        "estimated_wait_minutes": estimated_wait,
        "status": status,
        "message": f"Estimated waiting time is approximately {estimated_wait} minutes."
    }
