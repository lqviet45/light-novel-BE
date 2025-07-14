package com.lqviet.authservice.feign;

import com.lqviet.authservice.exceptions.UserServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserServiceErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        log.error("User service error - Method: {}, Status: {}, Reason: {}",
                methodKey, response.status(), response.reason());

        return switch (status) {
            case NOT_FOUND -> new UserServiceException("User not found", status);
            case BAD_REQUEST -> new UserServiceException("Invalid request to user service", status);
            case INTERNAL_SERVER_ERROR -> new UserServiceException("User service internal error", status);
            case SERVICE_UNAVAILABLE -> new UserServiceException("User service unavailable", status);
            default -> new UserServiceException("User service error: " + response.reason(), status);
        };
    }
}
