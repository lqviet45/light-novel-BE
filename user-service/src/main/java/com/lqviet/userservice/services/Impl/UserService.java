package com.lqviet.userservice.services.Impl;

import com.lqviet.userservice.dto.requests.PasswordUpdateRequest;
import com.lqviet.userservice.dto.requests.UserRegistrationRequest;
import com.lqviet.userservice.dto.requests.UserUpdateRequest;
import com.lqviet.userservice.dto.responses.UserResponse;
import com.lqviet.userservice.entities.Role;
import com.lqviet.userservice.entities.User;
import com.lqviet.userservice.enums.RoleName;
import com.lqviet.userservice.exceptions.EmailAlreadyExistsException;
import com.lqviet.userservice.exceptions.InvalidPasswordException;
import com.lqviet.userservice.exceptions.UserNotFoundException;
import com.lqviet.userservice.exceptions.UsernameAlreadyExistsException;
import com.lqviet.userservice.mapper.UserMapper;
import com.lqviet.userservice.repositories.RoleRepository;
import com.lqviet.userservice.repositories.UserRepository;
import com.lqviet.userservice.services.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public UserResponse createUser(UserRegistrationRequest request) {
        validateUniqueConstraints(request.getEmail(), request.getUsername());

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRoles(getDefaultRoles());

        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = findUserById(userId);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findUserById(id);

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException("Username already exists: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    public void updatePassword(Long id, PasswordUpdateRequest request) {
        User user = findUserById(id);

        if (request.getCurrentPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password updated for user ID: {}", id);
    }

    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
        log.info("User deleted with ID: {}", userId);
    }


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
