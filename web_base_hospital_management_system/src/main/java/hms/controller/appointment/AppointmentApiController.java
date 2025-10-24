package hms.controller.appointment;

import hms.dto.appointment.AppointmentCreateRequest;
import hms.dto.appointment.RescheduleRequest;
import hms.entity.appointment.Appointment;
import hms.service.appointment.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentApiController {

    private final AppointmentService service;

    public AppointmentApiController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Appointment> list(@RequestParam(defaultValue = "0") int page) {
        return service.upcoming(page);
    }

    @GetMapping("/availability")
    public List<LocalTime> availability(@RequestParam Long doctorId,
                                        @RequestParam LocalDate date) {
        return service.availability(doctorId, date);
    }

    @PostMapping
    public Appointment create(@Valid @RequestBody AppointmentCreateRequest req) {
        return service.book(req, "api");
    }

    @PostMapping("/{id}/cancel")
    public Appointment cancel(@PathVariable String id) {
        return service.cancel(id, "api");
    }

    @PostMapping("/{id}/reschedule")
    public Appointment reschedule(@PathVariable String id,
                                  @Valid @RequestBody RescheduleRequest req) {
        return service.reschedule(id, req, "api");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
