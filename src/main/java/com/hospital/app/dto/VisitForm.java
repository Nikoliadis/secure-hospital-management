package com.hospital.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class VisitForm {

    @NotNull(message = "Patient is required")
    private Long patientId;

    @NotNull(message = "Doctor is required")
    private Long doctorId;

    @NotNull(message = "Visit date is required")
    private LocalDateTime visitDate;

    @NotBlank(message = "Reason for visit is required")
    @Size(max = 500)
    private String reason;

    @Size(max = 2000)
    private String diagnosis;

    @Size(max = 1000)
    private String notes;
}
