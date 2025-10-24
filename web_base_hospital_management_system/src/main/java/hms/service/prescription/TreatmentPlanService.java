package hms.service.prescription;

import hms.entity.prescription.MedicalRecord;
import hms.entity.prescription.TreatmentPlan;
import hms.repository.prescription.MedicalRecordRepository;
import hms.repository.prescription.TreatmentPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TreatmentPlanService {

    private final TreatmentPlanRepository repo;
    private final MedicalRecordRepository recordRepo;

    public TreatmentPlanService(TreatmentPlanRepository repo, MedicalRecordRepository recordRepo) {
        this.repo = repo;
        this.recordRepo = recordRepo;
    }

    public List<TreatmentPlan> listByRecord(Long recordId) {
        return repo.findByMedicalRecord_IdOrderByCreatedAtDesc(recordId);
    }

    public TreatmentPlan get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public TreatmentPlan create(Long recordId, TreatmentPlan plan) {
        MedicalRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found: " + recordId));
        plan.setId(null);
        plan.setMedicalRecord(record);
        return repo.save(plan);
    }

    public TreatmentPlan update(Long id, TreatmentPlan incoming) {
        TreatmentPlan existing = get(id);
        if (existing == null) throw new IllegalArgumentException("Plan not found");

        existing.setTitle(incoming.getTitle());
        existing.setDescription(incoming.getDescription());
        existing.setGoals(incoming.getGoals());
        existing.setStartDate(incoming.getStartDate());
        existing.setEndDate(incoming.getEndDate());
        existing.setPhase(incoming.getPhase());
        existing.setStatus(incoming.getStatus());
        existing.setInterventions(incoming.getInterventions());
        existing.setMonitoring(incoming.getMonitoring());
        existing.setEducation(incoming.getEducation());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
