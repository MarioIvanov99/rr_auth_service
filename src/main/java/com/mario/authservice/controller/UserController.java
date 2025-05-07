package com.mario.authservice.controller;

import com.mario.authservice.dto.UserDto;
import com.mario.authservice.service.UserService;
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

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1) Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable @Min(1) Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
