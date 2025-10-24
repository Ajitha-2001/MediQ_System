package hms.controller;

import hms.dto.RegistrationDto;
import hms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UserService users;

    public RegistrationController(UserService users) {
        this.users = users;
    }

    /** Trim all incoming String fields; turn empty strings into nulls */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /** Alias: keep older links working (/registration → /register) */
    @GetMapping("/registration")
    public String redirectOldRegistrationUrl() {
        return "redirect:/register";
    }

    /** GET /register – show the form, model attribute name must match the template: ${registration} */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("registration")) {
            model.addAttribute("registration", new RegistrationDto());
        }
        return "register"; // templates/register.html
    }

    /** POST /register – handle submit */
    @PostMapping("/register")
    public String handleRegistration(@ModelAttribute("registration") @Valid RegistrationDto form,
                                     BindingResult result,
                                     Model model) {

        // Bean Validation first
        if (result.hasErrors()) {
            return "register";
        }

        // Cross-field validation: password match
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
            return "register";
        }

        try {
            users.register(form); // creates PATIENT, may send email, etc.

            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException ex) {
            // e.g., username/email already exist (surface as a top-level error banner)
            model.addAttribute("error", ex.getMessage());
            return "register";
        } catch (Exception ex) {
            model.addAttribute("error", "Registration failed");
            return "register";
        }
    }
}
