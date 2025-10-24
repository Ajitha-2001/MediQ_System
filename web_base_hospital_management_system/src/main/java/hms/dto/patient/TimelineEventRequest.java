package hms.dto.patient;

import jakarta.validation.constraints.NotBlank;

public record TimelineEventRequest(
    @NotBlank String type,
    @NotBlank String description
) {}
