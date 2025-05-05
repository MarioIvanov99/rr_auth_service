package com.mario.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AuthRequest {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
