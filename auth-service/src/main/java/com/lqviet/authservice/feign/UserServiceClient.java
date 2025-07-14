package com.lqviet.authservice.feign;

import com.lqviet.authservice.dtos.external.ExternalApiResponse;
import com.lqviet.authservice.dtos.external.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(
        name = "user-service",
        url = "${user-service.url}",
        configuration = UserServiceFeignConfig.class
)
public interface UserServiceClient {
    @GetMapping("${user-service.endpoints.get-user-by-email}")
    ExternalApiResponse<UserResponse> getUserByEmail(@PathVariable("email") String email);

    @GetMapping("${user-service.endpoints.get-user-by-id}")
    ExternalApiResponse<UserResponse> getUserById(@PathVariable("id") Long id);

    @PutMapping("${user-service.endpoints.update-last-login}")
    ExternalApiResponse<Void> updateLastLoginTime(@PathVariable("id") Long id);
}
