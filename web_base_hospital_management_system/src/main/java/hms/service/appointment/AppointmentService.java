package hms.service.appointment;

import hms.dto.appointment.AppointmentCreateRequest;
import hms.dto.appointment.RescheduleRequest;
import hms.entity.appointment.*;
import hms.repository.appointments.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointments;
    private final DoctorRepository doctors;
    private final DoctorScheduleRepository schedules;
    private final PatientRepository patients;

    public AppointmentService(AppointmentRepository appointments,
                              DoctorRepository doctors,
                              DoctorScheduleRepository schedules,
                              PatientRepository patients) {
        this.appointments = appointments;
        this.doctors = doctors;
        this.schedules = schedules;
        this.patients = patients;
    }

    public Page<Appointment> upcoming(int page) {
        return appointments.findByStartTimeGreaterThanEqualOrderByStartTimeAsc(
                LocalDateTime.now().minusHours(1), PageRequest.of(Math.max(page, 0), 10));
    }

    public Appointment get(String externalId) {
        return appointments.findByExternalId(externalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
    }

    public List<LocalTime> availability(Long doctorId, LocalDate date) {
        var sched = schedules.findFirstByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek()).orElse(null);
        if (sched == null) return List.of();

        List<LocalTime> free = new ArrayList<>();
        LocalTime cursor = sched.getStartTime();
        while (!cursor.plusMinutes(sched.getSlotMinutes()).isAfter(sched.getEndTime())) {
            LocalDateTime start = LocalDateTime.of(date, cursor);
            LocalDateTime end = start.plusMinutes(sched.getSlotMinutes());
            boolean busy = appointments.existsByDoctorIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                    doctorId, AppointmentStatus.BOOKED, end, start);
            if (!busy && start.isAfter(LocalDateTime.now())) free.add(cursor);
            cursor = cursor.plusMinutes(sched.getSlotMinutes());
        }
        return free;
    }

    public List<String> availableSlots(Long doctorId, LocalDate date) {
        return availability(doctorId, date).stream()
                .map(t -> String.format("%02d:%02d", t.getHour(), t.getMinute()))
                .toList();
    }

    @Transactional
    public Appointment book(AppointmentCreateRequest req, String actor) {
        var patient = patients.findById(req.patientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        var doctor = doctors.findById(req.doctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        var sched = schedules.findFirstByDoctorIdAndDayOfWeek(doctor.getId(), req.date().getDayOfWeek())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor not available on that day"));

        LocalTime time = LocalTime.parse(req.time());
        LocalDateTime start = LocalDateTime.of(req.date(), time);
        LocalDateTime end = start.plusMinutes(sched.getSlotMinutes());

        boolean clash = appointments.existsByDoctorIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                doctor.getId(), AppointmentStatus.BOOKED, end, start);
        if (clash) throw new ResponseStatusException(HttpStatus.CONFLICT, "Selected slot is not available");

        var appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setStatus(AppointmentStatus.BOOKED);
        appt.setChannel((req.channel() == null || req.channel().isBlank()) ? "IN_PERSON" : req.channel());
        appt.setNotes(req.notes());

        if (appt.getExternalId() == null || appt.getExternalId().isBlank()) {
            appt.setExternalId(UUID.randomUUID().toString());
        }

        if (actor != null && !actor.isBlank()) {
            appt.setNotes((appt.getNotes() == null ? "" : appt.getNotes() + " | ") + "Booked by " + actor);
        }
        return appointments.save(appt);
    }

    @Transactional
    public Appointment cancel(String externalId, String actor) {
        var appt = get(externalId);
        appt.setStatus(AppointmentStatus.CANCELLED);
        if (actor != null && !actor.isBlank()) {
            appt.setNotes((appt.getNotes() == null ? "" : appt.getNotes() + " | ") + "Cancelled by " + actor);
        }
        return appt;
    }

    @Transactional
    public Appointment reschedule(String externalId, RescheduleRequest req, String actor) {
        var appt = get(externalId);
        Long newDoctorId = (req.newDoctorId() != null) ? req.newDoctorId() : appt.getDoctor().getId();
        var doctor = doctors.findById(newDoctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        var sched = schedules.findFirstByDoctorIdAndDayOfWeek(newDoctorId, req.newDate().getDayOfWeek())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor not available on that day"));

        LocalTime time = LocalTime.parse(req.newTime());
        LocalDateTime start = LocalDateTime.of(req.newDate(), time);
        LocalDateTime end = start.plusMinutes(sched.getSlotMinutes());

        boolean clash = appointments.existsByDoctorIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                newDoctorId, AppointmentStatus.BOOKED, end, start);
        if (clash) throw new ResponseStatusException(HttpStatus.CONFLICT, "Selected slot is not available");

        appt.setDoctor(doctor);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setStatus(AppointmentStatus.RESCHEDULED);

        String note = (req.notes() == null ? "" : req.notes());
        if (actor != null && !actor.isBlank()) {
            note = (note.isBlank() ? "" : note + " | ") + "Rescheduled by " + actor;
        }
        if (!note.isBlank()) {
            appt.setNotes((appt.getNotes() == null ? "" : appt.getNotes() + " | ") + note);
        }
        return appt;
    }


    @Transactional
    public void delete(String externalId) {
        var appt = appointments.findByExternalId(externalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
        appointments.delete(appt);
    }

    public List<Doctor> allDoctors() { return doctors.findAll(); }
    public List<Patient> allPatients() { return patients.findAll(); }
}
