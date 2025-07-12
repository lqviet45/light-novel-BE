package com.lqviet.userservice.services;

import com.lqviet.userservice.dto.requests.PasswordUpdateRequest;
import com.lqviet.userservice.dto.requests.UserRegistrationRequest;
import com.lqviet.userservice.dto.requests.UserUpdateRequest;
import com.lqviet.userservice.dto.responses.PagedResponse;
import com.lqviet.userservice.dto.responses.UserResponse;
import com.lqviet.userservice.enums.UserStatus;
import com.lqviet.userservice.exceptions.EmailAlreadyExistsException;
import com.lqviet.userservice.exceptions.InvalidPasswordException;
import com.lqviet.userservice.exceptions.UserNotFoundException;
import com.lqviet.userservice.exceptions.UsernameAlreadyExistsException;
import org.springframework.data.domain.Pageable;

public interface IUserService {

    /**
     * Creates a new user
     * @param request user registration data
     * @return created user information
     * @throws EmailAlreadyExistsException if email already exists
     * @throws UsernameAlreadyExistsException if username already exists
     */
    UserResponse createUser(UserRegistrationRequest request);

    /**
     * Retrieves user by ID
     * @param userId the user ID
     * @return user information
     * @throws UserNotFoundException if user not found
     */
    UserResponse getUserById(Long userId);

    /**
     * Retrieves user by email
     * @param email the user's email
     * @return user information
     * @throws UserNotFoundException if user not found
     */
    UserResponse getUserByEmail(String email);

    /**
     * Retrieves user by username
     * @param username the user's username
     * @return user information
     * @throws UserNotFoundException if user not found
     */
    UserResponse getUserByUsername(String username);

    /**
     * Retrieves all users with pagination
     * @param pageable pagination information
     * @return paginated list of users
     */
    PagedResponse<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Retrieves users by status with pagination
     * @param status the user status to filter by
     * @param pageable pagination information
     * @return paginated list of users with specified status
     */
    PagedResponse<UserResponse> getUsersByStatus(UserStatus status, Pageable pageable);

    /**
     * Updates user information
     * @param id user ID
     * @param request update data
     * @return updated user information
     * @throws UserNotFoundException if user not found
     * @throws EmailAlreadyExistsException if email already exists
     * @throws UsernameAlreadyExistsException if username already exists
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Updates user password
     * @param id user ID
     * @param request password update data
     * @throws UserNotFoundException if user not found
     * @throws InvalidPasswordException if current password is incorrect
     */
    void updatePassword(Long id, PasswordUpdateRequest request);

    /**
     * Soft deletes a user by setting status to INACTIVE
     * @param userId user ID to delete
     * @throws UserNotFoundException if user not found
     */
    void deleteUser(Long userId);

    /**
     * Hard deletes a user from database
     * @param userId user ID to delete
     * @throws UserNotFoundException if user not found
     */
    void hardDeleteUser(Long userId);

    /**
     * Updates user status
     * @param userId user ID
     * @param status new status
     * @throws UserNotFoundException if user not found
     */
    void updateUserStatus(Long userId, UserStatus status);

    /**
     * Updates last login time for user
     * @param userId user ID
     * @throws UserNotFoundException if user not found
     */
    void updateLastLoginTime(Long userId);

    /**
     * Checks if email exists
     * @param email email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if username exists
     * @param username username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Gets total user count
     * @return total number of users
     */
    long getTotalUserCount();

    /**
     * Gets active user count
     * @return number of active users
     */
    long getActiveUserCount();

    /**
     * Verifies user email
     * @param userId user ID
     * @throws UserNotFoundException if user not found
     */
    void verifyEmail(Long userId);
}