package com.mario.authservice.service;

import com.mario.authservice.config.JwtUtils;
import com.mario.authservice.data.entity.User;
import com.mario.authservice.data.entity.Role;
import com.mario.authservice.data.repository.UserRepository;
import com.mario.authservice.dto.AuthRequest;
import com.mario.authservice.dto.AuthResponse;
import com.mario.authservice.dto.RegisterRequest;
import com.mario.authservice.exception.EmailAlreadyExistsException;
import com.mario.authservice.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authorities(Set.of(new Role()))
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        String token = jwtUtils.generateToken(user);


        return new AuthResponse(
                token,
                "JWT",
                user.getUsername(),
                user.getAuthorities().iterator().next().getAuthority()
        );
    }

    @Override
    public AuthResponse authenticate(AuthRequest request){
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtils.generateToken(user);

        return new AuthResponse(
                token,
                "JWT",
                user.getUsername(),
                user.getAuthorities().iterator().next().getAuthority()
        );
    }
}
