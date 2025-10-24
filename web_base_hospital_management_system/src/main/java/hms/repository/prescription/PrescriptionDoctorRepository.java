package hms.repository.prescription;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionDoctorRepository extends JpaRepository<hms.entity.prescription.DoctorPP, Long> {}