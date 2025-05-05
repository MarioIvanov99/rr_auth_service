package com.mario.authservice.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String role;
}
