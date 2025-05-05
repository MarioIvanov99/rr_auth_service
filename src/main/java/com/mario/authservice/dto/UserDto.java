package com.mario.authservice.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
}
