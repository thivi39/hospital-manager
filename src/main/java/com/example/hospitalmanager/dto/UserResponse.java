package com.example.hospitalmanager.dto;

import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Role;

public record UserResponse(
        Long id,
        String name,
        String username,
        Role role
) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getRole());
    }
}
