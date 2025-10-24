package hms.entity.analytics;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_reports")
public class CustomReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String type;

    private String period;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String data;

    private String status;

    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;


    public CustomReport() {
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = (this.status == null) ? "ACTIVE" : this.status;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public void markAsArchived() {
        this.status = "ARCHIVED";
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void markAsActive() {
    }
}
