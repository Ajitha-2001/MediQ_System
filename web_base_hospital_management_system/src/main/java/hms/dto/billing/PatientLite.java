package hms.dto.billing;

public class PatientLite {
    private Long id;          // JPA id
    private String patientId; // your domain/id card value
    private String firstName;
    private String lastName;
    private String gender;

    public PatientLite(Long id, Long patientId, String firstName, String lastName, String gender) {}

    public PatientLite(Long id, String patientId, String firstName, String lastName, String gender) {
        this.id = id;
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
