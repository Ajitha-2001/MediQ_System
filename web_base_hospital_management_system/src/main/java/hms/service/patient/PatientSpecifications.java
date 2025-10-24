package hms.service.patient;

import hms.entity.patient.PatientP;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.Set;

public class PatientSpecifications {
  public static Specification<PatientP> nameContains(String q) {
    if (q == null || q.isBlank()) return null;
    return (root, cq, cb) -> cb.or(
        cb.like(cb.lower(root.get("firstName")), "%" + q.toLowerCase() + "%"),
        cb.like(cb.lower(root.get("lastName")), "%" + q.toLowerCase() + "%")
    );
  }
  public static Specification<PatientP> genderEquals(String gender) {
    if (gender == null || gender.isBlank()) return null;
    return (root, cq, cb) -> cb.equal(cb.lower(root.get("gender")), gender.toLowerCase());
  }
  public static Specification<PatientP> dobBetween(LocalDate from, LocalDate to) {
    if (from == null && to == null) return null;
    if (from == null) return (r,c,cb) -> cb.lessThanOrEqualTo(r.get("dateOfBirth"), to);
    if (to == null) return (r,c,cb) -> cb.greaterThanOrEqualTo(r.get("dateOfBirth"), from);
    return (r,c,cb) -> cb.between(r.get("dateOfBirth"), from, to);
  }
  public static Specification<PatientP> hasAnyTag(Set<String> tags) {
    if (tags == null || tags.isEmpty()) return null;
    return (root, cq, cb) -> { var join = root.join("tags"); cq.distinct(true); return join.in(tags); };
  }
}
