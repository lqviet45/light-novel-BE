package com.lqviet.userservice.services;

import com.lqviet.userservice.dto.responses.PagedResponse;
import com.lqviet.userservice.dto.responses.UserResponse;
import com.lqviet.userservice.enums.UserStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IUserSearchService {

    /**
     * Advanced search for users with multiple filters
     * @param keyword general keyword to search in username, email, firstName, lastName
     * @param email email filter
     * @param username username filter
     * @param firstName first name filter
     * @param lastName last name filter
     * @param status user status filter
     * @param emailVerified email verification status filter
     * @param roleName role name filter
     * @param createdAfter created after date filter
     * @param createdBefore created before date filter
     * @param lastLoginAfter last login after date filter
     * @param pageable pagination information
     * @return paginated search results
     */
    PagedResponse<UserResponse> searchUsers(
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
            Pageable pageable);

    /**
     * Quick search for users by keyword
     * @param keyword search keyword
     * @param pageable pagination information
     * @return paginated search results
     */
    PagedResponse<UserResponse> quickSearch(String keyword, Pageable pageable);
}