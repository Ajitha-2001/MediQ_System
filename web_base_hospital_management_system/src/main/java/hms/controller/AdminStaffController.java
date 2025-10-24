package hms.controller;

import hms.dto.StaffForm;
import hms.entity.Role;
import hms.entity.User;
import hms.repository.RoleRepository;
import hms.service.AdminStaffService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffController {     // singleton Design Pattern

    private final AdminStaffService staffService;
    private final RoleRepository roleRepository;

    public AdminStaffController(AdminStaffService staffService, RoleRepository roleRepository) {
        this.staffService = staffService;
        this.roleRepository = roleRepository;
    }


    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       Model model) {
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("users", staffService.listAll(q));
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin-staff-list";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        StaffForm form = new StaffForm();
        form.setEnabled(true);
        model.addAttribute("form", form);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin-staff-form";
    }


    @PostMapping("/new")
    public String createSubmit(@Valid @ModelAttribute("form") StaffForm form,     //Decorator Pattern
                               BindingResult result,
                               Model model,
                               RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "admin-staff-form";
        }
        try {
            staffService.create(form);
            ra.addFlashAttribute("ok", "Staff account created.");
            return "redirect:/admin/staff";
        } catch (Exception e) {
            result.reject("error", e.getMessage());
            model.addAttribute("allRoles", roleRepository.findAll());
            return "admin-staff-form";
        }
    }


    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        User u = staffService.findById(id).orElseThrow();
        StaffForm form = new StaffForm();
        form.setId(u.getId());
        form.setUsername(u.getUsername());
        form.setEmail(u.getEmail());
        form.setFirstName(u.getFirstName());
        form.setLastName(u.getLastName());
        form.setPhoneNumber(u.getPhone());
        form.setEnabled(u.isEnabled());
        form.setRoles(u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        model.addAttribute("form", form);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin-staff-form";
    }


    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute("form") StaffForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "admin-staff-form";
        }
        try {
            staffService.update(id, form);
            ra.addFlashAttribute("ok", "Staff account updated.");
            return "redirect:/admin/staff";
        } catch (Exception e) {
            result.reject("error", e.getMessage());
            model.addAttribute("allRoles", roleRepository.findAll());
            return "admin-staff-form";
        }
    }


    @PostMapping("/{id}/enable")
    public String enable(@PathVariable Long id, RedirectAttributes ra) {
        staffService.enable(id);
        ra.addFlashAttribute("ok", "User enabled.");
        return "redirect:/admin/staff";
    }


    @PostMapping("/{id}/disable")
    public String disable(@PathVariable Long id, RedirectAttributes ra) {
        staffService.disable(id);
        ra.addFlashAttribute("ok", "User disabled.");
        return "redirect:/admin/staff";
    }


    @PostMapping("/{id}/assign")
    public String assign(@PathVariable Long id, @RequestParam String roleName, RedirectAttributes ra) {
        staffService.assignRole(id, roleName);
        ra.addFlashAttribute("ok", "Role assigned.");
        return "redirect:/admin/staff";
    }


    @PostMapping("/{id}/remove")
    public String remove(@PathVariable Long id, @RequestParam String roleName, RedirectAttributes ra) {
        staffService.removeRole(id, roleName);
        ra.addFlashAttribute("ok", "Role removed.");
        return "redirect:/admin/staff";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         Principal principal,
                         RedirectAttributes ra) {
        try {
            String currentUser = principal != null ? principal.getName() : null;
            staffService.delete(id, currentUser);
            ra.addFlashAttribute("ok", "Staff account deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("err", "Delete failed: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }
}
