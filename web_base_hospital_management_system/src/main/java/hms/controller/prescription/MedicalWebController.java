package hms.controller.prescription;

import hms.entity.prescription.MedicalRecord;
import hms.repository.prescription.MedicalRecordRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/medical/records")
public class MedicalWebController {

    private static final Logger log = LoggerFactory.getLogger(MedicalWebController.class);

    private final MedicalRecordRepository repo;

    public MedicalWebController(MedicalRecordRepository repo) {
        this.repo = repo;
    }

    /** List all medical records,  yah*/
    @GetMapping
    public String list(Model model) {
        List<MedicalRecord> records = repo.findAll();
        model.addAttribute("records", records);
        return "prescription/medical_records";
    }


    @GetMapping("/add")
    public String addRedirect() {
        return "redirect:/medical/records/new";
    }

    /**  create form */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("record", new MedicalRecord());
        return "prescription/medical_record_form";
    }

    /** Create a record */
    @PostMapping
    public String create(@ModelAttribute("record") @Valid MedicalRecord form,
                         BindingResult errors,
                         RedirectAttributes ra) {
        if (errors.hasErrors()) {
            return "prescription/medical_record_form";
        }

        if (form.getAdmissionDate() == null && form.getVisitDate() != null) {
            form.setAdmissionDate(form.getVisitDate());
        }
        repo.save(form);
        ra.addFlashAttribute("successMessage", "Medical record created.");
        return "redirect:/medical/records";
    }

    /**  edit form */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<MedicalRecord> opt = repo.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Medical record not found.");
            return "redirect:/medical/records";
        }
        model.addAttribute("record", opt.get());
        return "prescription/medical_record_form";
    }

    /** Update existing record  */
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @ModelAttribute("record") @Valid MedicalRecord form,
                         BindingResult errors,
                         RedirectAttributes ra,
                         Model model) {
        if (errors.hasErrors()) {
            // keep the form visible with validation messages
            model.addAttribute("record", form);
            return "prescription/medical_record_form";
        }

        Optional<MedicalRecord> opt = repo.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Medical record not found.");
            return "redirect:/medical/records";
        }

        MedicalRecord existing = opt.get();


        existing.setPatientName(form.getPatientName());
        existing.setDoctorName(form.getDoctorName());
        existing.setDiagnosis(form.getDiagnosis());
        existing.setVisitDate(form.getVisitDate());

        if (form.getAdmissionDate() == null && form.getVisitDate() != null) {
            existing.setAdmissionDate(form.getVisitDate());
        } else {
            existing.setAdmissionDate(form.getAdmissionDate());
        }

        existing.setNotes(form.getNotes());
        existing.setLabResults(form.getLabResults());

        repo.save(existing);
        ra.addFlashAttribute("successMessage", "Medical record updated.");
        return "redirect:/medical/records";
    }


    @GetMapping("/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<MedicalRecord> opt = repo.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Medical record not found.");
            return "redirect:/medical/records";
        }
        model.addAttribute("record", opt.get());
        return "prescription/medical_record_delete_confirm";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        if (!repo.existsById(id)) {
            ra.addFlashAttribute("errorMessage", "Medical record not found.");
            return "redirect:/medical/records";
        }
        repo.deleteById(id);
        log.info("Deleted medical record id={}", id);
        ra.addFlashAttribute("successMessage", "Medical record deleted.");
        return "redirect:/medical/records";
    }


    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "OK";
    }
}
