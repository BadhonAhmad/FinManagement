package com.finmanagement.service.impl;

import com.finmanagement.config.CustomUserDetailsService;
import com.finmanagement.config.JwtTokenProvider;
import com.finmanagement.dto.auth.LoginResponse;
import com.finmanagement.dto.auth.RegisterRequest;
import com.finmanagement.dto.common.ApiResponse;
import com.finmanagement.dto.user.UserResponse;
import com.finmanagement.entity.User;
import com.finmanagement.exception.DuplicateResourceException;
import com.finmanagement.mapper.UserMapper;
import com.finmanagement.repository.UserRepository;
import com.finmanagement.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                           CustomUserDetailsService userDetailsService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
    }

    @Override
    public ApiResponse<UserResponse> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        return ApiResponse.success("User registered successfully", userMapper.toResponse(user));
    }

    @Override
    public ApiResponse<LoginResponse> login(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        User user = userDetailsService.loadFullUser(username);

        String token = tokenProvider.generateToken(userDetails, user.getId(), user.getRole().name());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setExpiresIn(tokenProvider.getExpirationMs());
        loginResponse.setUser(userMapper.toResponse(user));

        return ApiResponse.success(loginResponse);
    }
}
