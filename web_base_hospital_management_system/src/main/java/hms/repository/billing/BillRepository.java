package hms.repository.billing;

import hms.entity.billing.Bill;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    long countByPaymentStatus(String status);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b WHERE b.dateIssued BETWEEN :start AND :end")
    Double sumRevenueBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // ---------- Fetch-join patient to avoid LazyInitialization ----------
    @Query("select b from Bill b join fetch b.patient order by b.billId asc")
    List<Bill> findAllWithPatient();

    @Query("select b from Bill b join fetch b.patient order by b.dateIssued desc")
    List<Bill> findTop10WithPatient();

    @Query("select b from Bill b join fetch b.patient where b.billId = :id")
    Optional<Bill> findByIdWithPatient(@Param("id") Long id);

    @Query("select b from Bill b join fetch b.patient where lower(b.paymentStatus) = lower(:status)")
    List<Bill> findByPaymentStatusWithPatient(@Param("status") String status);

    // (kept in case you need them elsewhere)
    List<Bill> findTop10ByOrderByDateIssuedDesc();
    List<Bill> findByPaymentStatusIgnoreCase(String status);
}
