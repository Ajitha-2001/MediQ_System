package hms.entity.prescription;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_plans")
public class TreatmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String goals;
    private LocalDate startDate;
    private LocalDate endDate;
    private String phase;
    private String status;

    @Column(length = 2000)
    private String interventions;

    @Column(length = 2000)
    private String monitoring;

    @Column(length = 2000)
    private String education;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInterventions() { return interventions; }
    public void setInterventions(String interventions) { this.interventions = interventions; }

    public String getMonitoring() { return monitoring; }
    public void setMonitoring(String monitoring) { this.monitoring = monitoring; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) { this.medicalRecord = medicalRecord; }
}
