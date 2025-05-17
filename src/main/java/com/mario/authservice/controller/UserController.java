package com.mario.authservice.controller;

import com.mario.authservice.dto.PasswordChangeRequest;
import com.mario.authservice.dto.UserDto;
import com.mario.authservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserDto> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        UserDto userDto = userService.changePassword(request);
        return ResponseEntity.ok(userDto);
    }
}
