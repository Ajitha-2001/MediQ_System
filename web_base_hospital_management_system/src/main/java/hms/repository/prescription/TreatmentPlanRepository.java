package hms.repository.prescription;

import hms.entity.prescription.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {
    List<TreatmentPlan> findByMedicalRecord_IdOrderByCreatedAtDesc(Long recordId);
}
