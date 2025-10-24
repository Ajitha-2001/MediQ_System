package hms.config.patient;

import hms.dto.patient.PatientCreateRequest;
import hms.service.patient.PatientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public abstract class DataLoader implements CommandLineRunner {
  private final PatientService service;
  public DataLoader(PatientService service) { this.service = service; }


}
