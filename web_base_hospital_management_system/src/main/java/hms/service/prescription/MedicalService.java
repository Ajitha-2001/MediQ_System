package hms.service.prescription;

import java.time.LocalDate;
import java.util.List;

import hms.entity.prescription.MedicalRecord;
import hms.entity.prescription.Prescription;
import hms.entity.prescription.TreatmentPlan;
import hms.repository.prescription.MedicalRecordRepository;
import hms.repository.prescription.PrescriptionRepository;
import hms.repository.prescription.TreatmentPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MedicalService {

    private final MedicalRecordRepository recordRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final TreatmentPlanRepository planRepo;

    public MedicalService(MedicalRecordRepository recordRepo,
                          PrescriptionRepository prescriptionRepo,
                          TreatmentPlanRepository planRepo) {
        this.recordRepo = recordRepo;
        this.prescriptionRepo = prescriptionRepo;
        this.planRepo = planRepo;
    }



}
