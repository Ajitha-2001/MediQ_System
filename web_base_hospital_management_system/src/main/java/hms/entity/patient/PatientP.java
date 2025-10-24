package hms.entity.patient;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(
        name = "patients",
        indexes = {
                @Index(name = "idx_patient_name", columnList = "lastName, firstName"),
                // IMPORTANT: index targets the real DB column name
                @Index(name = "idx_patient_external", columnList = "external_id", unique = true)
        }
)
public class PatientP {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Explicit DB column mapping (matches MySQL schema)
  @Column(name = "external_id", nullable = false, unique = true, length = 40)
  private String externalId = UUID.randomUUID().toString();

  @NotBlank
  private String firstName;

  @Column(nullable = true)
  private String lastName;

  private String gender;
  private LocalDate dateOfBirth;

  @Embedded
  private ContactInfo contact = new ContactInfo();

  @Embedded
  private MedicalInfo medical = new MedicalInfo();

  @ElementCollection
  @CollectionTable(name = "patient_tags", joinColumns = @JoinColumn(name = "patient_id"))
  @Column(name = "tag")
  private Set<String> tags = new HashSet<>();

  @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("eventTime DESC")
  private List<TimelineEvent> events = new ArrayList<>();

  /* ---------- Getters / Setters ---------- */

  public Long getId() { return id; }

  public String getExternalId() { return externalId; }
  public void setExternalId(String externalId) { this.externalId = externalId; }

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getGender() { return gender; }
  public void setGender(String gender) { this.gender = gender; }

  public LocalDate getDateOfBirth() { return dateOfBirth; }
  public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

  public ContactInfo getContact() { return contact; }
  public void setContact(ContactInfo contact) { this.contact = contact; }

  public MedicalInfo getMedical() { return medical; }
  public void setMedical(MedicalInfo medical) { this.medical = medical; }

  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags; }

  public List<TimelineEvent> getEvents() { return events; }
  public void setEvents(List<TimelineEvent> events) { this.events = events; }

  public void addEvent(TimelineEvent e) { events.add(e); e.setPatient(this); }
  public void removeEvent(TimelineEvent e) { events.remove(e); e.setPatient(null); }

  /** Convenience alias */
  public Long getPatientId() { return id; }

  /** Safety: ensure a UUID is always present */
  @PrePersist
  void ensureExternalId() {
    if (externalId == null || externalId.isBlank()) {
      externalId = UUID.randomUUID().toString();
    }
  }
}
