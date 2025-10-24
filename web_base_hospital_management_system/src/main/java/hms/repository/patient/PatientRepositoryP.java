package hms.repository.patient;

import hms.entity.patient.PatientP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepositoryP extends JpaRepository<PatientP, Long>, JpaSpecificationExecutor<PatientP> {

  @EntityGraph(attributePaths = {"contact", "medical", "tags"})
  Optional<PatientP> findByExternalId(String externalId);

  boolean existsByExternalId(String externalId);

  void deleteByExternalId(String externalId);

  @EntityGraph(attributePaths = {"tags"})
  Page<PatientP> findAll(Specification<PatientP> spec, Pageable pageable);

  @EntityGraph(attributePaths = {"tags"})
  Page<PatientP> findAll(Pageable pageable);

  // ----------------- Analytics Queries -----------------

  @Query("SELECT p.gender, COUNT(p) FROM PatientP p GROUP BY p.gender")
  List<Object[]> countByGender();
}
