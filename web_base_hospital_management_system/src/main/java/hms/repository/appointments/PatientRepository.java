package hms.repository.appointments;

import hms.entity.appointment.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> { }
