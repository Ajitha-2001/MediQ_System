package hms.service.prescription;

import hms.entity.prescription.MedicalRecord;
import hms.entity.prescription.Prescription;
import hms.entity.prescription.PrescriptionRecord;
import hms.repository.prescription.MedicalRecordRepository;
import hms.repository.prescription.PrescriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               MedicalRecordRepository medicalRecordRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional(readOnly = true)
    public List<Prescription> listByRecord(Long recordId) {
        return prescriptionRepository.findByMedicalRecord_IdOrderByIssuedAtDesc(recordId);
    }

    @Transactional(readOnly = true)
    public Prescription get(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found with id: " + id));
    }

    @Transactional
    public Prescription create(Long recordId, Prescription prescription) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord not found with id: " + recordId));


        prescription.setMedicalRecord(medicalRecord);
        if (prescription.getDoctorName() == null || prescription.getDoctorName().isBlank()) {
            prescription.setDoctorName(medicalRecord.getDoctorName());
        }
        if (prescription.getPatientName() == null || prescription.getPatientName().isBlank()) {
            prescription.setPatientName(medicalRecord.getPatientName());
        }
        if (prescription.getIssuedAt() == null) {
            prescription.setIssuedAt(LocalDateTime.now());
        }

        return prescriptionRepository.save(prescription);
    }

    @Transactional
    public Prescription update(Long id, Prescription updated) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found with id: " + id));


        updated.setMedicalRecord(existing.getMedicalRecord());

        // Update all editable fields
        existing.setMedication(updated.getMedication());
        existing.setDosage(updated.getDosage());
        existing.setDuration(updated.getDuration());
        existing.setFrequency(updated.getFrequency());
        existing.setRoute(updated.getRoute());
        existing.setInstructions(updated.getInstructions());
        existing.setRefills(updated.getRefills());

        existing.setDoctorName(updated.getDoctorName());
        existing.setPatientName(updated.getPatientName());

        if (updated.getIssuedAt() != null) {
            existing.setIssuedAt(updated.getIssuedAt());
        }

        return prescriptionRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new EntityNotFoundException("Prescription not found with id: " + id);
        }
        prescriptionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PrescriptionRecord getSampleRecord() {
        PrescriptionRecord sampleRecord = new PrescriptionRecord();
        sampleRecord.setId(1L);
        sampleRecord.setPatientName("John Doe");
        sampleRecord.setDoctorName("Dr. Smith");
        sampleRecord.setDiagnosis("Hypertension");
        return sampleRecord;
    }

    @Transactional
    public Prescription save(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }
}
