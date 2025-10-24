package hms.controller.prescription;

import hms.entity.prescription.DoctorPP;
import hms.entity.prescription.Specializations;
import hms.service.prescription.DoctorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;


@Controller

@RequestMapping("/prescription/doctors")
public class DoctorWebController {

    private final DoctorService service;

    public DoctorWebController(DoctorService service) {
        this.service = service;
    }

    // List all doctors
    @GetMapping
    public String listDoctors(Model model) {
        model.addAttribute("doctors", service.list());
        model.addAttribute("doctorForm", new DoctorPP()); // For the 'Add Doctor' modal/form
        model.addAttribute("specializations", Specializations.LIST); // For the specialization dropdown
        return "prescription/doctors"; // Renders templates/prescription/doctors.html
    }

    // Add a new doctor
    @PostMapping("/add")
    public String addDoctor(@ModelAttribute DoctorPP doctor,
                            @RequestParam("photo") MultipartFile photo,
                            RedirectAttributes ra) {
        try {
            if (!photo.isEmpty()) {

                String photoPath = savePhoto(photo);
                doctor.setPhotoPath(photoPath);
            }
            service.create(doctor);
            ra.addFlashAttribute("msg", "Doctor added successfully!");
        } catch (IllegalArgumentException | IOException e) {
            ra.addFlashAttribute("err", e.getMessage());
        }

        return "redirect:/prescription/doctors";
    }

    // View a single doctor's details
    @GetMapping("/{id}")
    public String viewDoctor(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", service.get(id));
        return "prescription/doctor_view"; // Renders templates/prescription/doctor_view.html
    }

    // Show the doctor edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", service.get(id));
        model.addAttribute("specializations", Specializations.LIST);
        return "prescription/doctor_edit"; // Renders templates/prescription/doctor_edit.html
    }


    @PostMapping("/{id}/edit")
    public String editDoctor(@PathVariable Long id,
                             @ModelAttribute DoctorPP doctor,
                             @RequestParam("photo") MultipartFile photo,
                             RedirectAttributes ra) {
        try {
            if (!photo.isEmpty()) {
                // Handle new photo upload
                String photoPath = savePhoto(photo);
                doctor.setPhotoPath(photoPath);
            }
            service.update(id, doctor);
            ra.addFlashAttribute("msg", "Doctor updated successfully!");
        } catch (IllegalArgumentException | IOException e) {
            ra.addFlashAttribute("err", e.getMessage());
        }

        return "redirect:/prescription/doctors";
    }

    // Delete a doctor
    @PostMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("msg", "Doctor deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("err", e.getMessage());
        }

        return "redirect:/prescription/doctors";
    }


    private String savePhoto(MultipartFile photo) throws IOException, IllegalArgumentException {
        // Validate size (max 2MB)
        if (photo.getSize() > (2 * 1024 * 1024)) {
            throw new IllegalArgumentException("File is too large! Maximum size is 2MB.");
        }

        // Validate type (only JPG/PNG)
        String contentType = photo.getContentType();
        if (contentType == null || !contentType.matches("image/(jpeg|png)")) {
            throw new IllegalArgumentException("Invalid file type! Only JPG and PNG images are allowed.");
        }

        String uploadDir = "uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create upload directory: " + uploadDir);
        }

        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        File dest = new File(dir.getAbsolutePath() + File.separator + fileName);
        photo.transferTo(dest);


        return "/" + uploadDir + fileName;
    }
}