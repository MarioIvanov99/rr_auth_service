package com.mario.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;
}