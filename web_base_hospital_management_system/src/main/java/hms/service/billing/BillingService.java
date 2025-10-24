package hms.service.billing;

import hms.entity.billing.Bill;
import hms.entity.patient.PatientP;
import hms.repository.billing.BillRepository;
import hms.repository.patient.PatientRepositoryP;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BillingService {

    private final BillRepository billRepo;
    private final PatientRepositoryP patientRepo;

    public BillingService(BillRepository billRepo, PatientRepositoryP patientRepo) {
        this.billRepo = billRepo;
        this.patientRepo = patientRepo;
    }

    @Transactional(readOnly = true)
    public List<Bill> listAll() {
        return billRepo.findAllWithPatient(); // fetch-join
    }

    @Transactional(readOnly = true)
    public Bill get(Long id) {
        return billRepo.findByIdWithPatient(id)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + id));
    }

    public Bill create(Long patientId,
                       Double amount,
                       String status,
                       LocalDate dateIssued,
                       String items,
                       LocalDate dueDate) {

        if (patientId == null) throw new IllegalArgumentException("Patient ID is required");

        PatientP patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        Bill bill = new Bill();
        bill.setPatient(patient);
        bill.setItems(items);
        bill.setPaymentStatus(normalizeStatus(status));
        bill.setDateIssued(dateIssued != null ? dateIssued : LocalDate.now());
        bill.setDueDate(dueDate);
        bill.setAmount(amount != null && amount >= 0 ? amount : 0.0);

        return billRepo.save(bill);
    }

    public Bill update(Long billId,
                       Long patientId,
                       Double amount,
                       String status,
                       LocalDate dateIssued,
                       String items,
                       LocalDate dueDate) {

        Bill bill = get(billId); // already fetch-joined

        if (patientId != null) {
            PatientP patient = patientRepo.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
            bill.setPatient(patient);
        }

        if (items != null) bill.setItems(items);
        if (status != null) bill.setPaymentStatus(normalizeStatus(status));
        if (dateIssued != null) bill.setDateIssued(dateIssued);
        bill.setDueDate(dueDate);
        if (amount != null) bill.setAmount(Math.max(0.0, amount));

        return billRepo.save(bill);
    }

    public void delete(Long id) {
        if (!billRepo.existsById(id)) throw new IllegalArgumentException("Bill not found: " + id);
        billRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Bill> findByStatus(String status) {
        return billRepo.findByPaymentStatusWithPatient(normalizeStatus(status));
    }

    @Transactional(readOnly = true)
    public long total() { return billRepo.count(); }

    @Transactional(readOnly = true)
    public long paid() { return billRepo.countByPaymentStatus("Paid"); }

    @Transactional(readOnly = true)
    public long unpaid() { return billRepo.countByPaymentStatus("Unpaid"); }

    @Transactional(readOnly = true)
    public long partial() { return billRepo.countByPaymentStatus("Partial"); }

    @Transactional(readOnly = true)
    public List<Bill> recent() { return billRepo.findTop10WithPatient(); }

    private String normalizeStatus(String s) {
        if (s == null) return "Unpaid";
        return switch (s.trim().toLowerCase()) {
            case "paid" -> "Paid";
            case "partial" -> "Partial";
            default -> "Unpaid";
        };
    }
}
