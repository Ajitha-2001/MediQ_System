package hms.service.prescription;

import hms.entity.prescription.MedicalRecord;
import hms.repository.prescription.MedicalRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional(readOnly = true)
    public MedicalRecord get(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<MedicalRecord> listAll() {
        return medicalRecordRepository.findAll();
    }

    @Transactional
    public MedicalRecord save(MedicalRecord medicalRecord) {
        return medicalRecordRepository.save(medicalRecord);
    }

    @Transactional
    public void delete(Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. MedicalRecord not found with id: " + id);
        }
        medicalRecordRepository.deleteById(id);
    }
}
