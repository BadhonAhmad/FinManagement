package com.finmanagement.service;

import com.finmanagement.dto.auth.LoginResponse;
import com.finmanagement.dto.auth.RegisterRequest;
import com.finmanagement.dto.common.ApiResponse;
import com.finmanagement.dto.user.UserResponse;

public interface AuthService {
    ApiResponse<UserResponse> register(RegisterRequest request);
    ApiResponse<LoginResponse> login(String username, String password);
}
