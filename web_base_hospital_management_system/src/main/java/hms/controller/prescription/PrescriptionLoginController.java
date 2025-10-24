package hms.controller.prescription;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import hms.service.prescription.PrescriptionService;
import hms.entity.prescription.PrescriptionRecord;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import hms.entity.prescription.Prescription;

@Controller
@RequestMapping("/prescriptions/login") // Changed to avoid conflict with WebController
public class PrescriptionLoginController {

    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping("/list")
    public String prescriptionsPage(Model model) {
        PrescriptionRecord record = prescriptionService.getSampleRecord();
        model.addAttribute("record", record);
        return "prescription/prescriptions";
    }

    @PostMapping("/record/{id}/add")
    public String addPrescription(@PathVariable Long id,
                                  @RequestParam String medication,
                                  @RequestParam String dosage,
                                  @RequestParam String instructions,
                                  Model model) {
        Prescription prescription = new Prescription();
        prescription.setRecordId(id);
        prescription.setMedication(medication);
        prescription.setDosage(dosage);
        prescription.setInstructions(instructions);

        prescriptionService.save(prescription);
        model.addAttribute("msg", "Prescription added successfully!");
        return "redirect:/prescriptions/login/list";
    }
}
