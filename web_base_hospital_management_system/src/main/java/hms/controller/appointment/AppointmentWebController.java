package hms.controller.appointment;

import hms.dto.appointment.AppointmentCreateRequest;
import hms.dto.appointment.RescheduleRequest;
import hms.entity.appointment.Appointment;
import hms.service.appointment.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentWebController {

    private final AppointmentService service;

    public AppointmentWebController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Appointment> p = service.upcoming(page);
        model.addAttribute("page", p);
        model.addAttribute("appts", p.getContent());
        model.addAttribute("title", "Appointments");
        return "appointments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        var patients = service.allPatients();
        var doctors = service.allDoctors();

        Long defaultPatientId = patients.isEmpty() ? null : patients.get(0).getId();
        Long defaultDoctorId = doctors.isEmpty() ? null : doctors.get(0).getId();
        LocalDate defaultDate = LocalDate.now().plusDays(1);

        model.addAttribute("form", new AppointmentCreateRequest(
                defaultPatientId, defaultDoctorId, defaultDate, "10:00", "IN_PERSON", null
        ));
        model.addAttribute("patients", patients);
        model.addAttribute("doctors", doctors);
        model.addAttribute("slots", (defaultDoctorId == null)
                ? List.of()
                : service.availableSlots(defaultDoctorId, defaultDate));
        return "appointments/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") AppointmentCreateRequest form,
                         BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            fillFormModel(model, form);
            return "appointments/form";
        }
        try {
            Appointment a = service.book(form, "web");
            return "redirect:/appointments/" + a.getExternalId();
        } catch (ResponseStatusException ex) {
            binding.reject("book.error", ex.getReason());
            fillFormModel(model, form);
            return "appointments/form";
        } catch (RuntimeException ex) {
            binding.reject("book.error", ex.getMessage());
            fillFormModel(model, form);
            return "appointments/form";
        }
    }

    @GetMapping("/{ext}")
    public String view(@PathVariable String ext, Model model) {
        try {
            model.addAttribute("appt", service.get(ext));
            return "appointments/view";
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            }
            throw ex;
        }
    }

    @PostMapping("/{ext}/cancel")
    public String cancel(@PathVariable String ext) {
        try {
            service.cancel(ext, "web");
            return "redirect:/appointments/" + ext;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            }
            throw ex;
        }
    }

    @GetMapping("/{ext}/reschedule")
    public String rescheduleForm(@PathVariable String ext, Model model) {
        try {
            var appt = service.get(ext);
            model.addAttribute("appt", appt);
            model.addAttribute("doctors", service.allDoctors());

            var form = new RescheduleRequest(
                    appt.getDoctor().getId(),
                    appt.getStartTime().toLocalDate(),
                    appt.getStartTime().toLocalTime().toString(),
                    null
            );
            model.addAttribute("id", appt.getExternalId());
            model.addAttribute("currentDoctor", "Keep current");
            model.addAttribute("form", form);
            model.addAttribute("slots", service.availableSlots(appt.getDoctor().getId(), appt.getStartTime().toLocalDate()));
            return "appointments/reschedule";
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            }
            throw ex;
        }
    }

    @PostMapping("/{ext}/reschedule")
    public String reschedule(@PathVariable String ext,
                             @Valid @ModelAttribute("form") RescheduleRequest form,
                             BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            var appt = service.get(ext);
            model.addAttribute("appt", appt);
            model.addAttribute("doctors", service.allDoctors());
            Long doctorId = (form.newDoctorId() != null) ? form.newDoctorId() : appt.getDoctor().getId();
            model.addAttribute("id", appt.getExternalId());
            model.addAttribute("slots", service.availableSlots(doctorId, form.newDate()));
            return "appointments/reschedule";
        }
        try {
            service.reschedule(ext, form, "web");
            return "redirect:/appointments/" + ext;
        } catch (ResponseStatusException ex) {
            binding.reject("reschedule.error", ex.getReason());
            var appt = service.get(ext);
            model.addAttribute("appt", appt);
            model.addAttribute("doctors", service.allDoctors());
            Long doctorId = (form.newDoctorId() != null) ? form.newDoctorId() : appt.getDoctor().getId();
            model.addAttribute("id", appt.getExternalId());
            model.addAttribute("slots", service.availableSlots(doctorId, form.newDate()));
            return "appointments/reschedule";
        } catch (RuntimeException ex) {
            binding.reject("reschedule.error", ex.getMessage());
            var appt = service.get(ext);
            model.addAttribute("appt", appt);
            model.addAttribute("doctors", service.allDoctors());
            Long doctorId = (form.newDoctorId() != null) ? form.newDoctorId() : appt.getDoctor().getId();
            model.addAttribute("id", appt.getExternalId());
            model.addAttribute("slots", service.availableSlots(doctorId, form.newDate()));
            return "appointments/reschedule";
        }
    }

    @GetMapping("/slots")
    @ResponseBody
    public List<String> slots(@RequestParam Long doctorId, @RequestParam String date) {
        return service.availableSlots(doctorId, LocalDate.parse(date));
    }


    @PostMapping("/{ext}/delete")
    public String delete(@PathVariable String ext, RedirectAttributes ra) {
        try {
            service.delete(ext);
            ra.addFlashAttribute("success", "Appointment deleted");
            return "redirect:/appointments";
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("error", "Appointment not found");
            return "redirect:/appointments";
        }
    }

    private void fillFormModel(Model model, AppointmentCreateRequest form) {
        model.addAttribute("patients", service.allPatients());
        model.addAttribute("doctors", service.allDoctors());
        model.addAttribute("slots", (form.doctorId() != null && form.date() != null)
                ? service.availableSlots(form.doctorId(), form.date())
                : List.of());
    }
}
