package hms.repository.appointments;

import hms.entity.appointment.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> { }
