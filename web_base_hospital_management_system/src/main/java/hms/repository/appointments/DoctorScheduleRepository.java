package hms.repository.appointments;

import hms.entity.appointment.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    Optional<DoctorSchedule> findFirstByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
}
