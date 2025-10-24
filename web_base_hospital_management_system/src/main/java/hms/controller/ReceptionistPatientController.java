package hms.controller;

import hms.entity.User;
import hms.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/receptionist/patients")
public class ReceptionistPatientController {

    private final UserService users;
    public ReceptionistPatientController(UserService users) { this.users = users; }

    @GetMapping("/new")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public String newPatient(Model model) {
        model.addAttribute("form", new User());
        return "receptionist/patient-form";
    }

    @PostMapping
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public String create(@ModelAttribute("form") User form, RedirectAttributes ra) {
        try {
            users.createPatient(form);
            ra.addFlashAttribute("ok", "Patient created and email sent.");
            return "redirect:/receptionist/patients/new";
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/receptionist/patients/new";
        }
    }
}
