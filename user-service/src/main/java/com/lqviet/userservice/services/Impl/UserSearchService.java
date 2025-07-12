package com.lqviet.userservice.services.Impl;

import com.lqviet.userservice.dto.responses.PagedResponse;
import com.lqviet.userservice.dto.responses.UserResponse;
import com.lqviet.userservice.entities.User;
import com.lqviet.userservice.enums.UserStatus;
import com.lqviet.userservice.mapper.UserMapper;
import com.lqviet.userservice.repositories.UserRepository;
import com.lqviet.userservice.services.IUserSearchService;
import com.lqviet.userservice.specifications.UserSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class UserSearchService implements IUserSearchService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public PagedResponse<UserResponse> searchUsers(
            String keyword,
            String email,
            String username,
            String firstName,
            String lastName,
            UserStatus status,
            Boolean emailVerified,
            String roleName,
            LocalDateTime createdAfter,
            LocalDateTime createdBefore,
            LocalDateTime lastLoginAfter,
            Pageable pageable) {

        log.debug("Performing advanced user search with multiple filters");

        // Use modern specification builder pattern
        Specification<User> specification = UserSpecificationBuilder.builder()
                .withKeyword(keyword)
                .withEmail(email)
                .withUsername(username)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withStatus(status)
                .withEmailVerified(emailVerified)
                .withRoleName(roleName)
                .withCreatedAfter(createdAfter)
                .withCreatedBefore(createdBefore)
                .withLastLoginAfter(lastLoginAfter)
                .build();

        Page<User> usersPage = userRepository.findAll(specification, pageable);
        List<UserResponse> userResponses = userMapper.toResponseList(usersPage.getContent());

        log.debug("Found {} users matching search criteria", usersPage.getTotalElements());

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
    public PagedResponse<UserResponse> quickSearch(String keyword, Pageable pageable) {
        log.debug("Performing quick search for keyword: {}", keyword);

        // Use builder pattern for quick search
        Specification<User> specification = UserSpecificationBuilder.builder()
                .withKeyword(keyword)
                .build();

        Page<User> usersPage = userRepository.findAll(specification, pageable);
        List<UserResponse> userResponses = userMapper.toResponseList(usersPage.getContent());

        log.debug("Quick search found {} users", usersPage.getTotalElements());

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
}