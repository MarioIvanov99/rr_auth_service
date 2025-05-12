package com.mario.authservice.service;

import com.mario.authservice.config.JwtUtils;
import com.mario.authservice.data.entity.Role;
import com.mario.authservice.data.entity.User;
import com.mario.authservice.data.repository.UserRepository;
import com.mario.authservice.dto.AuthRequest;
import com.mario.authservice.dto.AuthResponse;
import com.mario.authservice.dto.RegisterRequest;
import com.mario.authservice.exception.EmailAlreadyExistsException;
import com.mario.authservice.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        Role userRole = new Role();
        userRole.setAuthority("ROLE_USER");

        registerRequest = new RegisterRequest("testuser", "test@example.com", "password");
        authRequest = new AuthRequest("testuser", "password");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .authorities(Set.of(userRole))
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .isDeleted(false)
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        lenient().when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtils.generateToken(any(User.class))).thenReturn("mockedJwtToken");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mockedJwtToken", response.getToken());
        assertEquals("JWT", response.getTokenType());
        assertEquals("testuser", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
        verify(jwtUtils).generateToken(any(User.class));
    }

    @Test
    void register_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> authService.register(registerRequest));

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtils, never()).generateToken(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequest));

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtils, never()).generateToken(any(User.class));
    }

    @Test
    void authenticate_Success() {
        when(userRepository.findByUsernameOrEmail(authRequest.getUsernameOrEmail(), authRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(user)).thenReturn("mockedJwtToken");

        AuthResponse response = authService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals("mockedJwtToken", response.getToken());
        assertEquals("JWT", response.getTokenType());
        assertEquals("testuser", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());

        verify(userRepository).findByUsernameOrEmail("testuser", "testuser");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtUtils).generateToken(user);
    }

    @Test
    void authenticate_UserNotFound() {
        when(userRepository.findByUsernameOrEmail(authRequest.getUsernameOrEmail(), authRequest.getUsernameOrEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.authenticate(authRequest));

        verify(userRepository).findByUsernameOrEmail("testuser", "testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtils, never()).generateToken(any(User.class));
    }

    @Test
    void authenticate_BadCredentials() {
        when(userRepository.findByUsernameOrEmail(authRequest.getUsernameOrEmail(), authRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));

        verify(userRepository).findByUsernameOrEmail("testuser", "testuser");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtUtils, never()).generateToken(any(User.class));
    }
}