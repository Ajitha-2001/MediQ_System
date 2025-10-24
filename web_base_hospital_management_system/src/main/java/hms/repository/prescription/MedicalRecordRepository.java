package hms.repository.prescription;

import hms.entity.prescription.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatientNameOrderByAdmissionDateDesc(String patientName);

    List<MedicalRecord> findByAdmissionDate(LocalDate admissionDate);

    List<MedicalRecord> findAllByOrderByAdmissionDateDesc();

    long countByAdmissionDateBetween(LocalDate start, LocalDate end);

    Optional<MedicalRecord> findByPatientName(String patientName);
}
