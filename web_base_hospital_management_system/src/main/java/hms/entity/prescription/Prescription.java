package hms.entity.prescription;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Medication is required")
    private String medication;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    private String duration;
    private String frequency;
    private String route;
    private String instructions;
    private String refills;

    private String doctorName;
    private String patientName;

    private LocalDateTime issuedAt;

    @ManyToOne(fetch = FetchType.EAGER) // Avoid LazyInitialization issues in controllers/views
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @Transient
    private Long recordId;

    public Prescription() {
        this.issuedAt = LocalDateTime.now();
    }

    public Prescription(String medication, String dosage, String instructions) {
        this();
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMedication() { return medication; }
    public void setMedication(String medication) { this.medication = medication; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getRefills() { return refills; }
    public void setRefills(String refills) { this.refills = refills; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
        if (medicalRecord != null) {
            this.recordId = medicalRecord.getId();
        }
    }

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
}
