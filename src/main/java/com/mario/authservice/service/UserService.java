package com.mario.authservice.service;

import com.mario.authservice.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto getCurrentUser();
}
