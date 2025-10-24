package hms.controller.patient;

import hms.dto.patient.PatientCreateRequest;
import hms.entity.patient.PatientP;
import hms.service.patient.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Set;

@Controller
@RequestMapping("/patients")
public class PatientWebController {

  private final PatientService service;

  public PatientWebController(PatientService service) {
    this.service = service;
  }

  @GetMapping("/list")
  public String listRedirect() { return "redirect:/patients"; }

  @GetMapping
  public String list(@RequestParam(required = false) String q,
                     @RequestParam(required = false) String gender,
                     @RequestParam(required = false)
                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dobFrom,
                     @RequestParam(required = false)
                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dobTo,
                     @RequestParam(required = false) Set<String> tags,
                     @RequestParam(defaultValue = "0") int page,
                     Model model) {

    Page<PatientP> results = service.search(
            q, gender, dobFrom, dobTo, tags,
            PageRequest.of(Math.max(0, page), 10,
                    Sort.by("lastName").ascending().and(Sort.by("firstName").ascending()))
    );

    model.addAttribute("patients", results);
    model.addAttribute("active", "patients");
    return "patients/list";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    model.addAttribute("form", new PatientCreateRequest(
            "", "", "", null,
            "", "", "", "", "", "", "", "", "", "", "", Set.of()
    ));
    model.addAttribute("active", "patients");
    return "patients/form";
  }

  @PostMapping
  public String create(@Valid @ModelAttribute("form") PatientCreateRequest form,
                       BindingResult binding) {
    if (binding.hasErrors()) return "patients/form";
    PatientP p = service.register(form, "receptionist");
    return "redirect:/patients/" + p.getExternalId(); // ← use UUID
  }

  /** View by externalId (UUID) */
  @GetMapping("/{id}")
  public String view(@PathVariable String id, Model model) {
    try {
      PatientP p = service.getOrThrow(id);              // throws if not found
      model.addAttribute("patientP", p);                // ← template expects 'patientP'
      model.addAttribute("timeline", service.timeline(id));
      model.addAttribute("active", "patients");
      return "patients/view";
    } catch (jakarta.persistence.EntityNotFoundException ex) {
      model.addAttribute("id", id);
      model.addAttribute("active", "patients");
      return "patients/not-found";
    }
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable String id, Model model) {
    var p = service.getOrThrow(id);
    var form = new PatientCreateRequest(
            p.getFirstName(), p.getLastName(), p.getGender(), p.getDateOfBirth(),
            p.getContact().getPhone(), p.getContact().getEmail(),
            p.getContact().getAddressLine1(), p.getContact().getAddressLine2(),
            p.getContact().getCity(), p.getContact().getState(), p.getContact().getPostalCode(),
            p.getMedical().getBloodType(), p.getMedical().getAllergies(),
            p.getMedical().getChronicConditions(), p.getMedical().getNotes(), p.getTags()
    );
    model.addAttribute("form", form);
    model.addAttribute("id", id);
    model.addAttribute("active", "patients");
    return "patients/form";
  }

  @PostMapping("/{id}")
  public String update(@PathVariable String id,
                       @Valid @ModelAttribute("form") PatientCreateRequest form,
                       BindingResult binding) {
    if (binding.hasErrors()) return "patients/form";
    service.update(id, form, "receptionist");
    return "redirect:/patients/" + id; // ← keep using externalId
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable String id, RedirectAttributes ra) {
    service.delete(id, "receptionist");
    ra.addFlashAttribute("toast", "Patient deleted.");
    return "redirect:/patients";
  }

  @GetMapping("/{id}/delete")
  public String deleteGetNotAllowed(@PathVariable String id, RedirectAttributes ra) {
    ra.addFlashAttribute("error", "Delete must be via form POST.");
    return "redirect:/patients/" + id;
  }
}
