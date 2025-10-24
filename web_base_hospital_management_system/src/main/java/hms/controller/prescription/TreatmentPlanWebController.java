package hms.controller.prescription;

import hms.entity.prescription.MedicalRecord;
import hms.entity.prescription.TreatmentPlan;
import hms.service.prescription.MedicalRecordService;
import hms.service.prescription.TreatmentPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medical/records/{recordId}/plans")
public class TreatmentPlanWebController {

    private final TreatmentPlanService treatmentPlanService;
    private final MedicalRecordService medicalRecordService;

    public TreatmentPlanWebController(TreatmentPlanService treatmentPlanService,
                                      MedicalRecordService medicalRecordService) {
        this.treatmentPlanService = treatmentPlanService;
        this.medicalRecordService = medicalRecordService;
    }

    /**
     * ✅ List all treatment plans for a medical record
     */
    @GetMapping
    public String list(@PathVariable Long recordId, Model model) {
        MedicalRecord record = medicalRecordService.get(recordId);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        }

        model.addAttribute("record", record);
        model.addAttribute("plans", treatmentPlanService.listByRecord(recordId));
        return "prescription/treatment_plans";
    }

    /**
     * ✅ Show the form to create a new treatment plan
     */
    @GetMapping("/new")
    public String showCreateForm(@PathVariable Long recordId, Model model) {
        MedicalRecord record = medicalRecordService.get(recordId);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        }

        // Prepare a new empty plan and bind it to the form
        model.addAttribute("record", record);
        model.addAttribute("plan", new TreatmentPlan());
        return "prescription/treatment_plan_form";
    }


    @PostMapping
    public String create(@PathVariable Long recordId,
                         @Valid @ModelAttribute("plan") TreatmentPlan plan,
                         BindingResult errors,
                         Model model,
                         RedirectAttributes redirect) {


        MedicalRecord record = medicalRecordService.get(recordId);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        }


        if (errors.hasErrors()) {
            model.addAttribute("record", record);
            return "prescription/treatment_plan_form";
        }

        // Save the new treatment plan
        treatmentPlanService.create(recordId, plan);
        redirect.addFlashAttribute("successMessage", "✅ Treatment plan created successfully!");
        return "redirect:/medical/records/" + recordId + "/plans";
    }


    @GetMapping("/{planId}/edit")
    public String showEditForm(@PathVariable Long recordId,
                               @PathVariable Long planId,
                               Model model) {

        MedicalRecord record = medicalRecordService.get(recordId);
        TreatmentPlan plan = treatmentPlanService.get(planId);

        if (record == null || plan == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record or plan not found");
        }

        model.addAttribute("record", record);
        model.addAttribute("plan", plan);
        return "prescription/treatment_plan_form";
    }


    @PostMapping("/{planId}")
    public String update(@PathVariable Long recordId,
                         @PathVariable Long planId,
                         @Valid @ModelAttribute("plan") TreatmentPlan plan,
                         BindingResult errors,
                         Model model,
                         RedirectAttributes redirect) {

        MedicalRecord record = medicalRecordService.get(recordId);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        }

        if (errors.hasErrors()) {
            model.addAttribute("record", record);
            model.addAttribute("plan", plan);
            return "prescription/treatment_plan_form";
        }

        treatmentPlanService.update(planId, plan);
        redirect.addFlashAttribute("successMessage", "✅ Treatment plan updated successfully!");
        return "redirect:/medical/records/" + recordId + "/plans";
    }


    @PostMapping("/{planId}/delete")
    public String delete(@PathVariable Long recordId,
                         @PathVariable Long planId,
                         RedirectAttributes redirect) {

        treatmentPlanService.delete(planId);
        redirect.addFlashAttribute("successMessage", "🗑️ Treatment plan deleted successfully!");
        return "redirect:/medical/records/" + recordId + "/plans";
    }
}
