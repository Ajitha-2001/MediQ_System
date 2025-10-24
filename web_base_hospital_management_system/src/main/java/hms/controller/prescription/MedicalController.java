package hms.controller.prescription;

import hms.entity.prescription.MedicalRecord;
import hms.repository.prescription.MedicalRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical/records")
public class MedicalController {

    private final MedicalRecordRepository repo;

    public MedicalController(MedicalRecordRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<MedicalRecord> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> one(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MedicalRecord> create(@RequestBody MedicalRecord record) {
        record.setId(null);
        if (record.getAdmissionDate() == null && record.getVisitDate() != null) {
            record.setAdmissionDate(record.getVisitDate());
        }
        return ResponseEntity.ok(repo.save(record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> update(@PathVariable Long id, @RequestBody MedicalRecord incoming) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setPatientName(incoming.getPatientName());
                    existing.setDoctorName(incoming.getDoctorName());
                    existing.setDiagnosis(incoming.getDiagnosis());
                    existing.setVisitDate(incoming.getVisitDate());
                    if (incoming.getAdmissionDate() == null && incoming.getVisitDate() != null) {
                        existing.setAdmissionDate(incoming.getVisitDate());
                    } else {
                        existing.setAdmissionDate(incoming.getAdmissionDate());
                    }
                    existing.setNotes(incoming.getNotes());
                    existing.setLabResults(incoming.getLabResults());
                    return ResponseEntity.ok(repo.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
