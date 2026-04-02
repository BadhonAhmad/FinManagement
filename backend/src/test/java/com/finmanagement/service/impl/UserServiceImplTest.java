package com.finmanagement.service.impl;

import com.finmanagement.dto.user.UserResponse;
import com.finmanagement.dto.user.UserUpdateRequest;
import com.finmanagement.entity.User;
import com.finmanagement.entity.enums.Role;
import com.finmanagement.exception.DuplicateResourceException;
import com.finmanagement.exception.ResourceNotFoundException;
import com.finmanagement.mapper.UserMapper;
import com.finmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserServiceImpl userService;

    private User createTestUser(Long id, String username, Role role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setRole(role);
        return user;
    }

    @Test
    void getUserById_found() {
        User user = createTestUser(1L, "testuser", Role.VIEWER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setUsername("testuser");
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getUserById(1L);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_notFound_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void updateUser_roleChange() {
        User user = createTestUser(1L, "testuser", Role.VIEWER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User savedUser = createTestUser(1L, "testuser", Role.ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setRole(Role.ADMIN);
        when(userMapper.toResponse(any(User.class))).thenReturn(response);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setRole(Role.ADMIN);

        UserResponse result = userService.updateUser(1L, request);
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    void deleteUser_softDelete() {
        User user = createTestUser(1L, "testuser", Role.VIEWER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).save(argThat(u -> u.isDeleted()));
    }

    @Test
    void getAllUsers_withSearch() {
        User user = createTestUser(1L, "testuser", Role.VIEWER);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.searchUsers(eq("test"), any(Pageable.class))).thenReturn(page);

        UserResponse response = new UserResponse();
        response.setUsername("testuser");
        when(userMapper.toResponseList(List.of(user))).thenReturn(List.of(response));

        var result = userService.getAllUsers(0, 10, "test");
        assertEquals(1, result.getContent().size());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }
}
