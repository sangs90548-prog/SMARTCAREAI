
from waiting_time_predictor import predict_waiting_time

result = predict_waiting_time(
    waiting_patients=20,
    available_doctors=2,
    avg_consultation_minutes=15,
    priority_patients=2
)

print("SmartCareAI Waiting Time Prediction")
print("--------------------------------")
print("Waiting Patients: 20")
print("Available Doctors: 2")
print("Estimated Waiting Time:", result["estimated_wait_minutes"], "minutes")
print("Queue Status:", result["status"])
print("Message:", result["message"])
