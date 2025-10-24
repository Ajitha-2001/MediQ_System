package hms.service.prescription;

import hms.entity.prescription.DoctorPP;
import hms.repository.prescription.PrescriptionDoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DoctorService {
    private final PrescriptionDoctorRepository repo;

    public DoctorService(PrescriptionDoctorRepository repo) {
        this.repo = repo;
    }

    // CREATE
    public DoctorPP create(DoctorPP d) {
        d.setFullName(addPrefix(d.getFullName()));  // 🔹 ensure "Dr." is added
        return repo.save(d);
    }

    // READ (all)
    public List<DoctorPP> list() {
        return repo.findAll();
    }

    // READ (single)
    public DoctorPP get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
    }

    // UPDATE
    public DoctorPP update(Long id, DoctorPP d) {
        DoctorPP existing = get(id);
        existing.setFullName(addPrefix(d.getFullName())); // 🔹 keep "Dr." prefix
        existing.setGender(d.getGender());
        existing.setDateOfBirth(d.getDateOfBirth());
        existing.setAge(d.getAge());
        existing.setEmail(d.getEmail());
        existing.setPhone(d.getPhone());
        existing.setSpecialization(d.getSpecialization());
        if (d.getPhotoPath() != null) {
            existing.setPhotoPath(d.getPhotoPath());
        }
        return repo.save(existing);
    }

    // DELETE
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Doctor not found");
        }
        repo.deleteById(id);
    }

    // Helper: ensure Dr. prefix
    private String addPrefix(String name) {
        if (name == null || name.isBlank()) return name;
        if (name.toLowerCase().startsWith("dr.")) {
            return name; // already has Dr.
        }
        return "Dr. " + name;
    }
}