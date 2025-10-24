package hms.controller.prescription;

import hms.entity.prescription.MedicalRecord;
import hms.entity.prescription.Prescription;
import hms.service.prescription.MedicalRecordService;
import hms.service.prescription.PrescriptionService;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/prescriptions")
public class PrescriptionWebController {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionWebController.class);

    private final PrescriptionService prescriptionService;
    private final MedicalRecordService medicalRecordService;

    public PrescriptionWebController(PrescriptionService prescriptionService,
                                     MedicalRecordService medicalRecordService) {
        this.prescriptionService = prescriptionService;
        this.medicalRecordService = medicalRecordService;
    }


    @GetMapping
    public String prescriptionsHome(Model model) {
        List<MedicalRecord> allRecords = medicalRecordService.listAll();

        if (allRecords.isEmpty()) {
            model.addAttribute("record", null);
            model.addAttribute("prescriptions", List.of());
            model.addAttribute("allRecords", List.of());
            return "prescription/prescriptions";
        }

        Long firstRecordId = allRecords.get(0).getId();
        logger.info("Redirecting to first record: {}", firstRecordId);
        return "redirect:/prescriptions/record/" + firstRecordId;
    }

    @GetMapping("/record/{recordId}")
    public String listByRecord(@PathVariable Long recordId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            MedicalRecord record = medicalRecordService.get(recordId);
            List<MedicalRecord> allRecords = medicalRecordService.listAll();

            model.addAttribute("record", record);
            model.addAttribute("allRecords", allRecords);
            model.addAttribute("prescriptions", prescriptionService.listByRecord(recordId));
            return "prescription/prescriptions";
        } catch (EntityNotFoundException ex) {
            logger.warn("Record not found id={}", recordId);
            redirectAttributes.addFlashAttribute("errorMessage", "Medical record not found!");
            return "redirect:/prescriptions";
        }
    }

    // ---------------- Create prescription ----------------
    @GetMapping("/record/{recordId}/new")
    public String createForm(@PathVariable Long recordId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            MedicalRecord record = medicalRecordService.get(recordId);
            model.addAttribute("record", record);
            model.addAttribute("prescription", new Prescription());
            return "prescription/prescription_form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Medical record not found!");
            return "redirect:/prescriptions";
        }
    }

    @PostMapping("/record/{recordId}/new")
    public String create(@PathVariable Long recordId,
                         @Valid @ModelAttribute("prescription") Prescription prescription,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            try {
                MedicalRecord record = medicalRecordService.get(recordId);
                model.addAttribute("record", record);
            } catch (EntityNotFoundException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "Medical record not found!");
                return "redirect:/prescriptions";
            }
            return "prescription/prescription_form";
        }

        if (prescription.getIssuedAt() == null) {
            prescription.setIssuedAt(LocalDate.now().atStartOfDay());
        }

        try {
            prescriptionService.create(recordId, prescription);
            redirectAttributes.addFlashAttribute("successMessage", "Prescription created successfully!");
            return "redirect:/prescriptions/record/" + recordId;
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Medical record not found!");
            return "redirect:/prescriptions";
        }
    }

    // ---------------- Edit prescription ----------------
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            Prescription prescription = prescriptionService.get(id);
            model.addAttribute("prescription", prescription);
            model.addAttribute("record", prescription.getMedicalRecord());
            return "prescription/prescription_edit";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Prescription not found!");
            return "redirect:/prescriptions";
        }
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("prescription") Prescription prescription,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        Prescription original;
        try {
            original = prescriptionService.get(id);
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Prescription not found!");
            return "redirect:/prescriptions";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("record", original.getMedicalRecord());
            return "prescription/prescription_edit";
        }


        prescription.setId(original.getId());
        prescription.setMedicalRecord(original.getMedicalRecord());

        if (prescription.getDoctorName() == null || prescription.getDoctorName().isBlank()) {
            prescription.setDoctorName(original.getDoctorName());
        }
        if (prescription.getPatientName() == null || prescription.getPatientName().isBlank()) {
            prescription.setPatientName(original.getPatientName());
        }

        prescriptionService.update(id, prescription);
        redirectAttributes.addFlashAttribute("successMessage", "Prescription updated successfully!");
        return "redirect:/prescriptions/record/" + original.getMedicalRecord().getId();
    }

    // ---------------- Delete prescription ----------------
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            Prescription prescription = prescriptionService.get(id);
            Long recordId = (prescription.getMedicalRecord() != null)
                    ? prescription.getMedicalRecord().getId()
                    : null;

            prescriptionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Prescription deleted successfully!");

            if (recordId != null) {
                return "redirect:/prescriptions/record/" + recordId;
            }
            return "redirect:/prescriptions";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Prescription not found!");
            return "redirect:/prescriptions";
        }
    }
}
