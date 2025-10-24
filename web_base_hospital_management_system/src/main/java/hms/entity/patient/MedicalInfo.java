package hms.entity.patient;

import jakarta.persistence.Embeddable;

@Embeddable
public class MedicalInfo {
  private String bloodType;
  private String allergies;
  private String chronicConditions;
  private String notes;

  public String getBloodType() { return bloodType; }
  public void setBloodType(String bloodType) { this.bloodType = bloodType; }
  public String getAllergies() { return allergies; }
  public void setAllergies(String allergies) { this.allergies = allergies; }
  public String getChronicConditions() { return chronicConditions; }
  public void setChronicConditions(String chronicConditions) { this.chronicConditions = chronicConditions; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
}
