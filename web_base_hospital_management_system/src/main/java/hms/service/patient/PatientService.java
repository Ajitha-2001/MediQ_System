package hms.service.patient;

import hms.dto.patient.PatientCreateRequest;
import hms.dto.patient.TimelineEventRequest;
import hms.entity.patient.PatientP;
import hms.entity.patient.TimelineEvent;
import hms.repository.patient.PatientRepositoryP;
import hms.repository.patient.TimelineEventRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PatientService {
  private final PatientRepositoryP patients;
  private final TimelineEventRepository events;

  public PatientService(PatientRepositoryP patients, TimelineEventRepository events) {
    this.patients = patients;
    this.events = events;
  }

  /** CREATE */
  @Transactional
  public PatientP register(PatientCreateRequest req, String actor) {
    PatientP p = new PatientP();
    Mapper.mapToPatient(p, req);
    p = patients.save(p);
    addEvent(p, "PROFILE_CREATE", "Profile created by " + (actor == null ? "system" : actor));
    return p;
  }

  /** UPDATE */
  @Transactional
  public PatientP update(String externalId, PatientCreateRequest req, String actor) {
    PatientP p = getOrThrow(externalId);
    Mapper.mapToPatient(p, req);
    addEvent(p, "PROFILE_UPDATE", "Profile updated by " + (actor == null ? "system" : actor));
    return patients.save(p);
  }

  /** DELETE */
  @Transactional
  public void delete(String externalId, String actor) {
    PatientP p = getOrThrow(externalId);
    patients.delete(p);
  }

  /** SEARCH */
  public Page<PatientP> search(String q, String gender, java.time.LocalDate dobFrom,
                               java.time.LocalDate dobTo, Set<String> tags, Pageable pageable) {
    Specification<PatientP> spec = null;
    var s1 = PatientSpecifications.nameContains(q);
    if (s1 != null) spec = Specification.where(s1);
    var s2 = PatientSpecifications.genderEquals(gender);
    if (s2 != null) spec = (spec == null) ? Specification.where(s2) : spec.and(s2);
    var s3 = PatientSpecifications.dobBetween(dobFrom, dobTo);
    if (s3 != null) spec = (spec == null) ? Specification.where(s3) : spec.and(s3);
    var s4 = PatientSpecifications.hasAnyTag(tags);
    if (s4 != null) spec = (spec == null) ? Specification.where(s4) : spec.and(s4);
    return (spec == null) ? patients.findAll(pageable) : patients.findAll(spec, pageable);
  }

  /** READ (optional) */
  public Optional<PatientP> find(String externalId) {
    return patients.findByExternalId(externalId);
  }

  /** READ (strict) */
  public PatientP getOrThrow(String externalId) {
    return patients.findByExternalId(externalId)
            .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
  }

  /** TIMELINE */
  public List<TimelineEvent> timeline(String externalId) {
    return patients.findByExternalId(externalId)
            .map(p -> events.findByPatient_IdOrderByEventTimeDesc(p.getId()))
            .orElse(List.of());
  }

  /** ADD EVENT */
  @Transactional
  public void addTimelineEvent(String externalId, TimelineEventRequest req, String actor) {
    PatientP p = getOrThrow(externalId);
    addEvent(p, req.type(), req.description() + " (by " + (actor == null ? "system" : actor) + ")");
  }

  /* helper */
  private void addEvent(PatientP p, String type, String description) {
    TimelineEvent e = new TimelineEvent();
    e.setPatient(p);
    e.setType(type);
    e.setDescription(description);
    e.setEventTime(OffsetDateTime.now());
    events.save(e);
  }
}
