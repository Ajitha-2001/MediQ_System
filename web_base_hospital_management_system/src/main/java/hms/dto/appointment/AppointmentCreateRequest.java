package hms.dto.appointment;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AppointmentCreateRequest(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        @NotNull LocalDate date,
        @NotNull String time,
        String channel,
        String notes
) {}
