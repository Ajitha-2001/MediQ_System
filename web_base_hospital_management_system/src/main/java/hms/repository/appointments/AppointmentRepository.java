package hms.repository.appointments;

import hms.entity.appointment.Appointment;
import hms.entity.appointment.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @EntityGraph(attributePaths = {"patient", "doctor"})
    Optional<Appointment> findById(Long id);

    @EntityGraph(attributePaths = {"patient", "doctor"})
    Optional<Appointment> findByExternalId(String externalId);

    @EntityGraph(attributePaths = {"patient", "doctor"})
    Page<Appointment> findByStartTimeGreaterThanEqualOrderByStartTimeAsc(LocalDateTime from, Pageable pageable);

    boolean existsByDoctorIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId, AppointmentStatus status, LocalDateTime endExclusive, LocalDateTime startExclusive);

    boolean existsByExternalId(String externalId);

    long countByStatusAndStartTimeBetween(AppointmentStatus status, LocalDateTime start, LocalDateTime end);

    long countByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT DAY(start_time) AS day, COUNT(id) AS count FROM appointments " +
            "WHERE YEAR(start_time) = :year AND MONTH(start_time) = :month " +
            "GROUP BY DAY(start_time)", nativeQuery = true)
    List<Object[]> findDailyCountsInMonth(@Param("year") int year, @Param("month") int month);
}
