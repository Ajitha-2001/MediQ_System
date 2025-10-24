package hms.controller;

import hms.entity.User;
import hms.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/itadmin/users")
public class ItAdminUserController {

    private final UserService users;
    public ItAdminUserController(UserService users) { this.users = users; }

    @GetMapping("/new")
    @PreAuthorize("hasRole('IT_ADMIN')")
    public String newUser(@RequestParam(defaultValue = "PATIENT") String type, Model model) {
        // guard
        if ("ADMIN".equalsIgnoreCase(type)) type = "PATIENT";
        model.addAttribute("type", type);
        model.addAttribute("form", new User());
        return "itadmin/user-form";
    }

    @PostMapping
    @PreAuthorize("hasRole('IT_ADMIN')")
    public String create(@ModelAttribute("form") User form,
                         @RequestParam String type,
                         RedirectAttributes ra) {
        try {
            if ("ADMIN".equalsIgnoreCase(type)) {
                throw new IllegalArgumentException("IT Admin cannot create ADMIN accounts.");
            }
            switch (type.toUpperCase()) {
                case "STAFF" -> users.createStaff(form);
                case "PATIENT" -> users.createPatient(form);
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            }
            ra.addFlashAttribute("ok", "User created and email sent.");
            return "redirect:/itadmin/users/new?type=" + type;
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/itadmin/users/new?type=" + type;
        }
    }
}
