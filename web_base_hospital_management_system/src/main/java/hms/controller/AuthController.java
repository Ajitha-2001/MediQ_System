// src/main/java/hms/controller/AuthController.java
package hms.controller;

import hms.dto.AdminRegistrationDto;
import hms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    /** Landing page (public marketing/hero page). */
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    /** Staff login (Spring Security form login). */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("showBootstrapAdmin", !users.hasAnyAdmin());
        return "login";
    }

    /** ✅ Patient login page (renders templates/loginP.html). */
    @GetMapping("/patient/login")
    public String patientLogin() {
        return "loginP";
    }

    /** Optional alias for patient login (/loginP). */
    @GetMapping("/loginP")
    public String patientLoginAlias() {
        return "loginP";
    }

    /** ✅ Get Started page (renders templates/getstart.html). */
    @GetMapping("/getstart")
    public String getStart() {
        return "getstart";
    }

    /** Optional alias (/get-start). */
    @GetMapping("/get-start")
    public String getStartAlias() {
        return "getstart";
    }

    /** First-admin bootstrap form (only visible when no admin exists). */
    @GetMapping("/bootstrap-admin")
    public String bootstrapAdminForm(Model model) {
        if (users.hasAnyAdmin()) return "redirect:/login";
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new AdminRegistrationDto());
        }
        return "bootstrap-admin";
    }

    /** Handle first-admin bootstrap submit. */
    @PostMapping("/bootstrap-admin")
    public String bootstrapAdminSubmit(@Valid @ModelAttribute("form") AdminRegistrationDto form,
                                       BindingResult br,
                                       RedirectAttributes ra) {
        if (users.hasAnyAdmin()) return "redirect:/login";

        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", br);
            ra.addFlashAttribute("form", form);
            return "redirect:/bootstrap-admin";
        }

        try {
            users.bootstrapFirstAdmin(form);
            return "redirect:/login?registeredAdmin=true";
        } catch (Exception ex) {
            ra.addFlashAttribute("form", form);
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/bootstrap-admin";
        }
    }

    /** Access denied page used by SecurityConfig. */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
