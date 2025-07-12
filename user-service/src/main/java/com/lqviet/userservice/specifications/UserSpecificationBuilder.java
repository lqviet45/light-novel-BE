package com.lqviet.userservice.specifications;

import com.lqviet.userservice.entities.User;
import com.lqviet.userservice.enums.UserStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Specification Builder for User entity
 * Compatible with Spring Data JPA 3.5.0+
 */
public class UserSpecificationBuilder {

    private final List<Specification<User>> specifications = new ArrayList<>();

    public static UserSpecificationBuilder builder() {
        return new UserSpecificationBuilder();
    }

    public UserSpecificationBuilder withKeyword(String keyword) {
        if (StringUtils.hasText(keyword)) {
            specifications.add(createKeywordSpecification(keyword));
        }
        return this;
    }

    public UserSpecificationBuilder withEmail(String email) {
        if (StringUtils.hasText(email)) {
            specifications.add(createEmailSpecification(email));
        }
        return this;
    }

    public UserSpecificationBuilder withUsername(String username) {
        if (StringUtils.hasText(username)) {
            specifications.add(createUsernameSpecification(username));
        }
        return this;
    }

    public UserSpecificationBuilder withFirstName(String firstName) {
        if (StringUtils.hasText(firstName)) {
            specifications.add(createFirstNameSpecification(firstName));
        }
        return this;
    }

    public UserSpecificationBuilder withLastName(String lastName) {
        if (StringUtils.hasText(lastName)) {
            specifications.add(createLastNameSpecification(lastName));
        }
        return this;
    }

    public UserSpecificationBuilder withStatus(UserStatus status) {
        if (status != null) {
            specifications.add(createStatusSpecification(status));
        }
        return this;
    }

    public UserSpecificationBuilder withEmailVerified(Boolean emailVerified) {
        if (emailVerified != null) {
            specifications.add(createEmailVerifiedSpecification(emailVerified));
        }
        return this;
    }

    public UserSpecificationBuilder withRoleName(String roleName) {
        if (StringUtils.hasText(roleName)) {
            specifications.add(createRoleNameSpecification(roleName));
        }
        return this;
    }

    public UserSpecificationBuilder withCreatedAfter(LocalDateTime date) {
        if (date != null) {
            specifications.add(createCreatedAfterSpecification(date));
        }
        return this;
    }

    public UserSpecificationBuilder withCreatedBefore(LocalDateTime date) {
        if (date != null) {
            specifications.add(createCreatedBeforeSpecification(date));
        }
        return this;
    }

    public UserSpecificationBuilder withLastLoginAfter(LocalDateTime date) {
        if (date != null) {
            specifications.add(createLastLoginAfterSpecification(date));
        }
        return this;
    }

    public Specification<User> build() {
        return specifications.stream()
                .reduce(Specification::and)
                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
    }

    // Private specification creation methods
    private static Specification<User> createKeywordSpecification(String keyword) {
        return (root, query, criteriaBuilder) -> {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern));

            // Safely handle nullable fields
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(
                    criteriaBuilder.coalesce(root.get("firstName"), criteriaBuilder.literal(""))), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(
                    criteriaBuilder.coalesce(root.get("lastName"), criteriaBuilder.literal(""))), likePattern));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<User> createEmailSpecification(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%");
    }

    private static Specification<User> createUsernameSpecification(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%");
    }

    private static Specification<User> createFirstNameSpecification(String firstName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(
                                criteriaBuilder.coalesce(root.get("firstName"), criteriaBuilder.literal(""))),
                        "%" + firstName.toLowerCase() + "%");
    }

    private static Specification<User> createLastNameSpecification(String lastName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(
                                criteriaBuilder.coalesce(root.get("lastName"), criteriaBuilder.literal(""))),
                        "%" + lastName.toLowerCase() + "%");
    }

    private static Specification<User> createStatusSpecification(UserStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<User> createEmailVerifiedSpecification(Boolean emailVerified) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("emailVerified"), emailVerified);
    }

    private static Specification<User> createRoleNameSpecification(String roleName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("roles").get("name").as(String.class),
                        roleName.toUpperCase());
    }

    private static Specification<User> createCreatedAfterSpecification(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    private static Specification<User> createCreatedBeforeSpecification(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }

    private static Specification<User> createLastLoginAfterSpecification(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("lastLoginAt"), date);
    }
}