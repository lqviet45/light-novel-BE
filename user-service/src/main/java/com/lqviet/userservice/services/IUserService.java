package com.lqviet.userservice.services;

import com.lqviet.userservice.dto.requests.PasswordUpdateRequest;
import com.lqviet.userservice.dto.requests.UserRegistrationRequest;
import com.lqviet.userservice.dto.requests.UserUpdateRequest;
import com.lqviet.userservice.dto.responses.UserResponse;

public interface IUserService {
    /**
     * Creates a new user
     * @param request user registration data
     * @return created user information
     */
    UserResponse createUser(UserRegistrationRequest request);

    /**
     * Retrieves user by ID
     * @param userId the user ID
     * @return user information
     */
    UserResponse getUserById(Long userId);

    /**
     * Retrieves user by email
     * @param email the user's email
     * @return user information
     */
    UserResponse getUserByEmail(String email);

    /**
     * Updates user information
     * @param id user ID
     * @param request update data
     * @return updated user information
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Updates user password
     * @param id user ID
     * @param request password update data
     */
    void updatePassword(Long id, PasswordUpdateRequest request);

    /**
     * Deletes a user
     * @param userId user ID to delete
     */
    void deleteUser(Long userId);
}