package com.example.smartcareai

import com.example.smartcareai.data.models.AppointmentStatus
import com.example.smartcareai.data.models.AppointmentType
import com.example.smartcareai.data.repository.SmartCareRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SmartCareRepositoryTest {

    private lateinit var repository: SmartCareRepository

    @Before
    fun setUp() {
        repository = SmartCareRepository()
    }

    @Test
    fun testInitialHospitalsLoaded() {
        val list = repository.hospitals.value
        assertTrue("Initial hospitals should not be empty", list.isNotEmpty())
        assertEquals(4, list.size)
        val recommended = list.find { it.isRecommended }
        assertNotNull("Should have a recommended hospital", recommended)
        assertEquals("MetroCare Central Hospital", recommended?.name)
    }

    @Test
    fun testAddAppointmentGeneratesTokenAndNotification() {
        val initialAppCount = repository.appointments.value.size
        val hospital = repository.hospitals.value.first()
        val doctor = repository.doctors.first()

        val newApp = repository.addAppointment(
            appointmentType = AppointmentType.IN_PERSON,
            hospital = hospital,
            doctor = doctor,
            date = "Tomorrow",
            timeSlot = "10:30 AM",
            patientName = "Test Patient",
            patientPhone = "+1555000111",
            accessibilityNeeds = "Wheelchair"
        )

        assertNotNull("Appointment should be created", newApp)
        assertEquals(initialAppCount + 1, repository.appointments.value.size)
        assertEquals("Test Patient", newApp.patientName)
        assertTrue(newApp.referenceNumber.startsWith("SCA-"))

        val activeToken = repository.activeQueueToken.value
        assertNotNull("Queue token should be assigned automatically", activeToken)
        assertEquals(newApp.id, activeToken?.appointmentId)

        val latestNotif = repository.notifications.value.first()
        assertTrue(latestNotif.title.contains("Appointment Confirmed"))
    }

    @Test
    fun testCancelAppointmentUpdatesStatus() {
        val hospital = repository.hospitals.value.first()
        val doctor = repository.doctors.first()

        val app = repository.addAppointment(
            appointmentType = AppointmentType.IN_PERSON,
            hospital = hospital,
            doctor = doctor,
            date = "Tomorrow",
            timeSlot = "10:30 AM",
            patientName = "Test Patient",
            patientPhone = "+1555000111",
            accessibilityNeeds = ""
        )

        repository.cancelAppointment(app.id)

        val updatedApp = repository.getAppointmentById(app.id)
        assertNotNull(updatedApp)
        assertEquals(AppointmentStatus.CANCELLED, updatedApp?.status)
        assertNull("Queue token should be cleared upon cancellation", repository.activeQueueToken.value)
    }

    @Test
    fun testRescheduleAppointment() {
        val hospital = repository.hospitals.value.first()
        val doctor = repository.doctors.first()

        val app = repository.addAppointment(
            appointmentType = AppointmentType.IN_PERSON,
            hospital = hospital,
            doctor = doctor,
            date = "Tomorrow",
            timeSlot = "10:30 AM",
            patientName = "Test Patient",
            patientPhone = "+1555000111",
            accessibilityNeeds = ""
        )

        repository.rescheduleAppointment(app.id, "Friday, Oct 25", "02:00 PM")

        val updatedApp = repository.getAppointmentById(app.id)
        assertEquals("Friday, Oct 25", updatedApp?.date)
        assertEquals("02:00 PM", updatedApp?.timeSlot)
        assertEquals(AppointmentStatus.UPCOMING, updatedApp?.status)
    }

    @Test
    fun testSubmitSupportTicket() {
        val ticket = repository.submitSupportTicket(
            name = "Jane Doe",
            email = "jane@example.com",
            category = "Appointments",
            subject = "Issue with booking",
            message = "Need assistance with rescheduling slot."
        )

        assertNotNull(ticket)
        assertTrue(ticket.id.startsWith("TKT-"))
        assertEquals("Jane Doe", ticket.name)
        assertEquals(1, repository.supportTickets.value.size)
    }
}
