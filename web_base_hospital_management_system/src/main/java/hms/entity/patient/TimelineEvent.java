package hms.entity.patient;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "timeline_events",
        indexes = {
                @Index(name = "idx_event_patient_time", columnList = "patient_id,eventTime DESC")
        }
)
public class TimelineEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Owning side — column name matches the index definition
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "patient_id", nullable = false)
  private PatientP patient;

  @NotBlank
  private String type;

  @NotBlank
  @Column(length = 1000)
  private String description;

  private OffsetDateTime eventTime = OffsetDateTime.now();

  public Long getId() { return id; }

  public PatientP getPatient() { return patient; }
  public void setPatient(PatientP patient) { this.patient = patient; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public OffsetDateTime getEventTime() { return eventTime; }
  public void setEventTime(OffsetDateTime eventTime) { this.eventTime = eventTime; }
}
