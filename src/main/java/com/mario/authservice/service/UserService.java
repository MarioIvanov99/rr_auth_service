package com.mario.authservice.service;

import com.mario.authservice.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto getUserById(Long id);
    UserDto getCurrentUser();
    List<UserDto> getAllUsers();
    void deleteUser(Long id);
    void softDeleteUser(Long id);
}
