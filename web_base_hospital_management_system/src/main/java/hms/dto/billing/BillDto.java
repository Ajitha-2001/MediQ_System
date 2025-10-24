package hms.dto.billing;

import java.time.LocalDate;

public class BillDto {

    private Long billId;
    private Double amount;
    private String paymentStatus;
    private LocalDate dateIssued;
    private LocalDate dueDate;
    private String items;

    // Flattened patient fields
    private Long patientId;          // JPA id
    private String patientCode;      // domain code (entity.getPatientId())
    private String patientFirstName;
    private String patientLastName;
    private String patientFullName;

    public BillDto() {}

    public BillDto(Long billId,
                   Double amount,
                   String paymentStatus,
                   LocalDate dateIssued,
                   LocalDate dueDate,
                   String items,
                   Long patientId,
                   String patientCode,
                   String patientFirstName,
                   String patientLastName,
                   String patientFullName) {
        this.billId = billId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.dateIssued = dateIssued;
        this.dueDate = dueDate;
        this.items = items;
        this.patientId = patientId;
        this.patientCode = patientCode;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.patientFullName = patientFullName;
    }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDate getDateIssued() { return dateIssued; }
    public void setDateIssued(LocalDate dateIssued) { this.dateIssued = dateIssued; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientCode() { return patientCode; }
    public void setPatientCode(String patientCode) { this.patientCode = patientCode; }

    public String getPatientFirstName() { return patientFirstName; }
    public void setPatientFirstName(String patientFirstName) { this.patientFirstName = patientFirstName; }

    public String getPatientLastName() { return patientLastName; }
    public void setPatientLastName(String patientLastName) { this.patientLastName = patientLastName; }

    public String getPatientFullName() { return patientFullName; }
    public void setPatientFullName(String patientFullName) { this.patientFullName = patientFullName; }
}
