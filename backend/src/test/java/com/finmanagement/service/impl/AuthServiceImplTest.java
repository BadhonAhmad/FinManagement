package com.finmanagement.service.impl;

import com.finmanagement.config.CustomUserDetailsService;
import com.finmanagement.config.JwtTokenProvider;
import com.finmanagement.dto.auth.RegisterRequest;
import com.finmanagement.dto.common.ApiResponse;
import com.finmanagement.dto.user.UserResponse;
import com.finmanagement.entity.User;
import com.finmanagement.entity.enums.Role;
import com.finmanagement.exception.DuplicateResourceException;
import com.finmanagement.mapper.UserMapper;
import com.finmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider tokenProvider;
    @Mock private CustomUserDetailsService userDetailsService;
    @Mock private UserMapper userMapper;

    @InjectMocks private AuthServiceImpl authService;

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setRole(Role.VIEWER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setUsername("testuser");
        response.setRole(Role.VIEWER);
        when(userMapper.toResponse(any(User.class))).thenReturn(response);

        ApiResponse<UserResponse> result = authService.register(request);

        assertTrue(result.isSuccess());
        assertEquals("testuser", result.getData().getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_duplicateEmail_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }
}
