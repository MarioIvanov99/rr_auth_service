package com.mario.authservice.service;

import com.mario.authservice.data.repository.UserRepository;
import com.mario.authservice.dto.AuthRequest;
import com.mario.authservice.dto.AuthResponse;
import com.mario.authservice.dto.RegisterRequest;
import com.mario.authservice.exception.EmailAlreadyExistsException;
import com.mario.authservice.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    @Override
    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }


    }

    @Override
    public AuthResponse authenticate(AuthRequest request){

    }
}
