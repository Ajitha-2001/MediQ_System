package hms.repository.patient;

import hms.entity.patient.TimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimelineEventRepository extends JpaRepository<TimelineEvent, Long> {
  List<TimelineEvent> findByPatient_IdOrderByEventTimeDesc(Long patientId);
}
