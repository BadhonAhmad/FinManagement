package com.finmanagement.dto.user;

import com.finmanagement.entity.enums.Role;
import jakarta.validation.constraints.Email;

public class UserUpdateRequest {

    @Email(message = "Email must be valid")
    private String email;

    private Role role;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
