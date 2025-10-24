package hms.service.patient;

import hms.dto.patient.PatientCreateRequest;
import hms.entity.patient.PatientP;

public class Mapper {
  public static void mapToPatient(PatientP p, PatientCreateRequest req) {
    p.setFirstName(req.firstName());
    p.setLastName(req.lastName());
    p.setGender(req.gender());
    p.setDateOfBirth(req.dateOfBirth());
    var c = p.getContact();
    c.setPhone(req.phone()); c.setEmail(req.email());
    c.setAddressLine1(req.addressLine1()); c.setAddressLine2(req.addressLine2());
    c.setCity(req.city()); c.setState(req.state()); c.setPostalCode(req.postalCode());
    var m = p.getMedical();
    m.setBloodType(req.bloodType()); m.setAllergies(req.allergies());
    m.setChronicConditions(req.chronicConditions()); m.setNotes(req.notes());
    p.getTags().clear(); if (req.tags()!=null) p.getTags().addAll(req.tags());
  }
}
