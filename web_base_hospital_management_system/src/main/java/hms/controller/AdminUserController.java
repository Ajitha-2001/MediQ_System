package hms.controller;

import hms.entity.User;
import hms.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService users;
    public AdminUserController(UserService users) { this.users = users; }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newUser(@RequestParam(defaultValue = "PATIENT") String type, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("form", new User());
        return "admin/user-form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@ModelAttribute("form") User form,
                         @RequestParam String type,
                         RedirectAttributes ra) {
        try {
            switch (type.toUpperCase()) {
                case "ADMIN" -> users.createAdmin(form);
                case "STAFF" -> users.createStaff(form);
                case "PATIENT" -> users.createPatient(form);
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            }
            ra.addFlashAttribute("ok", "User created and email sent.");
            return "redirect:/admin/users/new?type=" + type;
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/users/new?type=" + type;
        }
    }
}
