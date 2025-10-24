package hms.dto.appointment;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RescheduleRequest(
        Long newDoctorId,
        @NotNull LocalDate newDate,
        @NotNull String newTime,
        String notes
) {}
