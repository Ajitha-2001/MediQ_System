package hms.dto.patient;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public record PatientCreateRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    String gender,
    LocalDate dateOfBirth,
    String phone,
    @Email String email,
    String addressLine1,
    String addressLine2,
    String city,
    String state,
    String postalCode,
    String bloodType,
    String allergies,
    String chronicConditions,
    String notes,
    Set<String> tags
) {}
