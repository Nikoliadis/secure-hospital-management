package com.hospital.app.dto;

import com.hospital.app.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EditUserForm {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required")
    private Role role;

    private boolean enabled;
}
