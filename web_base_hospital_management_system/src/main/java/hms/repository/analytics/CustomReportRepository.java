package hms.repository.analytics;

import hms.entity.analytics.CustomReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomReportRepository extends JpaRepository<CustomReport, Long> {


    Page<CustomReport> findByType(String type, Pageable pageable);


    Page<CustomReport> findByStatus(String status, Pageable pageable);


    List<CustomReport> findByCreatedAtBefore(LocalDateTime dateTime);


    Page<CustomReport> findByTitleContainingIgnoreCaseOrTypeContainingIgnoreCase(
            String titleQuery, String typeQuery, Pageable pageable);


    List<CustomReport> findTop5ByOrderByCreatedAtDesc();


    @Query("SELECT r.type, COUNT(r) FROM CustomReport r GROUP BY r.type")
    List<Object[]> countByType();


    List<CustomReport> findByPeriod(String period);


    List<CustomReport> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);


    long countByType(String type);


    long countByStatus(String status);


    Page<CustomReport> findByCreatedBy(String createdBy, Pageable pageable);


    @Query("SELECT r FROM CustomReport r WHERE r.status = 'ACTIVE' ORDER BY r.createdAt DESC")
    Page<CustomReport> findActiveReports(Pageable pageable);


    @Query("SELECT r FROM CustomReport r WHERE r.status = 'ARCHIVED' ORDER BY r.createdAt DESC")
    Page<CustomReport> findArchivedReports(Pageable pageable);
}