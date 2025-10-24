package hms.entity.billing;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import hms.entity.patient.PatientP;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "bills")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "billId")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @NotNull
    @Min(0)
    private Double amount = 0.0;

    @NotNull
    @Column(length = 20)
    private String paymentStatus = "Unpaid"; // Paid | Partial | Unpaid

    @NotNull
    private LocalDate dateIssued = LocalDate.now();

    private LocalDate dueDate;

    @Size(max = 2000)
    @Column(length = 2000)
    private String items;

    /** Each bill belongs to one patient */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PatientP patient;

    // --- Getters & Setters ---
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

    public PatientP getPatient() { return patient; }
    public void setPatient(PatientP patient) { this.patient = patient; }
}
