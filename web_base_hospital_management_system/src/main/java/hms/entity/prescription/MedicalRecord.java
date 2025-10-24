package hms.entity.prescription;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    private String doctorName;

    private String diagnosis;

    private LocalDate visitDate;

    // Added: admissionDate (used in PrescriptionService)
    private LocalDate admissionDate;

    private String notes;

    private String labResults;

    // Relationships
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreatmentPlan> treatmentPlans = new ArrayList<>();

    // Constructors
    public MedicalRecord() {}

    public MedicalRecord(String patientName, String doctorName, String diagnosis, LocalDate visitDate) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.diagnosis = diagnosis;
        this.visitDate = visitDate;
        this.admissionDate = visitDate;
    }

    // --- Helper Methods for Relationship Management ---
    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
        prescription.setMedicalRecord(this);
    }

    public void removePrescription(Prescription prescription) {
        prescriptions.remove(prescription);
        prescription.setMedicalRecord(null);
    }

    public void addTreatmentPlan(TreatmentPlan treatmentPlan) {
        treatmentPlans.add(treatmentPlan);
        treatmentPlan.setMedicalRecord(this);
    }

    public void removeTreatmentPlan(TreatmentPlan treatmentPlan) {
        treatmentPlans.remove(treatmentPlan);
        treatmentPlan.setMedicalRecord(null);
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }

    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLabResults() { return labResults; }
    public void setLabResults(String labResults) { this.labResults = labResults; }

    public List<Prescription> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<Prescription> prescriptions) { this.prescriptions = prescriptions; }

    public List<TreatmentPlan> getTreatmentPlans() { return treatmentPlans; }
    public void setTreatmentPlans(List<TreatmentPlan> treatmentPlans) { this.treatmentPlans = treatmentPlans; }
}
