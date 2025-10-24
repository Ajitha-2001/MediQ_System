package hms.repository.prescription;

import hms.entity.prescription.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByMedicalRecordId(Long recordId);


    List<Prescription> findByMedicalRecord_IdOrderByIssuedAtDesc(Long recordId);
}
