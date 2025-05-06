package com.mario.authservice.service;

import com.mario.authservice.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);
    UserDto getCurrentUser();
    List<UserDto> getAllUsers();
    void deleteUser(Long id);
    void softDeleteUser(Long id);
}
