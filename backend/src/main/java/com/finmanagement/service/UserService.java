package com.finmanagement.service;

import com.finmanagement.dto.common.PagedResponse;
import com.finmanagement.dto.user.UserResponse;
import com.finmanagement.dto.user.UserUpdateRequest;

public interface UserService {
    PagedResponse<UserResponse> getAllUsers(int page, int size, String search);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}
