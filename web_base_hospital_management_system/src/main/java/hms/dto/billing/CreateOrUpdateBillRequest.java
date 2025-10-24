package hms.dto.billing;

import java.time.LocalDate;

public class CreateOrUpdateBillRequest {
    private Long patientId;      // JPA id of PatientP
    private Double amount;
    private String paymentStatus; // "Paid" | "Partial" | "Unpaid"
    private LocalDate dateIssued;
    private LocalDate dueDate;
    private String items;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

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
}
