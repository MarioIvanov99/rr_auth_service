package com.mario.authservice.service;

import com.mario.authservice.data.entity.Role;
import com.mario.authservice.data.entity.User;
import com.mario.authservice.data.repository.UserRepository;
import com.mario.authservice.dto.UserDto;
import com.mario.authservice.exception.UnauthorizedException;
import com.mario.authservice.exception.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    private User user;

    @BeforeEach
    void setUp() {
        Role userRole = new Role();
        userRole.setAuthority("ROLE_USER");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .authorities(Set.of(userRole))
                .build();

        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }

    @Test
    void getCurrentUser_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDto userDto = userService.getCurrentUser();

        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("testuser", userDto.getUsername());
        assertEquals("test@example.com", userDto.getEmail());
        assertEquals("ROLE_USER", userDto.getRole());

        mockedSecurityContextHolder.verify(SecurityContextHolder::getContext);
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication).getName();
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getCurrentUser_NotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser());

        mockedSecurityContextHolder.verify(SecurityContextHolder::getContext);
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication, never()).getName();
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void getCurrentUser_AuthenticationIsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser());

        mockedSecurityContextHolder.verify(SecurityContextHolder::getContext);
        verify(securityContext).getAuthentication();
        verify(authentication, never()).isAuthenticated();
        verify(authentication, never()).getName();
        verify(userRepository, never()).findByUsername(anyString());
    }


    @Test
    void getCurrentUser_AuthenticatedUserNotFoundInDb() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUser());

        mockedSecurityContextHolder.verify(SecurityContextHolder::getContext);
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication).getName();
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.loadUserByUsername("nonexistentuser"));

        verify(userRepository).findByUsername("nonexistentuser");
    }
}