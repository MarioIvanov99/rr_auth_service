package com.mario.authservice.service;

import com.mario.authservice.dto.AuthRequest;
import com.mario.authservice.dto.AuthResponse;
import com.mario.authservice.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
}
