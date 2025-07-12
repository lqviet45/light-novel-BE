package com.lqviet.userservice.services.Impl;

import com.lqviet.userservice.dto.requests.PasswordUpdateRequest;
import com.lqviet.userservice.dto.requests.UserRegistrationRequest;
import com.lqviet.userservice.dto.requests.UserUpdateRequest;
import com.lqviet.userservice.dto.responses.PagedResponse;
import com.lqviet.userservice.dto.responses.UserResponse;
import com.lqviet.userservice.entities.Role;
import com.lqviet.userservice.entities.User;
import com.lqviet.userservice.enums.RoleName;
import com.lqviet.userservice.enums.UserStatus;
import com.lqviet.userservice.exceptions.*;
import com.lqviet.userservice.mapper.UserMapper;
import com.lqviet.userservice.repositories.RoleRepository;
import com.lqviet.userservice.repositories.UserRepository;
import com.lqviet.userservice.services.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRegistrationRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        validateUniqueConstraints(request.getEmail(), request.getUsername());

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(getDefaultRoles());
        user.setPasswordChangedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        log.debug("Retrieving user by ID: {}", userId);
        User user = findUserById(userId);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Retrieving user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.debug("Retrieving user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Retrieving all users with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> usersPage = userRepository.findAll(pageable);
        List<UserResponse> userResponses = userMapper.toResponseList(usersPage.getContent());

        return PagedResponse.<UserResponse>builder()
                .content(userResponses)
                .page(usersPage.getNumber())
                .size(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .first(usersPage.isFirst())
                .last(usersPage.isLast())
                .hasNext(usersPage.hasNext())
                .hasPrevious(usersPage.hasPrevious())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getUsersByStatus(UserStatus status, Pageable pageable) {
        log.debug("Retrieving users by status: {}", status);

        Page<User> usersPage = userRepository.findByStatus(status, pageable);
        List<UserResponse> userResponses = userMapper.toResponseList(usersPage.getContent());

        return PagedResponse.<UserResponse>builder()
                .content(userResponses)
                .page(usersPage.getNumber())
                .size(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .first(usersPage.isFirst())
                .last(usersPage.isLast())
                .hasNext(usersPage.hasNext())
                .hasPrevious(usersPage.hasPrevious())
                .build();
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = findUserById(id);

        // Validate and update email
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
            user.setEmailVerified(false); // Reset email verification when email changes
        }

        // Validate and update username
        if (StringUtils.hasText(request.getUsername()) && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException("Username already exists: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        // Update other fields
        if (StringUtils.hasText(request.getFirstName())) {
            user.setFirstName(request.getFirstName());
        }

        if (StringUtils.hasText(request.getLastName())) {
            user.setLastName(request.getLastName());
        }

        if (StringUtils.hasText(request.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (StringUtils.hasText(request.getBio())) {
            user.setBio(request.getBio());
        }

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void updatePassword(Long id, PasswordUpdateRequest request) {
        log.info("Updating password for user ID: {}", id);

        User user = findUserById(id);

        // Verify current password if provided
        if (StringUtils.hasText(request.getCurrentPassword())) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }
        }

        // Check if new password is different from current
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password updated successfully for user ID: {}", id);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        User user = findUserById(userId);

        // Soft delete by setting status to INACTIVE instead of hard delete
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        log.info("User soft deleted with ID: {}", userId);
    }

    @Override
    public void hardDeleteUser(Long userId) {
        log.warn("Hard deleting user with ID: {}", userId);

        User user = findUserById(userId);
        userRepository.delete(user);

        log.warn("User hard deleted with ID: {}", userId);
    }

    @Override
    public void updateUserStatus(Long userId, UserStatus status) {
        log.info("Updating user status for ID: {} to: {}", userId, status);

        int updatedRows = userRepository.updateUserStatus(userId, status);
        if (updatedRows == 0) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        log.info("User status updated successfully for ID: {}", userId);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        log.debug("Updating last login time for user ID: {}", userId);

        User user = findUserById(userId);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        Page<User> activePage = userRepository.findByStatus(UserStatus.ACTIVE, Pageable.unpaged());
        return activePage.getTotalElements();
    }

    @Override
    public void verifyEmail(Long userId) {
        log.info("Verifying email for user ID: {}", userId);

        User user = findUserById(userId);
        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("Email verified for user ID: {}", userId);
    }

    // Private helper methods
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    private void validateUniqueConstraints(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists: " + email);
        }

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already exists: " + username);
        }
    }

    private Set<Role> getDefaultRoles() {
        return roleRepository.findByNameIn(Set.of(RoleName.USER));
    }
}